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

package net.sourceforge.photogal.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.photogal.export.PhotogalData;
import net.sourceforge.photogal.export.PhotogalExporter;
import net.sourceforge.photogal.export.XStreamPhotogalExporter;
import net.sourceforge.photogal.hibernate.EntityManager;
import net.sourceforge.photogal.hibernate.HibernateEntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A servlet that exports the Photogal database and writes it to the response as
 * an XML file.
 */
public class PhotogalExportServlet extends HttpServlet {
    private static final Logger LOGGER = LoggerFactory.getLogger(PhotogalExportServlet.class);

    private PhotogalExporter exporter;
    private EntityManager entityManager;

    public PhotogalExportServlet() {
        setExporter(XStreamPhotogalExporter.getInstance());
        setEntityManager(HibernateEntityManager.getInstance());
    }

    /**
     * Returns the entity manager used to access the database.
     * 
     * @return the entity manager
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Sets the entity manager used to access the database.
     * 
     * @param entityManager the entity manager
     */
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        final PhotogalData data = getEntityManager().getData();
        response.setContentType("text/xml");
        response.setCharacterEncoding("UTF-8");
        getExporter().exportData(data, response.getWriter());
        LOGGER.info("Exported data (" + data.getImageDescriptors().size() + " images, "
                + data.getGalleries().size() + " galleries)");
    }
}
