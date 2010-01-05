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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class XStreamPhotogalExporterTest {
    @Test
    public void testExportData1() throws IOException {
        final PhotogalData data = new PhotogalData();
        data.setExportDate(new Date(0));
        final CharArrayWriter writer = new CharArrayWriter();
        XStreamPhotogalExporter.getInstance().exportData(data, writer);
        final String expectedValue = readFile("XStreamPhotogalExporterTestFile1.txt", "UTF-8");
        assertEquals(expectedValue, writer.toString());
    }

    @Test
    public void testExportData2() throws IOException {
        final PhotogalData data = new PhotogalData();
        data.setExportDate(new Date(0));
        final CharArrayWriter writer = new CharArrayWriter();
        XStreamPhotogalExporter.getInstance().exportData(data, writer, "foo");
        final String expectedValue = readFile("XStreamPhotogalExporterTestFile3.txt", "UTF-8");
        assertEquals(expectedValue, writer.toString());
    }

    @Test
    public void testImportData() throws IOException {
        final String xml = readFile("XStreamPhotogalExporterTestFile2.txt", "UTF-8");
        final Reader reader = new StringReader(xml);
        final PhotogalData data = XStreamPhotogalExporter.getInstance().importData(reader);
        Calendar exportDate = Calendar.getInstance();
        exportDate.clear();
        exportDate.set(Calendar.YEAR, 2009);
        exportDate.set(Calendar.MONTH, Calendar.JANUARY);
        exportDate.set(Calendar.DATE, 28);
        exportDate.set(Calendar.HOUR_OF_DAY, 22);
        exportDate.set(Calendar.MINUTE, 47);
        exportDate.set(Calendar.SECOND, 2);
        exportDate.set(Calendar.MILLISECOND, 882);
        exportDate.setTimeZone(TimeZone.getTimeZone("PST"));
        assertThat(data.getExportDate(), is(exportDate.getTime()));
        assertThat(data.getGalleries(), is(instanceOf(ArrayList.class)));
        assertThat(data.getImageDescriptors(), is(instanceOf(ArrayList.class)));
        assertThat(data.getGalleries().size(), is(9));
        assertThat(data.getImageDescriptors().size(), is(66));
    }

    @Test
    public void testImportExportRoundtrip() throws IOException {
        final String xml = readFile("XStreamPhotogalExporterTestFile2.txt", "UTF-8");
        final Reader reader = new StringReader(xml);
        final PhotogalData data = XStreamPhotogalExporter.getInstance().importData(reader);
        final StringWriter writer = new StringWriter();
        XStreamPhotogalExporter.getInstance().exportData(data, writer);
        assertThat(writer.toString(), is(xml));
    }

    private String readFile(String filename, String encoding) throws IOException {
        final InputStream in = getClass().getResourceAsStream(filename);
        if (in == null) {
            throw new IllegalArgumentException("File " + filename + " not found");
        }
        String retval;
        try {
            retval = IOUtils.toString(in, encoding);
        } finally {
            in.close();
        }
        return retval;
    }
}
