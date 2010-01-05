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

package net.sourceforge.photogal.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.photogal.hibernate.PhotogalDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

/**
 * A controller that clears the Photogal database.
 */
public class ClearDatabase extends PhotogalDaoAwareController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClearDatabase.class);

    /**
     * Constructs a new ClearDatabase.
     */
    public ClearDatabase() {
        super();
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final PhotogalDao dao = getPhotogalDao();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("clearing database");
        }
        final int galleryCount = dao.deleteAllGalleries();
        final int imageCount = dao.deleteAllImageDescriptors();

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(galleryCount + " galleries deleted");
            LOGGER.debug(imageCount + " image descriptors deleted");
        }
        return new ModelAndView("/edit/databaseCleared.jsp");
    }
}
