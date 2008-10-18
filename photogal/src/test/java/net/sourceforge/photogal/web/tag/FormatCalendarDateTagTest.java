/*
 *  Copyright 2008 The Photogal Team.
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

package net.sourceforge.photogal.web.tag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.junit.Before;
import org.junit.Test;
import org.sixcats.utils.CalendarDate;
import org.sixcats.utils.web.JspUtils;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;

public class FormatCalendarDateTagTest {
    private MockPageContext pageContext;
    private MockHttpServletResponse response;
    private FormatCalendarDateTag tag;

    @Before
    public void setUp() {
        response = new MockHttpServletResponse();
        pageContext = new MockPageContext(null, null, response, null);
        tag = new FormatCalendarDateTag();
        tag.setJspContext(pageContext);
    }

    @Test
    public void testDefaultProperties() {
        assertNull(tag.getPattern());
        assertEquals(JspUtils.PAGE_SCOPE, tag.getScope());
        assertNull(tag.getValue());
        assertNull(tag.getVar());
    }

    @Test
    public void testDoTag1() throws JspException, IOException {
        tag.doTag();
        assertEquals("", response.getContentAsString());
    }

    @Test
    public void testDoTag2() throws JspException, IOException {
        final CalendarDate date = new CalendarDate(1, 2, 3);
        tag.setValue(date);
        try {
            tag.doTag();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
    }

    @Test
    public void testDoTag3() throws JspException, IOException {
        final CalendarDate date = new CalendarDate(1, 2, 3);
        tag.setValue(date);
        tag.setPattern("{yyyy|MM/yyyy|MM/dd/yyyy}");
        tag.doTag();
        assertEquals("02/01/0003", response.getContentAsString());
    }

    @Test
    public void testDoTag4() throws JspException, IOException {
        pageContext.getRequest().setAttribute("foo", "bar");
        assertEquals("bar", pageContext.getRequest().getAttribute("foo"));
        tag.setVar("foo");
        tag.setScope("request");
        tag.doTag();
        assertNull(pageContext.getRequest().getAttribute("foo"));
    }

    @Test
    public void testDoTag5() throws JspException, IOException {
        final CalendarDate date = new CalendarDate(null, null, 2008);
        tag.setValue(date);
        tag.setVar("blah");
        tag.setScope("page");
        tag.setPattern("{yyyy|MM/yyyy|MM/dd/yyyy}");
        assertNull(pageContext.getAttribute("blah"));
        tag.doTag();
        assertEquals("2008", pageContext.getAttribute("blah"));
    }
}
