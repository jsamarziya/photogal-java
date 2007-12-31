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

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.photogal.Gallery;
import net.sourceforge.photogal.image.ScaledImageCalculator;

import org.sixcats.utils.web.controller.SimplePagedDataController;
import org.sixcats.utils.web.form.PagedDataForm;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class ShowGallery extends SimplePagedDataController {
    private ScaledImageCalculator scaledImageCalculator;

    public ScaledImageCalculator getScaledImageCalculator() {
        return scaledImageCalculator;
    }

    public void setScaledImageCalculator(
            ScaledImageCalculator scaledImageCalculator) {
        this.scaledImageCalculator = scaledImageCalculator;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ModelAndView showForm(HttpServletRequest request,
            HttpServletResponse response, BindException errors)
            throws Exception {
        final boolean canViewPrivate = ControllerUtils.canViewPrivate(request);
        final Gallery gallery = ControllerUtils.getGallery(request);
        if (!gallery.isPublic() && !canViewPrivate) {
            throw new ServletException("gallery is private");
        }
        final PagedDataForm form = (PagedDataForm) errors.getTarget();
        final Map<String, Object> model = errors.getModel();
        model.put("gallery", gallery);
        model.put("images", form.getPageContents(gallery.getImages()));
        model.put("scaledImageCalculator", getScaledImageCalculator());
        return new ModelAndView("gallery", model);
    }
}
