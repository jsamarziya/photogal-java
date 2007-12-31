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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;


import net.sourceforge.photogal.image.ImageOperations;
import net.sourceforge.photogal.image.ScaledImageCache;

import org.apache.commons.io.IOUtils;
import org.sixcats.utils.FileAccessManager;
import org.springframework.web.servlet.mvc.AbstractController;

public abstract class AbstractShowImageController extends AbstractController {
    private FileAccessManager fileAccessManager;
    private ScaledImageCache imageCache;

    public AbstractShowImageController() {
        super();
    }

    /**
     * Returns the file access manager used to control access to image files.
     */
    public FileAccessManager getFileAccessManager() {
        return fileAccessManager;
    }

    public void setFileAccessManager(final FileAccessManager manager) {
        fileAccessManager = manager;
    }

    public ScaledImageCache getImageCache() {
        return imageCache;
    }

    public void setImageCache(ScaledImageCache imageCache) {
        this.imageCache = imageCache;
    }

    protected void renderImage(final String filename, final String size,
            final HttpServletResponse response) throws IOException {
        File imageFile = getFileAccessManager().getFile(filename);
        if (!ImageOperations.isJPEG(imageFile)) {
            logger.warn("File " + imageFile.getPath()
                + " is not a supported image file");
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                               "Requested file is not a supported image file");
        }
        if (!size.equals("o")) {
            imageFile = getImageCache().getScaledImage(imageFile, size);
        }
        response.setContentType("image/jpeg");
        ServletOutputStream out = response.getOutputStream();
        InputStream in = new FileInputStream(imageFile);
        try {
            IOUtils.copy(in, out);
            out.flush();
        } finally {
            in.close();
        }
    }
}