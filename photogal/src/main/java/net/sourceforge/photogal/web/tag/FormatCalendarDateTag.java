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
package net.sourceforge.photogal.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.sixcats.utils.CalendarDate;
import org.sixcats.utils.CalendarDateFormat;
import org.sixcats.utils.web.JspUtils;

public class FormatCalendarDateTag extends SimpleTagSupport {
    private String pattern;
    private CalendarDate value;
    private String var;
    private String scope;

    public FormatCalendarDateTag() {
        setScope(JspUtils.PAGE_SCOPE);
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public CalendarDate getValue() {
        return value;
    }

    public void setValue(CalendarDate value) {
        this.value = value;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getVar() {
        return var;
    }

    public void setVar(String var) {
        this.var = var;
    }

    @Override
    public void doTag() throws JspException, IOException {
        final JspContext jspContext = getJspContext();
        String formatted = null;
        if (getValue() != null) {
            formatted = getFormat().format(getValue());
        }
        if (getVar() == null) {
            if (formatted != null) {
                jspContext.getOut().print(formatted);
            }
        } else {
            final int scope = JspUtils.getScope(getScope());
            if (formatted == null) {
                jspContext.removeAttribute(getVar(), scope);
            } else {
                jspContext.setAttribute(getVar(), formatted, scope);
            }
        }
    }

    private CalendarDateFormat getFormat() {
        if (getPattern() == null) {
            throw new IllegalArgumentException("pattern not set");
        }
        return new CalendarDateFormat(getPattern());
    }
}