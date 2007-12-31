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

public class ScaledImageCalculator {
    private Map<String, Integer> maxSizes;

    public void setMaxSizes(Map<String, Integer> sizes) {
        maxSizes = sizes;
    }

    public Map<String, Integer> getMaxSizes() {
        return Collections.unmodifiableMap(maxSizes);
    }

    public boolean isAvailableSize(final String size, final int imageWidth,
            final int imageHeight) {
        final int maxSize = getMaxSize(size);
        return imageWidth > maxSize || imageHeight > maxSize;
    }

    public Map<String, Boolean> getAvailableSizes(int imageWidth,
            int imageHeight) {
        Map<String, Boolean> retval = new HashMap<String, Boolean>();
        for (String size : getMaxSizes().keySet()) {
            retval.put(size, isAvailableSize(size, imageWidth, imageHeight));
        }
        return retval;
    }

    public int getMaxSize(String size) {
        return getMaxSizes().get(size);
    }

    public Dimension getScaledSize(Dimension d, String size) {
        Dimension scaled = new Dimension(d);
        int maxSize = getMaxSize(size);
        int maxDimension = Math.max(d.height, d.width);
        if (maxDimension > maxSize) {
            double scaleFactor = (double) maxSize / maxDimension;
            scaled.height *= scaleFactor;
            scaled.width *= scaleFactor;
        }
        return scaled;
    }
}
