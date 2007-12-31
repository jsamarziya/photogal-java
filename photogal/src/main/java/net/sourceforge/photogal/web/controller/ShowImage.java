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
package net.sourceforge.photogal.web.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.photogal.ImageDescriptor;

import org.springframework.web.servlet.ModelAndView;

/**
 * Serves requests for images.
 */
public class ShowImage extends AbstractShowImageController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final ImageDescriptor descriptor = ControllerUtils
                .getImageDescriptor(request);
        if (!descriptor.isPublic() && !ControllerUtils.canViewPrivate(request)) {
            throw new ServletException("image is private");
        }
        final String size = request.getParameter("size");
        if (size == null) {
            throw new ServletException("size not specified");
        }
        logger.debug("Serving image #" + descriptor.getId() + ", size " + size);
        final String filename = descriptor.getLocation();
        renderImage(filename, size, response);
        return null;
    }
}