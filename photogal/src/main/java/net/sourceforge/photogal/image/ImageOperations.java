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
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

// TODO move these methods to ImageUtils
public class ImageOperations {
    private static final float DEFAULT_SOFTEN_FACTOR = 0.01f;

    public void scaleImage(File source, File destination, int maxside) throws IOException {
        ImageIO.setUseCache(false);
        BufferedImage bsrc = ImageIO.read(source);

        double ratio = (double) maxside / Math.max(bsrc.getHeight(), bsrc.getWidth());
        int width = (int) (bsrc.getWidth() * ratio);
        int height = (int) (bsrc.getHeight() * ratio);

        BufferedImage bdest = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bdest.createGraphics();
        AffineTransform at = AffineTransform.getScaleInstance(ratio, ratio);
        g.drawRenderedImage(bsrc, at);
        ImageIO.write(bdest, "JPG", destination);

        bsrc.flush();
        bdest.flush();
        g.dispose();
    }

    // The idea for this implementation came from
    // http://blogs.cocoondev.org/mpo/archives/003584.html
    public static BufferedImage resize(BufferedImage image, double scale_factor) {
        if (scale_factor <= 0.0) {
            throw new IllegalArgumentException("scale factor must be > 0");
        }

        final BufferedImage retval = createCompatibleDestImage(image, scale_factor);
        Image i = image
                .getScaledInstance(retval.getWidth(), retval.getHeight(), Image.SCALE_SMOOTH);
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
     * @param image the image
     * @param maxSize the maximum allowed value for the height and width
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
        float[] kernelData = { 1, 4, 7, 4, 1, 4, 16, 26, 16, 4, 7, 26, 41, 26, 7, 4, 16, 26, 16, 4,
                1, 4, 7, 4, 1 };
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

    public static BufferedImage soften(final BufferedImage image, final float softenFactor) {
        float[] kernelData = { 0, softenFactor, 0, softenFactor, 1 - (softenFactor * 4),
                softenFactor, 0, softenFactor, 0 };
        Kernel kernel = new Kernel(3, 3, kernelData);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, createCompatibleDestImage(image));
    }

    public static BufferedImage sharpen(final BufferedImage image) {
        float[] kernelData = { 0.0f, -1.0f, 0.0f, -1.0f, 5.f, -1.0f, 0.0f, -1.0f, 0.0f };
        Kernel kernel = new Kernel(3, 3, kernelData);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(image, createCompatibleDestImage(image));
    }

    /**
     * Creates a destination image.
     * 
     * @param source the BufferedImage to be transformed
     * @return the destination image
     */
    private static BufferedImage createCompatibleDestImage(BufferedImage source) {
        return createCompatibleDestImage(source, 1.0);
    }

    /**
     * Creates a destination image scaled to the appropriate size.
     * 
     * @param source the BufferedImage to be transformed
     * @param scale_factor the factor by which to size the destination image
     * @return the destination image
     */
    private static BufferedImage createCompatibleDestImage(BufferedImage source, double scale_factor) {
        int scaledWidth = (int) (source.getWidth() * scale_factor);
        int scaledHeight = (int) (source.getHeight() * scale_factor);
        BufferedImage retval = new BufferedImage(scaledWidth, scaledHeight, source.getType());
        return retval;
    }
}
