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
package net.sourceforge.photogal.image;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang.NotImplementedException;
import org.sixcats.utils.FileAccessManager;
import org.sixcats.utils.image.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Runnable that is used to generate thumbnails for images.
 */
public class ThumbnailGenerator implements Runnable {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private ScaledImageCache cache;
    private FileAccessManager fileAccessManager;
    private long throttleDelay;
    private boolean isStopped;

    /**
     * A file filter that accepts image files for which a thumbnail have not
     * been created.
     */
    private final IOFileFilter imageWithoutThumbnailFileFilter = new IOFileFilter() {
        public boolean accept(File file) {
            try {
                if (!file.isFile()) {
                    // if (log.isDebugEnabled()) {
                    // log.debug(file + " is not a file");
                    // }
                    return false;
                } else if (!file.canRead()) {
                    if (log.isDebugEnabled()) {
                        log.debug(file + " is not readable");
                    }
                    return false;
                } else if (!getFileAccessManager().isValidFile(file)) {
                    if (log.isDebugEnabled()) {
                        log.debug(file + " is not valid");
                    }
                    return false;
                } else if (!ImageUtils.isImage(file)) {
                    if (log.isDebugEnabled()) {
                        log.debug(file + " is not an image file");
                    }
                    return false;
                } else if (getScaledImageCache().hasScaledImage(file, "t")) {
                    if (log.isDebugEnabled()) {
                        log.debug("thumbnail for " + file + " already created");
                    }
                    return false;
                } else {
                    return true;
                }
            } catch (IOException ex) {
                log.warn("Unable to check " + file, ex);
                return false;
            }
        }

        public boolean accept(File dir, String name) {
            throw new NotImplementedException();
        }
    };

    /**
     * Returns the scaled image cache used by this thumbnail generator to
     * determine whether or not to generate a thumbnail for a given image.
     * 
     * @return the scaled image cache
     */
    public ScaledImageCache getScaledImageCache() {
        return cache;
    }

    /**
     * Sets the scaled image cache used by this thumbnail generator to determine
     * whether or not to generate a thumbnail for a given image.
     * 
     * @param cache the scaled image cache
     */
    public void setScaledImageCache(ScaledImageCache cache) {
        this.cache = cache;
    }

    /**
     * Returns the file access manager used by this thumbnail generator to check
     * access for image files.
     * 
     * @return the file access manager
     */
    public FileAccessManager getFileAccessManager() {
        return fileAccessManager;
    }

    /**
     * Sets the file access manager used by this thumbnail generator to check
     * access for image files.
     * 
     * @param fileAccessManager the file access manager
     */
    public void setFileAccessManager(FileAccessManager fileAccessManager) {
        this.fileAccessManager = fileAccessManager;
    }

    /**
     * Returns the amount of time (in milliseconds) that this thumbnail
     * generator will wait between each thumbnail generation.
     * 
     * @return the throttle delay
     */
    public long getThrottleDelay() {
        return throttleDelay;
    }

    /**
     * Sets the amount of time (in milliseconds) that this thumbnail generator
     * will wait between each thumbnail generation.
     * 
     * @param delay the throttle delay
     */
    public void setThrottleDelay(final long delay) {
        throttleDelay = delay;
    }

    @Override
    public void run() {
        log.info("Starting thumbnail generation run");
        try {
            generate();
            log.info("Thumbnail generation run done");
        } catch (Exception ex) {
            log.error("Error occured while generating thumbnails, aborting", ex);
        }
    }

    /**
     * Notifies this generator to stop executing.
     */
    public void stop() {
        isStopped = true;
    }

    /**
     * Generates thumbnails.
     */
    @SuppressWarnings("unchecked")
    private void generate() {
        isStopped = false;
        final File imageDirectory = new File(getFileAccessManager().getBaseDirectory());
        if (!imageDirectory.exists()) {
            log.warn("Image directory " + imageDirectory + " does not exist");
            return;
        }
        int thumbnailsGenerated = 0;
        Collection<File> images = FileUtils.listFiles(imageDirectory,
                imageWithoutThumbnailFileFilter, TrueFileFilter.INSTANCE);
        log.info("found " + images.size() + " images to create thumbnails for");
        if (images.size() == 0) {
            return;
        }
        if (getThrottleDelay() > 0) {
            log.info("Waiting " + getThrottleDelay() + "ms between each");
        }
        for (File file : images) {
            if (isStopped) {
                log.info("Thumbnail generator stopped");
                break;
            }
            try {
                getScaledImageCache().getScaledImage(file, "t");
                ++thumbnailsGenerated;
                if (log.isDebugEnabled()) {
                    log.debug("Created thumbnail for " + file);
                }
                if (getThrottleDelay() > 0) {
                    Thread.sleep(getThrottleDelay());
                }
            } catch (Exception ex) {
                log.warn("error occured while creating thumbnail for " + file, ex);
            }
        }
        log.info(thumbnailsGenerated + " thumbnails generated");
    }
}
