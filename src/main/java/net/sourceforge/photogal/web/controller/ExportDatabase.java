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

import net.sourceforge.photogal.export.PhotogalData;
import net.sourceforge.photogal.export.PhotogalExporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

/**
 * A controller that exports the Photogal database and writes it to the response
 * as an XML file.
 */
public class ExportDatabase extends PhotogalDaoAwareController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportDatabase.class);

    private PhotogalExporter exporter;

    /**
     * Constructs a new ExportDatabase.
     */
    public ExportDatabase() {
        super();
    }

    /**
     * Returns the exporter used by this servlet to export data.
     * 
     * @return the exporter
     */
    public PhotogalExporter getExporter() {
        return exporter;
    }

    /**
     * Sets the exporter used by this servlet to export data.
     * 
     * @param exporter the exporter
     */
    public void setExporter(PhotogalExporter exporter) {
        this.exporter = exporter;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(getExporter(), "an Exporter must be set");
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final PhotogalData data = getPhotogalDao().getData();
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        getExporter().exportData(data, response.getWriter(), "UTF-8");
        LOGGER.info("Exported data (" + data.getImageDescriptors().size() + " images, "
                + data.getGalleries().size() + " galleries)");
        return null;
    }
}
