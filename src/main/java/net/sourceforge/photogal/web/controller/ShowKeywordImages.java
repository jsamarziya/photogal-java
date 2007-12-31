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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.photogal.ImageDescriptor;
import net.sourceforge.photogal.hibernate.HibernateEntityManager;
import net.sourceforge.photogal.image.ScaledImageCalculator;
import net.sourceforge.photogal.web.form.ShowKeywordImagesForm;

import org.hibernate.Query;
import org.sixcats.utils.web.controller.SimplePagedDataController;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class ShowKeywordImages extends SimplePagedDataController {
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
        final ShowKeywordImagesForm form = (ShowKeywordImagesForm) errors
                .getTarget();
        final String keyword = form.getKeyword();
        final int imageCount = getImageCount(keyword, canViewPrivate);
        final List<ImageDescriptor> images = getImages(keyword, canViewPrivate,
                                                       form.getStartIndex(),
                                                       form.getItemsPerPage());

        final Map<String, Object> model = errors.getModel();
        model.put("imageCount", imageCount);
        model.put("images", images);
        model.put("scaledImageCalculator", getScaledImageCalculator());
        return new ModelAndView("keywordImages", model);
    }

    /**
     * Returns the number of image descriptors that match the specified keyword.
     * 
     * @param keyword the keyword
     * @param includePrivate if true, include private images
     * @return the number of image descriptors that match the keyword
     */
    private int getImageCount(final String keyword, final boolean includePrivate) {
        return HibernateEntityManager.getInstance()
                .getKeywordImageCount(keyword, includePrivate);
    }

    @SuppressWarnings("unchecked")
    private List<ImageDescriptor> getImages(final String keyword,
            final boolean includePrivate, final int firstResult,
            final int maxResults) {
        Query query = HibernateEntityManager.getInstance()
                .createGetImagesByKeywordQuery(keyword, includePrivate);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
        final List<ImageDescriptor> retval = query.list();
        return retval;
    }
}
