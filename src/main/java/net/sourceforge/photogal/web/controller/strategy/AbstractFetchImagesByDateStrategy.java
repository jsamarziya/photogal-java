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

package net.sourceforge.photogal.web.controller.strategy;

import net.sourceforge.photogal.hibernate.PhotogalDao;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

public abstract class AbstractFetchImagesByDateStrategy implements FetchImagesByDateStrategy,
        InitializingBean {
    private PhotogalDao photogalDao;

    /**
     * Returns the DAO used by this strategy to access photogal data.
     * 
     * @return the DAO
     */
    public PhotogalDao getPhotogalDao() {
        return photogalDao;
    }

    /**
     * Sets the DAO used by this strategy to access photogal data.
     * 
     * @param photogalDao the DAO
     */
    public void setPhotogalDao(PhotogalDao photogalDao) {
        this.photogalDao = photogalDao;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(getPhotogalDao(), "a PhotogalDao must be set");
    }
}
