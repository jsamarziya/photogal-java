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

import net.sourceforge.photogal.hibernate.PhotogalDao;

import org.sixcats.utils.web.controller.SimplePagedDataController;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * A subclass of SimplePagedDataController that provides access to a
 * PhotogalDao.
 */
public abstract class PhotogalDaoAwarePagedDataController extends SimplePagedDataController
        implements InitializingBean {
    private PhotogalDao photogalDao;

    /**
     * Returns the PhotogalDao used by this controller.
     * 
     * @return the photogalDao
     */
    public PhotogalDao getPhotogalDao() {
        return photogalDao;
    }

    /**
     * Sets the PhotogalDao used by this controller.
     * 
     * @param photogalDao the photogalDao
     */
    public void setPhotogalDao(PhotogalDao photogalDao) {
        this.photogalDao = photogalDao;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(getPhotogalDao(), "a photogalDao must be set");
    }
}
