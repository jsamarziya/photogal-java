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
package net.sourceforge.photogal.web.form;

import org.sixcats.utils.CalendarDate;
import org.sixcats.utils.web.form.DefaultPagedDataForm;

public class ShowImagesByDateForm extends DefaultPagedDataForm {
    public static final String DATE_TYPE_POSTED = "posted";
    public static final String DATE_TYPE_TAKEN = "taken";

    private int year;
    private Integer month;
    private String dateType;
    private boolean allMonths;
    private boolean includePrivate;

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public boolean isAllMonths() {
        return allMonths;
    }

    public void setAllMonths(boolean allMonths) {
        this.allMonths = allMonths;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public boolean getIncludePrivate() {
        return includePrivate;
    }

    public void setIncludePrivate(boolean includePrivate) {
        this.includePrivate = includePrivate;
    }

    /**
     * Returns a CalendarDate representing the selected month and/or year.
     */
    public CalendarDate getCalendarDate() {
        return new CalendarDate(null, getMonth(), getYear());
    }
}