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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifDirectory;

public class ImageMetadataUtils {
    /**
     * Returns the date a JPEG image was created.
     * 
     * @param jpegFile the file containing the JPEG image
     * @return the date the image was created, or <code>null</code> if the
     * image does not contain creation date information
     * @throws JpegProcessingException if an error occurs
     * @throws MetadataException if an error occurs
     */
    public static final Date getImageDate(final File jpegFile)
            throws JpegProcessingException, MetadataException {
        Metadata metadata = JpegMetadataReader.readMetadata(jpegFile);
        ExifDirectory directory = (ExifDirectory) metadata
                .getDirectory(ExifDirectory.class);
        Date retval = null;
        if (directory.containsTag(ExifDirectory.TAG_DATETIME)) {
            retval = directory.getDate(ExifDirectory.TAG_DATETIME);
        }
        return retval;
    }

    /**
     * Returns the size of an image.
     * 
     * @param file the file containing the image
     * @return the size of the specified image
     * @throws IOException if an I/O error occurs
     */
    public static Dimension getSize(final File file) throws IOException {
        BufferedImage image = ImageOperations.getImage(file);
        return new Dimension(image.getWidth(), image.getHeight());
    }
}
