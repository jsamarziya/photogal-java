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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.photogal.Gallery;
import net.sourceforge.photogal.ImageDescriptor;

import org.springframework.web.servlet.ModelAndView;

public class DeleteImage extends PhotogalDaoAwareController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String action = request.getParameter("action");
        if (action == null) {
            return handleConfirmRequest(request);
        } else if ("delete".equals(action)) {
            return handleDeleteRequest(request);
        } else if ("cancel".equals(action)) {
            return null;
        } else {
            throw new IllegalArgumentException("unable to handle action \"" + action + "\"");
        }
    }

    private ModelAndView handleDeleteRequest(HttpServletRequest request) {
        final Gallery gallery = ControllerUtils.getGallery(getPhotogalDao(), request);
        final ImageDescriptor imageDescriptor = ControllerUtils.getImageDescriptor(
                getPhotogalDao(), request);
        if (!gallery.containsImage(imageDescriptor)) {
            throw new IllegalStateException("image " + imageDescriptor.getId()
                    + " is not contained in gallery " + gallery.getId());
        }
        if (gallery.isGalleryImage(imageDescriptor)) {
            gallery.setGalleryImage(null);
        }
        logger.debug("removing image " + imageDescriptor.getId() + " from gallery "
                + gallery.getId());
        gallery.removeImage(imageDescriptor);
        if (getPhotogalDao().getImageGalleryCount(imageDescriptor.getId()) <= 1) {
            logger.debug("image " + imageDescriptor.getId() + " is now orphaned, deleting it");
            getPhotogalDao().delete(imageDescriptor);
        }
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("imageId", imageDescriptor.getId());
        model.put("imageLocation", imageDescriptor.getLocation());
        model.put("gallery", gallery);
        return new ModelAndView("edit/imageDeleted", model);
    }

    private ModelAndView handleConfirmRequest(HttpServletRequest request) {
        final long galleryId = ControllerUtils.getGalleryId(request);
        final ImageDescriptor imageDescriptor = ControllerUtils.getImageDescriptor(
                getPhotogalDao(), request);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("galleryId", galleryId);
        model.put("image", imageDescriptor);
        return new ModelAndView("edit/confirmDeleteImage", model);
    }
}