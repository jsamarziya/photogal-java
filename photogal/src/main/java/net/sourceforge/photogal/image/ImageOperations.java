/*
 *  Copyright 2007 The Photogal Team.
 *  
 *  This file is part of photogal.
 *
 *  photogal is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  photogal is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with photogal.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.photogal.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.MetadataException;

public class ImageOperations {
    private static final Logger log = LoggerFactory
            .getLogger(ImageOperations.class);
    private static final float DEFAULT_SOFTEN_FACTOR = 0.01f;

    // The idea for this implementation came from
    // http://blogs.cocoondev.org/mpo/archives/003584.html
    public static BufferedImage resize(BufferedImage image, double scale_factor) {
        if (scale_factor <= 0.0) {
            throw new IllegalArgumentException("scale factor must be > 0");
        }

        final BufferedImage retval = createCompatibleDestImage(image,
                scale_factor);
        Image i = image.getScaledInstance(retval.getWidth(),
                retval.getHeight(), Image.SCALE_SMOOTH);
        // This ensures that all the pixels in the image are loaded.
        i = new ImageIcon(i).getImage();

        // Copy image to buffered image
        Graphics g = retval.createGraphics();
        // Clear background and paint the image.
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, retval.getWidth(), retval.getHeight());
        g.drawImage(i, 0, 0, null);
        g.dispose();
        g = null;
        return retval;
    }

    /**
     * Resizes an image so that the height and width of the image do not exceed
     * the specified size.
     * 
     * @param image
     *            the image
     * @param maxSize
     *            the maximum allowed value for the height and width
     * @return the image, resized as necessary
     */
    public static BufferedImage limitSize(BufferedImage image, int maxSize) {
        BufferedImage retval;
        int maxDimension = Math.max(image.getHeight(), image.getWidth());
        if (maxDimension <= maxSize) {
            retval = image;
        } else {
            retval = resize(image, (double) maxSize / maxDimension);
        }
        return retval;
    }

    public static BufferedImage smooth(final BufferedImage image) {
        float[] kernelData = { 1, 4, 7, 4, 1, 4, 16, 26, 16, 4, 7, 26, 41, 26,
                7, 4, 16, 26, 16, 4, 1, 4, 7, 4, 1 };
        int total = 0;
        for (int i = 0; i < kernelData.length; i++) {
            total += kernelData[i];
        }
        for (int i = 0; i < kernelData.length; i++) {
            kernelData[i] /= total;
        }
        Kernel kernel = new Kernel(5, 5, kernelData);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, createCompatibleDestImage(image));
    }

    public static BufferedImage soften(final BufferedImage image) {
        return soften(image, DEFAULT_SOFTEN_FACTOR);
    }

    public static BufferedImage soften(final BufferedImage image,
            final float softenFactor) {
        float[] kernelData = { 0, softenFactor, 0, softenFactor,
                1 - (softenFactor * 4), softenFactor, 0, softenFactor, 0 };
        Kernel kernel = new Kernel(3, 3, kernelData);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, createCompatibleDestImage(image));
    }

    public static BufferedImage sharpen(final BufferedImage image) {
        float[] kernelData = { 0.0f, -1.0f, 0.0f, -1.0f, 5.f, -1.0f, 0.0f,
                -1.0f, 0.0f };
        Kernel kernel = new Kernel(3, 3, kernelData);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, createCompatibleDestImage(image));
    }

    /**
     * Saves an image.
     * 
     * @param image
     *            the image to save
     * @param output
     *            an <code>Object</code> to be used as an output destination,
     *            such as a <code>File</code>, writable
     *            <code>RandomAccessFile</code>, or <code>OutputStream</code>
     * @param writer
     *            the ImageWriter to use to write the image
     * @param writeParam
     *            the write param to use when writing the image
     * @throws IOException
     * @throws IOException
     *             if an I/O error occurs
     */
    private static void saveImage(final BufferedImage image,
            final Object output, final ImageWriter writer,
            final ImageWriteParam writeParam) throws IOException {
        ImageOutputStream ios = null;
        try {
            ios = ImageIO.createImageOutputStream(output);
            writer.setOutput(ios);
            IIOImage ioImage = new IIOImage(image, null, null);
            writer.write(null, ioImage, writeParam);
        } finally {
            if (writer != null) {
                writer.dispose();
                if (ios != null) {
                    ios.close();
                }
            }
        }

    }

    /**
     * Saves an image as a JPEG.
     * 
     * @param image
     *            the image to save
     * @param output
     *            an <code>Object</code> to be used as an output destination,
     *            such as a <code>File</code>, writable
     *            <code>RandomAccessFile</code>, or <code>OutputStream</code>
     * @throws IOException
     *             if an I/O error occurs
     */
    public static void saveAsJPEG(final BufferedImage image, final Object output)
            throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        writeParam.setCompressionQuality(0.75f);
        saveImage(image, output, writer, writeParam);
    }

    /**
     * Saves an image as a PNG.
     * 
     * @param image
     *            the image to save
     * @param output
     *            an <code>Object</code> to be used as an output destination,
     *            such as a <code>File</code>, writable
     *            <code>RandomAccessFile</code>, or <code>OutputStream</code>
     * @throws IOException
     *             if an I/O error occurs
     */
    public static void saveAsPNG(final BufferedImage image, final Object output)
            throws IOException {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
        ImageWriteParam writeParam = writer.getDefaultWriteParam();
        saveImage(image, output, writer, writeParam);
    }

    /**
     * Creates a destination image.
     * 
     * @param source
     *            the BufferedImage to be transformed
     * @return the destination image
     */
    private static BufferedImage createCompatibleDestImage(BufferedImage source) {
        return createCompatibleDestImage(source, 1.0);
    }

    /**
     * Creates a destination image scaled to the appropriate size.
     * 
     * @param source
     *            the BufferedImage to be transformed
     * @param scale_factor
     *            the factor by which to size the destination image
     * @return the destination image
     */
    private static BufferedImage createCompatibleDestImage(
            BufferedImage source, double scale_factor) {
        int scaledWidth = (int) (source.getWidth() * scale_factor);
        int scaledHeight = (int) (source.getHeight() * scale_factor);
        BufferedImage retval = new BufferedImage(scaledWidth, scaledHeight,
                source.getType());
        return retval;
    }

    public static BufferedImage getImage(final File file) throws IOException {
        log.debug("Fetching image " + file.getPath());
        if (!file.exists()) {
            throw new FileNotFoundException(file.getPath());
        }
        BufferedImage retval = ImageIO.read(file);
        return retval;
    }

    /**
     * Returns true if the given file is a JPEG file.
     * 
     * @param file
     *            the file
     * @return <code>true</code> if the file is a JPEG file, <code>false</code>
     *         otherwise
     * @throws IOException
     *             if an I/O error occurs
     */
    public static boolean isJPEG(final File file) throws IOException {
        boolean retval = false;
        if (file.isFile()) {
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                retval = in.read() == 255 && in.read() == 216;
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
        return retval;
    }

    /**
     * Returns true if the given file is an image file.
     * 
     * @param file
     *            the file
     * @return <code>true</code> if the file is an image file,
     *         <code>false</code> otherwise
     * @throws IOException
     *             if an I/O error occurs
     */
    public static boolean isImage(final File file) throws IOException {
        return isJPEG(file);
    }

    /**
     * Returns a collection of the image files contained in the specified
     * directory.
     * 
     * @param directory
     *            the directory
     * @return a collection of image files contained in the directory
     * @throws IOException
     *             if an I/O error occurs
     */
    public static Collection<File> getImageFiles(File directory)
            throws IOException {
        ArrayList<File> retval = new ArrayList<File>();
        if (!directory.isDirectory()) {
            throw new IOException("Unable to list files: " + directory
                    + " is not a directory");
        }
        if (!directory.canRead()) {
            throw new IOException("Unable to list files: " + directory
                    + " not readable");
        }
        File[] files = directory.listFiles();
        if (files == null) {
            throw new IOException("Unable to list files in " + directory);
        }
        for (File file : files) {
            if (file.canRead() && isImage(file)) {
                retval.add(file);
            }
        }
        return retval;
    }

    /**
     * Returns the date that the specified image was created.
     * 
     * @param imageFile
     *            the file that contains the image
     * @return the date the image was created, or <code>null</code> if the image
     *         creation date cannot be determined
     */
    public static Date getImageCreationDate(final File imageFile) {
        Date retval = null;
        try {
            if (isJPEG(imageFile)) {
                retval = ImageMetadataUtils.getImageDate(imageFile);
            }
        } catch (IOException ex) {
            log.warn("unable to determine if file is a JPEG", ex);
        } catch (JpegProcessingException ex) {
            log.warn("unable to process JPEG file", ex);
        } catch (MetadataException ex) {
            log.warn("unable to read JPEG metadata", ex);
        }
        return retval;
    }
}
