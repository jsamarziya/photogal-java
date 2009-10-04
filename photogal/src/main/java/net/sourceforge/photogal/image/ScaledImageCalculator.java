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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.sixcats.utils.image.ImageUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Provides services used to relate symbolic image sizes to size constraints.
 */
public class ScaledImageCalculator implements InitializingBean {
    private Map<String, Integer> maxSizes;

    /**
     * Returns a mapping of size names to maximum dimensions.
     * 
     * @return a map containing the maximum image dimensions, mapped by size
     *         name
     */
    public Map<String, Integer> getMaxSizes() {
        return maxSizes == null ? null : Collections.unmodifiableMap(maxSizes);
    }

    /**
     * Sets the maximum size map.
     * 
     * @param sizes the map of the maximum image dimensions, mapped by size name
     */
    public void setMaxSizes(Map<String, Integer> sizes) {
        maxSizes = sizes;
    }

    /**
     * Returns <code>true</code> if at least one of the specified dimensions is
     * greater than the maximum size for the specified image size name.
     * 
     * @param size the size name
     * @param imageWidth the width of the image
     * @param imageHeight the height of the image
     * @return <code>true</code> if either imageWidth or imageHeight is greater
     *         than the maximum size, <code>false</code> otherwise
     */
    public boolean isAvailableSize(final String size, final int imageWidth, final int imageHeight) {
        final int maxSize = getMaxSize(size);
        return imageWidth > maxSize || imageHeight > maxSize;
    }

    /**
     * Returns the available sizes for an image of the specified size.
     * 
     * @param imageWidth the width of the image
     * @param imageHeight the height of the image
     * @return a Map that maps each size code to a boolean that indicates
     *         whether or not that size code is valid for the given image size
     */
    public Map<String, Boolean> getAvailableSizes(int imageWidth, int imageHeight) {
        Map<String, Boolean> retval = new HashMap<String, Boolean>();
        for (String size : getMaxSizes().keySet()) {
            retval.put(size, isAvailableSize(size, imageWidth, imageHeight));
        }
        return retval;
    }

    /**
     * Returns the maximum dimension for the specified size.
     * 
     * @param size the name of the size
     * @return the maximum dimension
     */
    public int getMaxSize(String size) {
        final Integer retval = getMaxSizes().get(size);
        if (retval == null) {
            throw new IllegalArgumentException("unknown size " + size);
        }
        return retval;
    }

    /**
     * Returns the scaled size of an image that has the specified dimensions,
     * for the specified size.
     * 
     * @param d the image dimensions
     * @param size the size name
     * 
     * @return the scaled size
     */
    public Dimension getScaledSize(Dimension d, String size) {
        final int maxSize = getMaxSize(size);
        final double scaleFactor = ImageUtils.getScaleFactor(d, maxSize);
        return new Dimension((int) Math.round(d.width * scaleFactor), (int) Math.round(d.height
                * scaleFactor));
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(getMaxSizes(), "a maxSizes map must be set");
    }
}
