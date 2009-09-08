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

import java.util.List;

import net.sourceforge.photogal.ImageDescriptor;
import net.sourceforge.photogal.web.form.ShowImagesByDateForm;

public class FetchImagesByYearTaken extends AbstractFetchImagesByDateStrategy {
    public boolean canHandleRequest(ShowImagesByDateForm form) {
        return ShowImagesByDateForm.DATE_TYPE_TAKEN.equals(form.getDateType())
                && form.isAllMonths();
    }

    public int getImageCount(ShowImagesByDateForm form) {
        return getPhotogalDao().getImageCountByYearTaken(form.getYear(), form.getIncludePrivate());
    }

    @Override
    public List<ImageDescriptor> getImages(ShowImagesByDateForm form) {
        return getPhotogalDao().getImageDescriptorsByYearTaken(form.getYear(),
                form.getIncludePrivate(), form.getStartIndex(), form.getItemsPerPage());
    }
}
