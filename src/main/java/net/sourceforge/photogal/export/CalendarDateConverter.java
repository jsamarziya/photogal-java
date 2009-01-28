/*
 *  Copyright 2009 The Photogal Team.
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

package net.sourceforge.photogal.export;

import java.text.ParseException;

import org.sixcats.utils.CalendarDate;
import org.sixcats.utils.CalendarDateFormat;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * A {@link Converter} for {@link CalendarDate}s.
 */
public class CalendarDateConverter implements SingleValueConverter {
    private static final CalendarDateConverter INSTANCE = new CalendarDateConverter();

    /**
     * Returns the singleton instance of this class.
     * 
     * @return the CalendarDateConverter
     */
    public static CalendarDateConverter getInstance() {
        return INSTANCE;
    }

    private CalendarDateConverter() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type) {
        return CalendarDate.class.equals(type);
    }

    @Override
    public Object fromString(String str) {
        CalendarDate retval;
        try {
            retval = (CalendarDate) CalendarDateFormat.getInstance().parseObject(str);
        } catch (ParseException ex) {
            throw new ConversionException(ex);
        }
        return retval;
    }

    @Override
    public String toString(Object obj) {
        return CalendarDateFormat.getInstance().format(obj);
    }
}
