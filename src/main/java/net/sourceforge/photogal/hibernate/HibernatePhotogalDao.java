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
package net.sourceforge.photogal.hibernate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.photogal.Gallery;
import net.sourceforge.photogal.ImageDescriptor;
import net.sourceforge.photogal.export.PhotogalData;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.sixcats.utils.CalendarDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * An implementation of PhotogalDao that uses Hibernate to access the photogal
 * database.
 */
public class HibernatePhotogalDao implements PhotogalDao, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernatePhotogalDao.class);

    private SessionFactory sessionFactory;

    /**
     * Constructs a new HibernatePhotogalDao.
     */
    public HibernatePhotogalDao() {
    }

    /**
     * Returns the Hibernate SessionFactory used by this DAO.
     * 
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Sets the Hibernate SessionFactory used by this DAO.
     * 
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(getSessionFactory(), "a SessionFactory must be set");
        getCurrentSession().beginTransaction();
        try {
            initializeNextId(Gallery.class);
            initializeNextId(ImageDescriptor.class);
        } finally {
            getCurrentSession().getTransaction().commit();
        }
    }

    /**
     * Initializes the HibernateIdGenerator with the correct next id value for
     * the specified entity class.
     * 
     * @param clazz the class
     */
    private void initializeNextId(Class<?> clazz) {
        final Query query = getCurrentSession().createQuery(
                "select max(o.id) from " + clazz.getSimpleName() + " o");
        final Long maxId = (Long) ObjectUtils.defaultIfNull(query.uniqueResult(), 0l);
        HibernateIdGenerator.getInstance().setNextId(clazz, maxId + 1);
    }

    /**
     * Returns the current hibernate session.
     */
    private Session getCurrentSession() {
        return getSessionFactory().getCurrentSession();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Gallery> getGalleries(final boolean includePrivate) {
        final Criteria criteria = getCurrentSession().createCriteria(Gallery.class).addOrder(
                Order.asc("orderIndex"));
        if (!includePrivate) {
            criteria.add(Restrictions.eq("public", Boolean.TRUE));
        }
        final List<Gallery> retval = criteria.list(); // unchecked
        return retval;
    }

    @Override
    public int getGalleryCount(final boolean includePrivate) {
        final Criteria criteria = getCurrentSession().createCriteria(Gallery.class).setProjection(
                Projections.rowCount());
        if (!includePrivate) {
            criteria.add(Restrictions.eq("public", Boolean.TRUE));
        }
        final Number retval = (Number) criteria.uniqueResult();
        return retval.intValue();
    }

    @Override
    public Gallery getGallery(final long id) {
        final Gallery retval = (Gallery) getCurrentSession().createCriteria(Gallery.class).add(
                Restrictions.eq("id", id)).uniqueResult();
        return retval;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ImageDescriptor> getImageDescriptors() {
        final Criteria criteria = getCurrentSession().createCriteria(ImageDescriptor.class)
                .addOrder(Order.asc("id"));
        final List<ImageDescriptor> retval = criteria.list(); // unchecked
        return retval;
    }

    @Override
    public ImageDescriptor getImageDescriptor(final long imageId) {
        final ImageDescriptor retval = (ImageDescriptor) getCurrentSession().createCriteria(
                ImageDescriptor.class).add(Restrictions.eq("id", imageId)).uniqueResult();
        return retval;
    }

    @Override
    public ImageDescriptor getImageDescriptor(final String location) {
        final ImageDescriptor retval = (ImageDescriptor) getCurrentSession().createCriteria(
                ImageDescriptor.class).add(Restrictions.eq("location", location)).uniqueResult();
        return retval;
    }

    @Override
    public boolean galleryExists(final long id) {
        final Number retval = (Number) getCurrentSession().createCriteria(Gallery.class).add(
                Restrictions.eq("id", id)).setProjection(Projections.rowCount()).uniqueResult();
        return retval.longValue() > 0;
    }

    @Override
    public int getGalleryCountForImage(long imageId) {
        final Number retval = (Number) getCurrentSession().getNamedQuery("getImageGalleryCount")
                .setLong("imageId", imageId).uniqueResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("image " + imageId + " is contained in " + retval + " galleries");
        }
        return retval.intValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void deleteGallery(final long galleryId, boolean deleteOrphanImages) {
        final Gallery gallery = getGallery(galleryId);
        if (deleteOrphanImages) {
            deleteOrphanImages(gallery);
        }
        getCurrentSession().delete(gallery);
        final List<Gallery> list = getCurrentSession().createCriteria(Gallery.class).add(
                Restrictions.gt("orderIndex", gallery.getOrderIndex())).list();
        for (Gallery g : list) {
            final int oldIndex = g.getOrderIndex();
            final int newIndex = oldIndex - 1;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("moving gallery " + g.getId() + " from " + oldIndex + " to "
                        + newIndex);
            }
            g.setOrderIndex(newIndex);
        }
    }

    /**
     * Deletes any images contained exclusively in the specified gallery.
     * 
     * @param gallery the gallery that is being deleted
     */
    private void deleteOrphanImages(final Gallery gallery) {
        for (ImageDescriptor descriptor : gallery.getImages()) {
            if (getGalleryCountForImage(descriptor.getId()) <= 1) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("image " + descriptor.getId() + " is now orphaned, deleting it");
                }
                getCurrentSession().delete(descriptor);
            }
        }
    }

    @Override
    public void moveGallery(final int fromIndex, final int toIndex) {
        if (fromIndex == toIndex) {
            return;
        } else if (fromIndex < toIndex) {
            moveGalleryDown(fromIndex, toIndex);
        } else {
            moveGalleryUp(fromIndex, toIndex);
        }
    }

    /**
     * Moves a gallery down the list, bubbling all galleries that follow it up
     * one position.
     * 
     * @param fromIndex the index of the gallery to move
     * @param toIndex the index to move the gallery to
     */
    @SuppressWarnings("unchecked")
    private void moveGalleryDown(final int fromIndex, final int toIndex) {
        final Gallery gallery = (Gallery) getCurrentSession().createCriteria(Gallery.class).add(
                Restrictions.eq("orderIndex", fromIndex)).uniqueResult();
        final List<Gallery> list = getCurrentSession().createCriteria(Gallery.class).add(
                Restrictions.gt("orderIndex", fromIndex)).add(
                Restrictions.le("orderIndex", toIndex)).list();
        for (Gallery g : list) {
            final int oldIndex = g.getOrderIndex();
            final int newIndex = oldIndex - 1;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("moving gallery " + g.getId() + " from " + oldIndex + " to "
                        + newIndex);
            }
            g.setOrderIndex(newIndex);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("moving gallery " + gallery.getId() + " from " + gallery.getOrderIndex()
                    + " to " + toIndex);
        }
        gallery.setOrderIndex(toIndex);
    }

    /**
     * Moves a gallery up the list, moving galleries down one position as
     * necessary to make room for it.
     * 
     * @param fromIndex the index of the gallery to move
     * @param toIndex the index to move the gallery to
     */
    @SuppressWarnings("unchecked")
    private void moveGalleryUp(final int fromIndex, final int toIndex) {
        final Gallery gallery = (Gallery) getCurrentSession().createCriteria(Gallery.class).add(
                Restrictions.eq("orderIndex", fromIndex)).uniqueResult();
        final List<Gallery> list = getCurrentSession().createCriteria(Gallery.class).add(
                Restrictions.ge("orderIndex", toIndex)).add(
                Restrictions.lt("orderIndex", fromIndex)).list();
        for (Gallery g : list) {
            final int oldIndex = g.getOrderIndex();
            final int newIndex = oldIndex + 1;
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("moving gallery " + g.getId() + " from " + oldIndex + " to "
                        + newIndex);
            }
            g.setOrderIndex(newIndex);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("moving gallery " + gallery.getId() + " from " + gallery.getOrderIndex()
                    + " to " + toIndex);
        }
        gallery.setOrderIndex(toIndex);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Integer> getKeywords(boolean includePrivate) {
        final Query query = getCurrentSession().getNamedQuery("getKeywords");
        query.setBoolean("includePrivate", includePrivate);
        final List<String> keywords = query.list();
        return CollectionUtils.getCardinalityMap(keywords);
    }

    @Override
    public int getImageCountForKeyword(String keyword, boolean includePrivate) {
        final Query query = getCurrentSession().getNamedQuery("countImagesByKeyword");
        query.setString("keyword", keyword);
        query.setBoolean("includePrivate", includePrivate);
        final Number retval = (Number) query.uniqueResult();
        return retval.intValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, CalendarDate> getImageCreationDates(final boolean includePrivate) {
        Map<Long, CalendarDate> retval = new HashMap<Long, CalendarDate>();
        final Query query = getCurrentSession().getNamedQuery("getImageCreationDates");
        query.setBoolean("includePrivate", includePrivate);
        final List<Object[]> results = query.list(); // unchecked
        for (Object[] r : results) {
            retval.put((Long) r[0], (CalendarDate) r[1]);
        }
        return retval;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, Date> getDescriptorCreationDates(boolean includePrivate) {
        final Map<Long, Date> retval = new HashMap<Long, Date>();
        final Query query = getCurrentSession().getNamedQuery("getDescriptorCreationDates");
        query.setBoolean("includePrivate", includePrivate);
        final List<Object[]> results = query.list(); // unchecked
        for (Object[] r : results) {
            retval.put((Long) r[0], (Date) r[1]);
        }
        return retval;
    }

    @Override
    public int getImageCountByDateTaken(int year, Integer month, boolean includePrivate) {
        final Query query = getCurrentSession().getNamedQuery("countImagesByDateTaken");
        query.setInteger("year", year);
        query.setParameter("month", month);
        query.setBoolean("includePrivate", includePrivate);
        final Number retval = (Number) query.uniqueResult();
        return retval.intValue();
    }

    @Override
    public int getImageCountByYearTaken(int year, boolean includePrivate) {
        final Query query = getCurrentSession().getNamedQuery("countImagesByYearTaken");
        query.setInteger("year", year);
        query.setBoolean("includePrivate", includePrivate);
        final Number retval = (Number) query.uniqueResult();
        return retval.intValue();
    }

    @Override
    public int getImageCountByDatePosted(final Date startDate, final Date endDate,
            final boolean includePrivate) {
        final Query query = getCurrentSession().getNamedQuery("countImagesByDatePosted");
        query.setTimestamp("startDate", startDate);
        query.setTimestamp("endDate", endDate);
        query.setBoolean("includePrivate", includePrivate);
        final Number retval = (Number) query.uniqueResult();
        return retval.intValue();
    }

    @Override
    public PhotogalData getData() {
        final PhotogalData data = new PhotogalData();
        data.setExportDate(new Date());
        data.setImageDescriptors(getImageDescriptors());
        data.setGalleries(getGalleries(true));
        return data;
    }

    @Override
    public void delete(Object entity) {
        getCurrentSession().delete(entity);
    }

    @Override
    public void saveOrUpdate(Object... objects) {
        for (Object object : objects) {
            getCurrentSession().saveOrUpdate(object);
        }
    }

    // TODO why do we have this? can't we just call saveOrUpdate()?
    @Override
    public void update(Object object) {
        getCurrentSession().update(object);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ImageDescriptor> getImageDescriptors(String keyword, boolean includePrivate,
            int start, int max) {
        final Query query = getCurrentSession().getNamedQuery("getImagesByKeyword");
        query.setString("keyword", keyword);
        query.setBoolean("includePrivate", includePrivate);
        query.setFirstResult(start);
        query.setMaxResults(max);
        final List<ImageDescriptor> retval = query.list(); // unchecked
        return retval;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ImageDescriptor> getImageDescriptorsByDatePosted(Date startDate, Date endDate,
            boolean includePrivate, int start, int max) {
        final Query query = getCurrentSession().getNamedQuery("getImagesByDatePosted");
        query.setTimestamp("startDate", startDate);
        query.setTimestamp("endDate", endDate);
        query.setBoolean("includePrivate", includePrivate);
        query.setFirstResult(start);
        query.setMaxResults(max);
        final List<ImageDescriptor> retval = query.list(); // unchecked
        return retval;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ImageDescriptor> getImageDescriptorsByDateTaken(int year, Integer month,
            boolean includePrivate, int start, int max) {
        final Query query = getCurrentSession().getNamedQuery("getImagesByDateTaken");
        query.setInteger("year", year);
        query.setParameter("month", month);
        query.setBoolean("includePrivate", includePrivate);
        query.setFirstResult(start);
        query.setMaxResults(max);
        final List<ImageDescriptor> retval = query.list(); // unchecked
        return retval;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ImageDescriptor> getImageDescriptorsByYearTaken(int year, boolean includePrivate,
            int start, int max) {
        final Query query = getCurrentSession().getNamedQuery("getImagesByYearTaken");
        query.setInteger("year", year);
        query.setBoolean("includePrivate", includePrivate);
        query.setFirstResult(start);
        query.setMaxResults(max);
        final List<ImageDescriptor> retval = query.list(); // unchecked
        return retval;
    }

    @Override
    public int deleteAllGalleries() {
        // I'd prefer to execute a single "DELETE FROM Gallery" update but this
        // causes database constraint violations to occur. Perhaps I'll figure
        // out how to deal with that someday.
        final List<Gallery> galleries = getGalleries(true);
        final int retval = galleries.size();
        try {
            for (Gallery gallery : galleries) {
                delete(gallery);
            }
        } finally {
            initializeNextId(Gallery.class);
        }
        return retval;
    }

    @Override
    public int deleteAllImageDescriptors() {
        // I'd prefer to execute a single "DELETE FROM ImageDescriptor" update
        // but this causes database constraint violations to occur. Perhaps I'll
        // figure out how to deal with that someday.
        final List<ImageDescriptor> images = getImageDescriptors();
        final int retval = images.size();
        try {
            for (ImageDescriptor image : images) {
                delete(image);
            }
        } finally {
            initializeNextId(ImageDescriptor.class);
        }
        return retval;
    }

    @Override
    public int getImageDescriptorCount(boolean includePrivate) {
        final Criteria criteria = getCurrentSession().createCriteria(ImageDescriptor.class)
                .setProjection(Projections.rowCount());
        if (!includePrivate) {
            criteria.add(Restrictions.eq("public", Boolean.TRUE));
        }
        final Number retval = (Number) criteria.uniqueResult();
        return retval.intValue();
    }

    @Override
    public void importData(PhotogalData data) {
        final Session session = getCurrentSession();
        try {
            for (ImageDescriptor image : data.getImageDescriptors()) {
                session.replicate(image, ReplicationMode.OVERWRITE);
            }
        } finally {
            initializeNextId(ImageDescriptor.class);
        }
        try {
            for (Gallery gallery : data.getGalleries()) {
                session.replicate(gallery, ReplicationMode.OVERWRITE);
            }
        } finally {
            initializeNextId(Gallery.class);
        }
    }
}