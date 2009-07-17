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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.sixcats.utils.CalendarDate;
import org.sixcats.utils.CalendarDateSetDayTransformer;
import org.springframework.web.servlet.ModelAndView;

public class ShowDateIndex extends PhotogalDaoAwareController {
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final boolean canViewPrivate = ControllerUtils.canViewPrivate(request);
        final Map<String, Object> model = new HashMap<String, Object>();
        model.put("takenDateCount", org.sixcats.utils.CollectionUtils
                .getIndexedDateCountMap(getDateTakenCountMap(canViewPrivate)));
        model.put("postedDateCount", org.sixcats.utils.CollectionUtils
                .getIndexedDateCountMap(getDatePostedCountMap(canViewPrivate)));
        return new ModelAndView("dateIndex", model);
    }

    @SuppressWarnings("unchecked")
    private Map<CalendarDate, Integer> getDateTakenCountMap(final boolean includePrivate) {
        final Map<Long, CalendarDate> creationDateMap = getPhotogalDao().getImageCreationDates(
                includePrivate);
        final List<CalendarDate> creationDates = new ArrayList<CalendarDate>(creationDateMap
                .values());
        CollectionUtils.transform(creationDates, CalendarDateSetDayTransformer.getNullInstance());
        final Map<CalendarDate, Integer> dateCountMap = CollectionUtils
                .getCardinalityMap(creationDates);
        return dateCountMap;
    }

    @SuppressWarnings("unchecked")
    private Map<CalendarDate, Integer> getDatePostedCountMap(final boolean includePrivate) {
        final Map<Long, Date> postedDateMap = getPhotogalDao().getDescriptorCreationDates(
                includePrivate);
        final List<CalendarDate> postedDates = new ArrayList<CalendarDate>();
        for (Date date : postedDateMap.values()) {
            postedDates.add((CalendarDate) CalendarDateSetDayTransformer.getNullInstance()
                    .transform(new CalendarDate(date)));

        }
        final Map<CalendarDate, Integer> dateCountMap = CollectionUtils
                .getCardinalityMap(postedDates);
        return dateCountMap;
    }
}