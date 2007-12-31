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
import javax.servlet.jsp.el.ELException;
import javax.servlet.jsp.el.ExpressionEvaluator;
import javax.servlet.jsp.el.FunctionMapper;
import javax.servlet.jsp.el.VariableResolver;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.sixcats.utils.CalendarDate;
import org.sixcats.utils.CalendarDateFormat;
import org.sixcats.utils.web.JspUtils;

public class FormatCalendarDateTag extends SimpleTagSupport {
    private String pattern;
    private String value;
    private String var;
    private String scope;
    private CalendarDate date;

    public FormatCalendarDateTag() {
        setScope(JspUtils.PAGE_SCOPE);
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public CalendarDate getDate() {
        return date;
    }

    private void setDate(CalendarDate date) {
        this.date = date;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
        try {
            evaluateExpressions();
        } catch (ELException ex) {
            throw new JspException(ex);
        }
        String formatted = null;
        if (getValue() != null) {
            formatted = getFormat().format(getDate());
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
        return new CalendarDateFormat(getPattern());
    }

    private void evaluateExpressions() throws ELException {
        final JspContext jspContext = getJspContext();
        final ExpressionEvaluator expressionEvaluator = jspContext
                .getExpressionEvaluator();
        final VariableResolver variableResolver = jspContext
                .getVariableResolver();
        final FunctionMapper fMapper = null;
        setPattern((String) expressionEvaluator.evaluate(getPattern(),
                                                         String.class,
                                                         variableResolver,
                                                         fMapper));
        setDate((CalendarDate) expressionEvaluator.evaluate(getValue(),
                                                            CalendarDate.class,
                                                            variableResolver,
                                                            fMapper));
        if (getScope() != null) {
            setScope((String) expressionEvaluator.evaluate(getScope(),
                                                           String.class,
                                                           variableResolver,
                                                           fMapper));
        }
        if (getVar() != null) {
            setVar((String) expressionEvaluator.evaluate(getVar(),
                                                         String.class,
                                                         variableResolver,
                                                         fMapper));
        }
    }
}