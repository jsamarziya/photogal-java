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

import net.sourceforge.photogal.image.ScaledImageCalculator;
import net.sourceforge.photogal.web.controller.strategy.FetchImagesByDateStrategy;
import net.sourceforge.photogal.web.form.ShowImagesByDateForm;

import org.sixcats.utils.web.controller.SimplePagedDataController;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

public class ShowImagesByDate extends SimplePagedDataController {
    private ScaledImageCalculator scaledImageCalculator;
    private List<FetchImagesByDateStrategy> fetchStrategies;

    public ScaledImageCalculator getScaledImageCalculator() {
        return scaledImageCalculator;
    }

    public void setScaledImageCalculator(ScaledImageCalculator scaledImageCalculator) {
        this.scaledImageCalculator = scaledImageCalculator;
    }

    private List<FetchImagesByDateStrategy> getFetchStrategies() {
        return fetchStrategies;
    }

    public void setFetchStrategies(List<FetchImagesByDateStrategy> fetchStrategies) {
        this.fetchStrategies = fetchStrategies;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
            BindException errors) throws Exception {
        final ShowImagesByDateForm form = (ShowImagesByDateForm) errors.getTarget();
        form.setIncludePrivate(ControllerUtils.canViewPrivate(request));
        final FetchImagesByDateStrategy fetchStrategy = getFetchStrategy(form);
        if (fetchStrategy == null) {
            throw new IllegalStateException(
                    "unable to find a fetch strategy to handle this request");
        }
        final Map<String, Object> model = errors.getModel();
        model.put("imageCount", fetchStrategy.getImageCount(form));
        model.put("images", fetchStrategy.getImages(form));
        model.put("scaledImageCalculator", getScaledImageCalculator());
        return new ModelAndView("imagesByDate", model);
    }

    private FetchImagesByDateStrategy getFetchStrategy(ShowImagesByDateForm form) {
        for (FetchImagesByDateStrategy strategy : getFetchStrategies()) {
            if (strategy.canHandleRequest(form)) {
                return strategy;
            }
        }
        return null;
    }
}
