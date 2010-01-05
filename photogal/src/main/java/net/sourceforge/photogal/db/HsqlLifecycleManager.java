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
package net.sourceforge.photogal.db;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * A bean that manages the HSQL database lifecycle.
 */
public class HsqlLifecycleManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HsqlLifecycleManager.class);

    private Resource dbScript;
    private JdbcTemplate jdbcTemplate;

    /**
     * Returns the resource that contains the SQL statements needed to
     * initialize the database.
     * 
     * @return the databse initialization script
     */
    public Resource getDatabaseScript() {
        return dbScript;
    }

    /**
     * Sets the resource that contains the SQL statements needed to initialize
     * the database.
     * 
     * @param script the databse initialization script
     */
    public void setDatabaseScript(Resource script) {
        dbScript = script;
    }

    /**
     * Returns the JdbcTemplate used to connect to the database.
     * 
     * @return the JdbcTemplate
     */
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    /**
     * Sets the JdbcTemplate used to connect to the database.
     * 
     * @param template the JdbcTemplate
     */
    public void setJdbcTemplate(JdbcTemplate template) {
        this.jdbcTemplate = template;
    }

    /**
     * Initializes the database if necessary.
     */
    public void startup() {
        if (!isDatabaseInitialized()) {
            try {
                initializeDatabase();
            } catch (IOException ex) {
                throw new RuntimeException("An error occurred while initializing the database", ex);
            } catch (DataAccessException ex) {
                throw new RuntimeException("An error occurred while initializing the database", ex);
            }
        }
    }

    /**
     * Shuts down the database.
     */
    public void shutdown() {
        try {
            shutdownDatabase();
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
    private boolean isDatabaseInitialized() {
        boolean retval;
        try {
            final int galleryCount = getJdbcTemplate()
                    .queryForInt("select count(*) from GALLERIES");
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
     */
    private void initializeDatabase() throws IOException {
        LOGGER.info("Initializing database");
        final InputStream dbScript = getDatabaseScript().getInputStream();
        if (dbScript == null) {
            throw new IllegalStateException("Unable to find resource" + getDatabaseScript());
        }
        try {
            final String sql = IOUtils.toString(dbScript);
            getJdbcTemplate().execute(sql);
        } finally {
            IOUtils.closeQuietly(dbScript);
        }
    }

    /**
     * Shuts down the database.
     */
    private void shutdownDatabase() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Shutting down database");
        }
        getJdbcTemplate().execute("SHUTDOWN COMPACT");
    }
}
