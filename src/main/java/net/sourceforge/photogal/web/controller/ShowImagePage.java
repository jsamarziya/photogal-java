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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.photogal.Gallery;
import net.sourceforge.photogal.ImageDescriptor;
import net.sourceforge.photogal.image.ScaledImageCalculator;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ShowImagePage extends AbstractController {
    private ScaledImageCalculator scaledImageCalculator;

    public ScaledImageCalculator getScaledImageCalculator() {
        return scaledImageCalculator;
    }

    public void setScaledImageCalculator(
            ScaledImageCalculator scaledImageCalculator) {
        this.scaledImageCalculator = scaledImageCalculator;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final Map<String, Object> model = new HashMap<String, Object>();
        final ImageDescriptor image = ControllerUtils
                .getImageDescriptor(request);
        if (!image.isPublic() && !ControllerUtils.canViewPrivate(request)) {
            throw new ServletException("image is private");
        }
        model.put("image", image);
        model.put("scaledImageCalculator", getScaledImageCalculator());

        if (ControllerUtils.hasGalleryId(request)) {
            final Gallery gallery = ControllerUtils.getGallery(request);
            final List<ImageDescriptor> galleryImages = gallery.getImages();
            final int position = galleryImages.indexOf(image);
            ImageDescriptor previousImage = null;
            ImageDescriptor nextImage = null;
            if (position > 0) {
                previousImage = galleryImages.get(position - 1);
            }
            if (position != -1 && position + 1 < galleryImages.size()) {
                nextImage = galleryImages.get(position + 1);
            }
            model.put("gallery", gallery);
            model.put("imageIndex", position);
            model.put("previousImage", previousImage);
            model.put("nextImage", nextImage);
        }
        return new ModelAndView("imagePage", model);
    }
}
