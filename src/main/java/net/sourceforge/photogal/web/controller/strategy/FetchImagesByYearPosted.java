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
package net.sourceforge.photogal.web.controller.strategy;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sourceforge.photogal.ImageDescriptor;
import net.sourceforge.photogal.web.form.ShowImagesByDateForm;

import org.sixcats.utils.ThreadLocalObjects;

public class FetchImagesByYearPosted extends AbstractFetchImagesByDateStrategy {
    public boolean canHandleRequest(ShowImagesByDateForm form) {
        return ShowImagesByDateForm.DATE_TYPE_POSTED.equals(form.getDateType())
                && form.isAllMonths();
    }

    private Date getStartDate(ShowImagesByDateForm form) {
        Calendar cal = ThreadLocalObjects.getCalendar();
        cal.set(Calendar.YEAR, form.getYear());
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    private Date getEndDate(ShowImagesByDateForm form) {
        Calendar cal = ThreadLocalObjects.getCalendar();
        cal.set(Calendar.YEAR, form.getYear());
        cal.set(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }

    public int getImageCount(ShowImagesByDateForm form) {
        return getPhotogalDao().getImageCountByDatePosted(getStartDate(form), getEndDate(form),
                form.getIncludePrivate());
    }

    @Override
    public List<ImageDescriptor> getImages(ShowImagesByDateForm form) {
        return getPhotogalDao().getImageDescriptorsByDatePosted(getStartDate(form),
                getEndDate(form), form.getIncludePrivate(), form.getStartIndex(),
                form.getItemsPerPage());
    }
}