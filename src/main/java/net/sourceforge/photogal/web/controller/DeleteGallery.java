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
import net.sourceforge.photogal.hibernate.HibernateEntityManager;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.view.RedirectView;

public class DeleteGallery extends AbstractController {
    private static final View EDIT_GALLERIES_VIEW = new RedirectView(
                                                                     "/edit/editGalleries.do",
                                                                     true);

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String action = request.getParameter("action");
        if (action == null) {
            return handleConfirmRequest(request);
        } else if ("delete".equals(action)) {
            return handleDeleteRequest(request);
        } else if ("cancel".equals(action)) {
            return handleCancelRequest(request);
        } else {
            throw new IllegalArgumentException("unable to handle action \""
                + action + "\"");
        }
    }

    private ModelAndView handleConfirmRequest(final HttpServletRequest request) {
        final Gallery gallery = ControllerUtils.getGallery(request);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("gallery", gallery);
        return new ModelAndView("edit/confirmDeleteGallery", model);
    }

    private ModelAndView handleDeleteRequest(final HttpServletRequest request) {
        final long galleryId = ControllerUtils.getGalleryId(request);
        logger.debug("deleting gallery " + galleryId);
        HibernateEntityManager.getInstance().deleteGallery(galleryId, true);
        return new ModelAndView(EDIT_GALLERIES_VIEW);
    }

    private ModelAndView handleCancelRequest(final HttpServletRequest request) {
        return new ModelAndView(EDIT_GALLERIES_VIEW);
    }
}
