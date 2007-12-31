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
package net.sourceforge.photogal.web.controller.xml;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class DefaultWebServiceResponse implements WebServiceResponse {
    private String status;
    private String statusMessage;
    private Map<String, String[]> requestParameters;

    public DefaultWebServiceResponse() {
    }

    @SuppressWarnings("unchecked")
    public DefaultWebServiceResponse(HttpServletRequest request) {
        setRequestParameters(request.getParameterMap());
    }

    public Map<String, String[]> getRequestParameters() {
        return requestParameters;
    }

    public void setRequestParameters(final Map<String, String[]> params) {
        requestParameters = params;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(final String message) {
        statusMessage = message;
    }
}
