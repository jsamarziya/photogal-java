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
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Set;

import net.sourceforge.photogal.Gallery;
import net.sourceforge.photogal.ImageDescriptor;

import org.hibernate.collection.PersistentList;
import org.hibernate.collection.PersistentSet;

import com.thoughtworks.xstream.XStream;

/**
 * An implementation of the PhotogalExporter interface that uses XStream for
 * serialization.
 */
public class XStreamPhotogalExporter implements PhotogalExporter, PhotogalImporter {
    private static final String EXPORT_VERSION_ID = "1.0";
    private static final Class<?>[] ANNOTATED_CLASSES = { PhotogalData.class, Gallery.class,
            ImageDescriptor.class };
    private static final XStreamPhotogalExporter INSTANCE = new XStreamPhotogalExporter();

    private XStreamPhotogalExporter() {
    }

    /**
     * Returns the singleton instance of this class.
     * 
     * @return the PhotogalExporterImpl instance
     */
    public static final XStreamPhotogalExporter getInstance() {
        return INSTANCE;
    }

    @Override
    public void exportData(PhotogalData data, Writer writer) throws IOException {
        if (data.getExportDate() == null) {
            data.setExportDate(new Date());
        }
        data.setVersion(EXPORT_VERSION_ID);
        final XStream xstream = createXStream();
        writer.write("<?xml version='1.0'?>\n");
        xstream.toXML(data, writer);
    }

    @Override
    public PhotogalData importData(Reader reader) throws IOException {
        final XStream xstream = createXStream();
        return (PhotogalData) xstream.fromXML(reader);
    }

    /**
     * Creates an XStream facade used to serialize photogal data to XML.
     * 
     * @return the XStream
     */
    private XStream createXStream() {
        final XStream retval = new XStream();
        retval.processAnnotations(ANNOTATED_CLASSES);
        retval.registerConverter(CalendarDateConverter.getInstance());
        retval.addDefaultImplementation(PersistentList.class, List.class);
        retval.addDefaultImplementation(PersistentSet.class, Set.class);
        retval.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
        return retval;
    }
}
