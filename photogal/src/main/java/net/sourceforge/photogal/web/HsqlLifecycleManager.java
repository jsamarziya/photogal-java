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

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * A ServletContextListener that manages the HSQL database lifecycle.
 */
public class HsqlLifecycleManager implements ServletContextListener {
    /**
     * The location of the classpath resource that contains the SQL statements
     * needed to initialize the database.
     */
    private static final String DB_SCRIPT = "/net/sourceforge/photogal/hsql/db.script";

    private static final Logger LOGGER = LoggerFactory.getLogger(HsqlLifecycleManager.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        final WebApplicationContext appContext = getApplicationContext(event);
        final JdbcTemplate jdbcTemplate = createJdbcTemplate(appContext);
        if (!isDatabaseInitialized(jdbcTemplate)) {
            try {
                initializeDatabase(jdbcTemplate);
            } catch (IOException ex) {
                throw new RuntimeException("An error occurred while initializing the database", ex);
            } catch (DataAccessException ex) {
                throw new RuntimeException("An error occurred while initializing the database", ex);
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        final WebApplicationContext appContext = getApplicationContext(event);
        final JdbcTemplate jdbcTemplate = createJdbcTemplate(appContext);
        try {
            shutdownDatabase(jdbcTemplate);
        } catch (DataAccessException ex) {
            throw new RuntimeException("An error occured while shutting down database", ex);
        }
    }

    /**
     * Returns <code>true</code> if the database appears to have been
     * initialized.
     * 
     * @return <code>true</code> if the database has been initialized,
     *         <code>false</code> if the database has not been initialized
     */
    private boolean isDatabaseInitialized(JdbcTemplate jdbcTemplate) {
        boolean retval;
        try {
            final int galleryCount = jdbcTemplate.queryForInt("select count(*) from GALLERIES");
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Found " + galleryCount
                        + " galleries - database appears to be initialized");
            }
            retval = true;
        } catch (DataAccessException ex) {
            retval = false;
        }
        return retval;
    }

    /**
     * Initializes the database. Creates the tables, etc.
     * 
     * @param jdbcTemplate the JdbcTemplate to use to connect to the database
     */
    private void initializeDatabase(JdbcTemplate jdbcTemplate) throws IOException {
        LOGGER.info("Initializing database");
        final InputStream dbScript = getClass().getResourceAsStream(DB_SCRIPT);
        if (dbScript == null) {
            throw new IllegalStateException("Unable to find resource" + DB_SCRIPT);
        }
        try {
            final String sql = IOUtils.toString(dbScript);
            jdbcTemplate.execute(sql);
        } finally {
            IOUtils.closeQuietly(dbScript);
        }
    }

    /**
     * Shuts down the database.
     * 
     * @param jdbcTemplate the JdbcTemplate to use to connect to the database
     */
    private void shutdownDatabase(JdbcTemplate jdbcTemplate) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Shutting down database");
        }
        jdbcTemplate.execute("SHUTDOWN COMPACT");
    }

    /**
     * Returns the WebApplicationContext for the specified ServletContextEvent.
     * 
     * @return the WebApplicationContext
     */
    private WebApplicationContext getApplicationContext(ServletContextEvent event) {
        return WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
    }

    /**
     * Creates a JdbcTemplate that can be used to connect to the database.
     * 
     * @return the JdbcTemplate
     */
    private JdbcTemplate createJdbcTemplate(ApplicationContext context) {
        final DataSource dataSource = (DataSource) context.getBean("dataSource", DataSource.class);
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate;
    }
}
