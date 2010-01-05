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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.photogal.export.PhotogalData;
import net.sourceforge.photogal.export.PhotogalImporter;
import net.sourceforge.photogal.hibernate.PhotogalDao;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

/**
 * A controller that imports the Photogal database from an export file.
 */
public class ImportDatabase extends PhotogalDaoAwareController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportDatabase.class);
    private PhotogalImporter importer;

    /**
     * Constructs a new ImportDatabase.
     */
    public ImportDatabase() {
        super();
    }

    /**
     * Returns the importer used by this servlet to import data.
     * 
     * @return the importer
     */
    public PhotogalImporter getImporter() {
        return importer;
    }

    /**
     * Sets the importer used by this servlet to import data.
     * 
     * @param importer the importer
     */
    public void setImporter(PhotogalImporter importer) {
        this.importer = importer;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Assert.notNull(getImporter(), "an Importer must be set");
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        checkDatabaseIsEmpty();
        final MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        final MultipartFile importFile = multipartRequest.getFile("importfile");
        if (importFile == null) {
            throw new IllegalArgumentException("No import file specified");
        }
        final PhotogalData importData = parseData(importFile);
        final int galleryCount = importData.getGalleries().size();
        final int imageCount = importData.getImageDescriptors().size();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Parsed import data:\n" + "Version: " + importData.getVersion()
                    + "\nExport Date:" + importData.getExportDate() + "\n" + galleryCount
                    + " galleries\n" + imageCount + " images");
        }
        getPhotogalDao().importData(importData);
        final ModelAndView retval = new ModelAndView("/edit/importComplete");
        retval.addObject("galleryCount", galleryCount);
        retval.addObject("imageCount", imageCount);
        return retval;
    }

    /**
     * Verifies that the photogal database is empty.
     * 
     * @throws IllegalStateException if the database is not empty
     */
    private void checkDatabaseIsEmpty() {
        PhotogalDao dao = getPhotogalDao();
        if (dao.getGalleryCount(true) > 0 || dao.getImageDescriptorCount(true) > 0) {
            throw new IllegalStateException("database not empty");
        }
    }

    /**
     * Parses an import file.
     * 
     * @param importFile the import file
     * @return the parsed data
     */
    private PhotogalData parseData(MultipartFile importFile) throws IOException {
        PhotogalData retval;
        final InputStream importFileStream = importFile.getInputStream();
        try {
            final Reader reader = new InputStreamReader(importFileStream, "UTF-8");
            retval = getImporter().importData(reader);
        } finally {
            IOUtils.closeQuietly(importFileStream);
        }
        return retval;
    }
}
