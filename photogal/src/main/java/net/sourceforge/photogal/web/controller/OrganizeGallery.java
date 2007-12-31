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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import net.sourceforge.photogal.hibernate.HibernateEntityManager;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class OrganizeGallery extends AbstractController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final long galleryId = ControllerUtils.getGalleryId(request);
        if (!HibernateEntityManager.getInstance().galleryExists(galleryId)) {
            throw new ServletException("gallery " + galleryId + " not found");
        }
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("galleryId", galleryId);
        model.put("returnTo", request.getParameter("returnTo"));
        model.put("returnToName", request.getParameter("returnToName"));
        return new ModelAndView("edit/organizeGallery", model);
    }
}
