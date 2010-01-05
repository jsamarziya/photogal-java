/*
 *  Copyright 2010 The Photogal Team.
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

package net.sourceforge.photogal.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides id generation services for Hibernate entities.
 */
public class HibernateIdGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateIdGenerator.class);
    private static final HibernateIdGenerator INSTANCE = new HibernateIdGenerator();
    /**
     * A mapping from entity class to the next id for that class.
     */
    private Map<Class<?>, Long> nextIds;

    private HibernateIdGenerator() {
        nextIds = new HashMap<Class<?>, Long>();
    }

    /**
     * Returns the singleton instance of this class.
     */
    public static HibernateIdGenerator getInstance() {
        return INSTANCE;
    }

    /**
     * Generates an id for an object of the specified class.
     * 
     * @return the new id
     */
    public synchronized Long getNextId(Class<?> clazz) {
        final Long retval = nextIds.get(clazz);
        if (retval == null) {
            throw new IllegalArgumentException("next id not initialized for " + clazz);
        }
        nextIds.put(clazz, retval + 1);
        return retval;
    }

    /**
     * Sets the next id for entity objects of the specified class.
     * 
     * @param clazz the entity class
     * @param nextId the next id
     */
    public synchronized void setNextId(Class<?> clazz, long nextId) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("initialized next id for " + clazz + " to " + nextId);
        }
        nextIds.put(clazz, nextId);
    }
}
