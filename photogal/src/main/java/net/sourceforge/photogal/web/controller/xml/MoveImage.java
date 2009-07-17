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

import net.sourceforge.photogal.Gallery;

public class MoveImage extends PhotogalDaoAwareWebService {
    @Override
    protected WebServiceResponse handleWebServiceRequest(HttpServletRequest request)
            throws Exception {
        DefaultWebServiceResponse response = new DefaultWebServiceResponse(request);
        final long galleryId = Long.parseLong(request.getParameter("gallery"));
        final int fromIndex = Integer.parseInt(request.getParameter("from"));
        final int toIndex = Integer.parseInt(request.getParameter("to"));
        final Gallery gallery = getPhotogalDao().getGallery(galleryId);
        if (gallery == null) {
            response.setStatus(WebServiceResponse.STATUS_ERROR);
            response.setStatusMessage("Gallery " + galleryId + " not found");
        } else {
            gallery.moveImage(fromIndex, toIndex);
            getPhotogalDao().update(gallery);
            response.setStatus(WebServiceResponse.STATUS_OK);
            log.debug("moved image " + fromIndex + " to " + toIndex + " (gallery " + galleryId
                    + ")");
        }
        return response;
    }
}
