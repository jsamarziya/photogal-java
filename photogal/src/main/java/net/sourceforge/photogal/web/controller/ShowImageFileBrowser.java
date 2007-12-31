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
package net.sourceforge.photogal.web.controller;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.photogal.Gallery;
import net.sourceforge.photogal.ImageDescriptor;
import net.sourceforge.photogal.hibernate.HibernateEntityManager;
import net.sourceforge.photogal.image.ImageOperations;

import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang.Validate;
import org.sixcats.utils.FileAccessManager;
import org.sixcats.utils.FileUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ShowImageFileBrowser extends AbstractController {
    private FileAccessManager fileAccessManager;

    /**
     * Returns the file access manager used to access image files.
     */
    public FileAccessManager getFileAccessManager() {
        return fileAccessManager;
    }

    public void setFileAccessManager(final FileAccessManager manager) {
        fileAccessManager = manager;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final String dir = request.getParameter("dir");
        Validate.notNull(dir, "no dir specified");

        final Gallery gallery = ControllerUtils.getGallery(request);
        logger.debug("Showing image file browser for " + File.separatorChar
            + dir + ", galleryId=" + gallery.getId());

        final File baseDir = new File(getFileAccessManager().getBaseDirectory());
        final File imageDir = getFileAccessManager().getFile(dir);
        final Collection<File> imageFiles = getImageFiles(imageDir);
        final Map<String, Object> model = new HashMap<String, Object>();
        final File parentDirectory = imageDir.getParentFile();
        if (FileUtils.isAncestor(baseDir, parentDirectory)) {
            model.put("parentDirectory", new File(FileUtils
                    .getRelativePath(baseDir, parentDirectory)));
        }
        model.put("currentDirectory", new File(File.separator, FileUtils
                .getRelativePath(baseDir, imageDir)));
        model.put("imageFiles", imageFiles);
        model.put("subdirectories", getSubdirectories(imageDir));
        model.put("gallery", gallery);
        model.put("imageDescriptors", getImageDescriptors(imageFiles));
        final File parentDir = imageDir.getParentFile();
        if (parentDir != null && FileUtils.isAncestor(baseDir, parentDir)) {
            model.put("parentDir", FileUtils
                    .getRelativePath(baseDir, parentDir));
        }
        return new ModelAndView("edit/imageFileBrowser", model);
    }

    private Map<File, ImageDescriptor> getImageDescriptors(
            Collection<File> imageFiles) {
        Map<File, ImageDescriptor> retval = new HashMap<File, ImageDescriptor>();
        for (File file : imageFiles) {
            final String location = file.getPath().replace('\\', '/');
            final ImageDescriptor descriptor = HibernateEntityManager
                    .getInstance().getImageDescriptor(location);
            if (descriptor != null) {
                retval.put(file, descriptor);
            }
        }
        return retval;
    }

    private Collection<File> getImageFiles(final File dir) throws IOException {
        File baseDir = new File(getFileAccessManager().getBaseDirectory());
        ArrayList<File> retval = new ArrayList<File>();
        for (File file : ImageOperations.getImageFiles(dir)) {
            retval.add(new File(FileUtils.getRelativePath(baseDir, file)));
        }
        Collections.sort(retval);
        return retval;
    }

    private Collection<File> getSubdirectories(final File dir)
            throws IOException {
        File baseDir = new File(getFileAccessManager().getBaseDirectory());
        ArrayList<File> retval = new ArrayList<File>();
        for (File subdir : dir
                .listFiles((FileFilter) DirectoryFileFilter.INSTANCE)) {
            retval.add(new File(FileUtils.getRelativePath(baseDir, subdir)));
        }
        Collections.sort(retval);
        return retval;
    }
}