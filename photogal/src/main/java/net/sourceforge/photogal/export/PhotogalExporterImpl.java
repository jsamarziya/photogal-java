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

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sourceforge.photogal.Gallery;
import net.sourceforge.photogal.ImageDescriptor;
import net.sourceforge.photogal.hibernate.HibernateEntityManager;

import org.hibernate.collection.PersistentList;
import org.hibernate.collection.PersistentSet;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.thoughtworks.xstream.XStream;

/**
 * The default implementation of the PhotogalExporter interface.
 */
public class PhotogalExporterImpl implements PhotogalExporter, InitializingBean {
    private HibernateEntityManager entityManager;

    /**
     * Creates a new PhotogalExporterImpl.
     */
    public PhotogalExporterImpl() {
        super();
    }

    /**
     * Returns the HibernateEntityManager used by this class to access the
     * database.
     * 
     * @return the entityManager
     */
    public HibernateEntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Sets the HibernateEntityManager used by this class to access the
     * database.
     * 
     * @param entityManager the entityManager
     */
    public void setEntityManager(HibernateEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void export(Writer writer) throws IOException {
        final HibernateEntityManager entityManager = getEntityManager();
        final PhotogalData data = new PhotogalData();
        data.setExportDate(new Date());
        data.setImageDescriptors(entityManager.getImageDescriptors());
        data.setGalleries(entityManager.getGalleries(true));
        final XStream xstream = createXStream();
        writer.write("<?xml version='1.0'?>\n");
        xstream.toXML(data, writer);
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(getEntityManager(), "an entityManager must be set");
    }

    /**
     * Creates the XStream facade used to convert the photogal data to XML.
     * 
     * @return the XStream
     */
    private static XStream createXStream() {
        final XStream retval = new XStream();
        retval.processAnnotations(new Class[] { PhotogalData.class, Gallery.class,
                ImageDescriptor.class });
        retval.registerConverter(CalendarDateConverter.getInstance());
        retval.addDefaultImplementation(PersistentList.class, List.class);
        retval.addDefaultImplementation(PersistentSet.class, Set.class);
        retval.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
        return retval;
    }
}
