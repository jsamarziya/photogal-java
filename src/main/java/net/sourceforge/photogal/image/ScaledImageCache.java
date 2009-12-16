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
import java.util.Map;

import org.sixcats.utils.image.ImageScaler;
import org.sixcats.utils.image.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Manages the cache of scaled image files.
 */
public class ScaledImageCache implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScaledImageCache.class);
    private File cacheDirectory;
    private ScaledImageCalculator scaledImageCalculator;
    private ImageScaler imageScaler;

    /**
     * Returns the cache directory.
     * 
     * @return the cache directory
     */
    public File getCacheDirectory() {
        return cacheDirectory;
    }

    /**
     * Sets the cache directory.
     * 
     * @param directory the cache directory
     */
    public void setCacheDirectory(final File directory) {
        cacheDirectory = directory;
    }

    /**
     * Returns the scaled image calculator.
     * 
     * @return the scaled image calculator
     */
    public ScaledImageCalculator getScaledImageCalculator() {
        return scaledImageCalculator;
    }

    /**
     * Sets the scaled image calculator.
     * 
     * @param calculator the scaled image calculator
     */
    public void setScaledImageCalculator(final ScaledImageCalculator calculator) {
        scaledImageCalculator = calculator;
    }

    /**
     * Returns the image scaler.
     * 
     * @return the image scaler
     */
    public ImageScaler getImageScaler() {
        return imageScaler;
    }

    /**
     * Sets the image scaler.
     * 
     * @param scaler the image scaler
     */
    public void setImageScaler(ImageScaler scaler) {
        imageScaler = scaler;
    }

    /**
     * Returns <code>true</code> if this cache contains the specified scaled
     * image file.
     * 
     * @param imageFile the original image file
     * @param sizeId the side of the scaled image
     * @return <code>true</code> if this cache contains the specified scaled
     *         instance, <code>false</code> otherwise
     */
    public boolean hasScaledImage(final File imageFile, final String sizeId) {
        return getScaledImageFile(imageFile, sizeId).exists();
    }

    /**
     * Returns the file containing the scaled instance of the specified image,
     * creating it if necessary.
     * 
     * @param imageFile the original image file
     * @param sizeId the size of the scaled image
     * @return the scaled image file
     */
    public File getScaledImage(final File imageFile, final String sizeId) throws IOException {
        File scaledImage = getScaledImageFile(imageFile, sizeId);
        if (scaledImage.exists()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found cached image " + scaledImage);
            }
        } else {
            createScaledImages(imageFile);
        }
        return scaledImage;
    }

    /**
     * Returns the file containing the scaled instance of the specified image.
     * 
     * @param imageFile the image file
     * @param sizeId the size of the scaled image
     * @return the file containing the scaled image
     */
    private File getScaledImageFile(final File imageFile, final String sizeId) {
        final File cacheDir = new File(getCacheDirectory(), sizeId);
        final File scaledImage = new File(cacheDir, imageFile.getPath());
        return scaledImage;
    }

    /**
     * Creates scaled instances of the specified image.
     * 
     * @param originalImageFile the image file
     * @throws IOException if an I/O error occurs
     */
    private void createScaledImages(File originalImageFile) throws IOException {
        final BufferedImage originalImage = ImageUtils.readImage(originalImageFile);
        if (originalImage == null) {
            throw new IOException("unable to read original image file "
                    + originalImageFile.getPath());
        }
        final Dimension originalSize = new Dimension(originalImage.getWidth(), originalImage
                .getHeight());
        final Map<String, Integer> sizes = getScaledImageCalculator().getMaxSizes();
        for (Map.Entry<String, Integer> sizeEntry : sizes.entrySet()) {
            final File scaledImageFile = getScaledImageFile(originalImageFile, sizeEntry.getKey());
            if (scaledImageFile.exists()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Cached image " + scaledImageFile + " exists, skipping scaling");
                }
            } else {
                if (!scaledImageFile.getParentFile().exists()) {
                    scaledImageFile.getParentFile().mkdirs();
                }
                final double scaleFactor = ImageUtils.getScaleFactor(originalSize, sizeEntry
                        .getValue());
                final BufferedImage scaledImage = getImageScaler().resize(originalImage,
                        scaleFactor);
                ImageUtils.saveAsJPEG(scaledImage, scaledImageFile);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Created cached image " + scaledImageFile);
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(getCacheDirectory(), "a cache directory must be set");
        Assert.notNull(getScaledImageCalculator(), "a scaled image calculator must be set");
        Assert.notNull(getImageScaler(), "an image scaler must be set");
    }
}
