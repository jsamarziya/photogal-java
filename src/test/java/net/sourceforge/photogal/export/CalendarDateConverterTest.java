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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.sixcats.utils.CalendarDate;

public class CalendarDateConverterTest {
    private final CalendarDateConverter converter = CalendarDateConverter.getInstance();

    @Test
    public void testRoundtripConversion() {
        final Collection<CalendarDate> dates = new ArrayList<CalendarDate>();
        dates.add(new CalendarDate(null, null, 2009));
        dates.add(new CalendarDate(null, null, 2));
        dates.add(new CalendarDate(null, 3, 1234));
        dates.add(new CalendarDate(1, 2, 3456));
        for (CalendarDate date : dates) {
            assertThat(converter.fromString(converter.toString(date)), is((Object) date));
        }
    }
}
