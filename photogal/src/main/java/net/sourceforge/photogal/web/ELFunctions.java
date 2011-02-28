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
package net.sourceforge.photogal.web;

import java.awt.Dimension;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.photogal.ImageDescriptor;
import net.sourceforge.photogal.image.ScaledImageCalculator;
import net.sourceforge.photogal.web.controller.ControllerUtils;

public class ELFunctions {
    private ELFunctions() {
    }

    public static Dimension getScaledSize(ScaledImageCalculator calculator, Dimension originalSize,
            String size) {
        return calculator.getScaledSize(originalSize, size);
    }

    public static boolean isAvailableSize(ScaledImageCalculator calculator,
            ImageDescriptor descriptor, String size) {
        return calculator.isAvailableSize(size, descriptor.getWidth(), descriptor.getHeight());
    }

    public static boolean canEdit(HttpServletRequest request) {
        return ControllerUtils.canEdit(request);
    }

    /**
     * Returns the number of pages in a data set.
     * 
     * @param itemCount the number of items in the set
     * @param itemsPerPage the max number of items displayed per page
     * @return the number of pages in the set
     */
    public static int pageCount(int itemCount, int itemsPerPage) {
        return itemCount / itemsPerPage + (itemCount % itemsPerPage == 0 ? 0 : 1);
    }

    /**
     * Returns a normalized file expression. Replaces all backslash file
     * seperator characters with forward slash characters.
     * 
     * @param path the path to normalize
     * @return the normalized path
     */
    public static String normalizedPath(String path) {
        if (path == null) {
            return null;
        }
        return path.replace('\\', '/');
    }
}
