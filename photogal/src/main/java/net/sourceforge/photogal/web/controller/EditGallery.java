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

import net.sourceforge.photogal.Gallery;
import net.sourceforge.photogal.web.form.EditGalleryForm;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

public class EditGallery extends PhotogalDaoAwareFormController {
    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
            throws Exception {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
            BindException errors) throws Exception {
        EditGalleryForm form = (EditGalleryForm) errors.getTarget();
        if (form.getId() == null) {
            throw new ServletException("no gallery id specified");
        } else if (form.isNewGallery()) {
            logger.debug("preparing EditGallery form for new gallery");
            form.setName("New Gallery");
            form.setPublic(false);
        } else {
            logger.debug("preparing EditGallery form for gallery \"" + form.getId() + "\"");
            Gallery gallery = ControllerUtils.getGallery(getPhotogalDao(), form.getId());
            form.setName(gallery.getName());
            form.setDescription(gallery.getDescription());
            form.setPublic(gallery.isPublic());
        }
        return showForm(request, errors, "edit/editGallery");
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors) throws Exception {
        final EditGalleryForm form = (EditGalleryForm) command;
        final String action = request.getParameter("action");
        if (action == null) {
            throw new ServletException("no action specified");
        } else if (action.equals("save")) {
            saveGallery(form);
            return new ModelAndView(form.getReturnTo());
        } else if (action.equals("cancel")) {
            return new ModelAndView(form.getCancelTo());
        } else {
            throw new ServletException("Unknown action " + action);
        }
    }

    private void saveGallery(final EditGalleryForm form) throws Exception {
        String id = form.getId();
        if (id == null) {
            throw new ServletException("no gallery id specified");
        }
        Gallery gallery = null;
        if (form.isNewGallery()) {
            gallery = new Gallery();
            gallery.setOrderIndex(getPhotogalDao().getGalleryCount(true));
        } else {
            gallery = ControllerUtils.getGallery(getPhotogalDao(), id);
        }
        gallery.setName(form.getName());
        gallery.setDescription(form.getDescription());
        gallery.setPublic(form.isPublic());
        getPhotogalDao().saveOrUpdate(gallery);
        if (form.isNewGallery()) {
            logger.debug("created new gallery (id=" + gallery.getId() + ")");
        } else {
            logger.debug("updated gallery (id=" + gallery.getId() + ")");
        }
    }
}