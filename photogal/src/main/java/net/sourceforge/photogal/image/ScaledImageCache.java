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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaledImageCache {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private File cacheDirectory;
    private ScaledImageCalculator scaledImageCalculator;

    public File getCacheDirectory() {
        return cacheDirectory;
    }

    public void setCacheDirectory(final File directory) {
        cacheDirectory = directory;
    }

    public ScaledImageCalculator getScaledImageCalculator() {
        return scaledImageCalculator;
    }

    public void setScaledImageCalculator(final ScaledImageCalculator calculator) {
        scaledImageCalculator = calculator;
    }

    private File getScaledImageFile(final File imageFile, final String sizeId) {
        final File cacheDir = new File(getCacheDirectory(), sizeId);
        final File scaledImage = new File(cacheDir, imageFile.getPath());
        return scaledImage;
    }

    public boolean hasScaledImage(final File imageFile, final String sizeId) {
        return getScaledImageFile(imageFile, sizeId).exists();
    }

    public File getScaledImage(final File imageFile, final String sizeId)
            throws IOException {
        File scaledImage = getScaledImageFile(imageFile, sizeId);
        if (scaledImage.exists()) {
            log.debug("Found cached image " + scaledImage);
        } else {
            createAndSaveScaledImage(imageFile, scaledImage, sizeId);
            log.debug("Created cached image " + scaledImage);
        }
        return scaledImage;
    }

    private void createAndSaveScaledImage(File originalImageFile,
            File scaledImageFile, String sizeId) throws IOException {
        BufferedImage originalImage = ImageOperations
                .getImage(originalImageFile);
        if (originalImage == null) {
            throw new IOException("unable to read original image file "
                + originalImageFile.getPath());
        }
        BufferedImage scaledImage = createScaledImage(originalImage, sizeId);
        if (!scaledImageFile.getParentFile().exists()) {
            scaledImageFile.getParentFile().mkdirs();
        }
        ImageOperations.saveAsJPEG(scaledImage, scaledImageFile);
    }

    protected BufferedImage createScaledImage(final BufferedImage image,
            final String sizeId) {
        return ImageOperations.limitSize(image, getScaledImageCalculator()
                .getMaxSize(sizeId));
    }
}
