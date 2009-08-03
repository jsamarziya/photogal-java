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
package net.sourceforge.photogal.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import net.sourceforge.photogal.image.ScaledImageCache;

import org.sixcats.utils.FileAccessManager;
import org.sixcats.utils.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * A ServletContextListener that does some application lifecycle logging.
 */
public class ApplicationLogger implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLogger.class);

    public void contextInitialized(ServletContextEvent event) {
        final ApplicationContext appContext = WebApplicationContextUtils
                .getWebApplicationContext(event.getServletContext());
        LOGGER.info("photogal version " + getVersion() + " starting");
        logDatabaseInfo(appContext);
        logImageFileDirectory(appContext);
        logScaledImageCacheDirectory(appContext);
    }

    public void contextDestroyed(ServletContextEvent event) {
        LOGGER.info("photogal shutting down");
    }

    private String getVersion() {
        return Version.getVersion() + " (" + Version.getDeploymentEnvironment() + ")";
    }

    private void logDatabaseInfo(ApplicationContext appContext) {
        try {
            final String databaseDirectory = (String) appContext.getBean("databaseDirectory",
                    String.class);
            LOGGER.info("Database directory: " + databaseDirectory);
        } catch (Exception ex) {
            LOGGER.error("error logging database directory", ex);
        }
    }

    private void logImageFileDirectory(final ApplicationContext context) {
        try {
            final FileAccessManager imageFileAccessManager = (FileAccessManager) context.getBean(
                    "imageFileAccessManager", FileAccessManager.class);
            LOGGER.info("Image file base directory: " + imageFileAccessManager.getBaseDirectory());
        } catch (Exception ex) {
            LOGGER.error("error logging image file directory", ex);
        }
    }

    private void logScaledImageCacheDirectory(final ApplicationContext context) {
        try {
            final ScaledImageCache scaledImageCache = (ScaledImageCache) context.getBean(
                    "scaledImageCache", ScaledImageCache.class);
            LOGGER.info("Scaled image cache directory: " + scaledImageCache.getCacheDirectory());
        } catch (Exception ex) {
            LOGGER.error("error logging scaled image cache directory", ex);
        }
    }
}
