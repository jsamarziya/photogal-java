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

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.photogal.hibernate.HibernateEntityManager;


public class MoveGallery extends WebService {
    @Override
    protected WebServiceResponse handleWebServiceRequest(
            HttpServletRequest request) throws Exception {
        DefaultWebServiceResponse response = new DefaultWebServiceResponse(
                                                                           request);
        final int fromIndex = Integer.parseInt(request.getParameter("from"));
        final int toIndex = Integer.parseInt(request.getParameter("to"));
        final int galleryCount = HibernateEntityManager.getInstance()
                .getGalleryCount();
        if (fromIndex < 0 || fromIndex > galleryCount - 1) {
            response.setStatus(WebServiceResponse.STATUS_ERROR);
            response.setStatusMessage("fromIndex (" + fromIndex
                + ") out of range");
        } else if (toIndex < 0 || toIndex > galleryCount - 1) {
            response.setStatus(WebServiceResponse.STATUS_ERROR);
            response.setStatusMessage("toIndex (" + fromIndex
                + ") out of range");
        } else {
            HibernateEntityManager.getInstance()
                    .moveGallery(fromIndex, toIndex);
            response.setStatus(WebServiceResponse.STATUS_OK);
            log.debug("moved gallery " + fromIndex + " to " + toIndex);
        }
        return response;
    }
}
