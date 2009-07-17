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
import org.hibernate.Criteria;
import org.hibernate.Query;
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
    }

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
    public int getGalleryCount() {
        final Integer retval = (Integer) getCurrentSession().createCriteria(Gallery.class)
                .setProjection(Projections.rowCount()).uniqueResult();
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
        final Integer retval = (Integer) getCurrentSession().createCriteria(Gallery.class).add(
                Restrictions.eq("id", id)).setProjection(Projections.rowCount()).uniqueResult();
        return retval > 0;
    }

    @Override
    public int getImageGalleryCount(long imageId) {
        final int retval = (Integer) getCurrentSession().getNamedQuery("getImageGalleryCount")
                .setLong("imageId", imageId).uniqueResult();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("image " + imageId + " is contained in " + retval + " galleries");
        }
        return retval;
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

    private void deleteOrphanImages(final Gallery gallery) {
        for (ImageDescriptor descriptor : gallery.getImages()) {
            if (getImageGalleryCount(descriptor.getId()) <= 1) {
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
        final List<String> keywords = getCurrentSession().getNamedQuery(
                includePrivate ? "getAllKeywords" : "getPublicKeywords").list();
        return CollectionUtils.getCardinalityMap(keywords);
    }

    @Override
    public int getKeywordImageCount(String keyword, boolean includePrivate) {
        final Query query = getCurrentSession().getNamedQuery(
                includePrivate ? "countAllImagesByKeyword" : "countPublicImagesByKeyword");
        query.setString("keyword", keyword);
        final Integer retval = (Integer) query.uniqueResult();
        return retval;
    }

    /**
     * Returns a query that returns images that have a specified keyword.
     * 
     * @param keyword the keyword
     * @param includePrivate if <code>true</code>, return private images
     */
    private Query createGetImagesByKeywordQuery(final String keyword, boolean includePrivate) {
        final Query query = getCurrentSession().getNamedQuery(
                includePrivate ? "getAllImagesByKeyword" : "getPublicImagesByKeyword");
        query.setString("keyword", keyword);
        return query;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, CalendarDate> getImageCreationDates(final boolean includePrivate) {
        Map<Long, CalendarDate> retval = new HashMap<Long, CalendarDate>();
        final List<Object[]> results = getCurrentSession().getNamedQuery(
                includePrivate ? "getAllImageCreationDates" : "getPublicImageCreationDates").list(); // unchecked
        for (Object[] r : results) {
            retval.put((Long) r[0], (CalendarDate) r[1]);
        }
        return retval;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<Long, Date> getDescriptorCreationDates(boolean includePrivate) {
        final Map<Long, Date> retval = new HashMap<Long, Date>();
        final List<Object[]> results = getCurrentSession().getNamedQuery(
                includePrivate ? "getAllDescriptorCreationDates"
                        : "getPublicDescriptorCreationDates").list(); // unchecked
        for (Object[] r : results) {
            retval.put((Long) r[0], (Date) r[1]);
        }
        return retval;
    }

    @Override
    public int getImagesByDateTakenCount(int year, Integer month, boolean includePrivate) {
        final Query query = getCurrentSession().getNamedQuery(
                includePrivate ? "countAllImagesByDateTaken" : "countPublicImagesByDateTaken");
        query.setInteger("year", year);
        query.setParameter("month", month);
        final Integer retval = (Integer) query.uniqueResult();
        return retval;
    }

    /**
     * Returns a query that returns images that were taken in the specified
     * month and year.
     * 
     * @param year the year
     * @param month the month
     * @param includePrivate if <code>true</code>, include private images
     */
    private Query createImagesByDateTakenQuery(int year, Integer month, boolean includePrivate) {
        Query query = getCurrentSession().getNamedQuery(
                includePrivate ? "getAllImagesByDateTaken" : "getPublicImagesByDateTaken");
        query.setInteger("year", year);
        query.setParameter("month", month);
        return query;
    }

    @Override
    public int getImagesByYearTakenCount(int year, boolean includePrivate) {
        final Query query = getCurrentSession().getNamedQuery(
                includePrivate ? "countAllImagesByYearTaken" : "countPublicImagesByYearTaken");
        query.setInteger("year", year);
        final Integer retval = (Integer) query.uniqueResult();
        return retval;
    }

    /**
     * Returns a query that returns images that were taken in the specified
     * year.
     * 
     * @param year the year
     * @param includePrivate if true, include private images
     */
    private Query createImagesByYearTakenQuery(int year, boolean includePrivate) {
        final Query query = getCurrentSession().getNamedQuery(
                includePrivate ? "getAllImagesByYearTaken" : "getPublicImagesByYearTaken");
        query.setInteger("year", year);
        return query;
    }

    @Override
    public int getImagesByDatePostedCount(final Date startDate, final Date endDate,
            final boolean includePrivate) {
        final Query query = getCurrentSession().getNamedQuery(
                includePrivate ? "countAllImagesByDatePosted" : "countPublicImagesByDatePosted");
        query.setDate("startDate", startDate);
        query.setDate("endDate", endDate);
        final Integer retval = (Integer) query.uniqueResult();
        return retval;
    }

    private Query createImagesByDatePostedQuery(final Date startDate, final Date endDate,
            final boolean includePrivate) {
        final Query query = getCurrentSession().getNamedQuery(
                includePrivate ? "getAllImagesByDatePosted" : "getPublicImagesByDatePosted");
        query.setDate("startDate", startDate);
        query.setDate("endDate", endDate);
        return query;
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
    public void saveOrUpdate(Object object) {
        getCurrentSession().saveOrUpdate(object);
    }

    @Override
    public void update(Object object) {
        getCurrentSession().update(object);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ImageDescriptor> getImageDescriptors(String keyword, boolean includePrivate,
            int start, int max) {
        final Query query = createGetImagesByKeywordQuery(keyword, includePrivate);
        query.setFirstResult(start);
        query.setMaxResults(max);
        final List<ImageDescriptor> retval = query.list(); // unchecked
        return retval;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ImageDescriptor> getImageDescriptorsByDatePosted(Date startDate, Date endDate,
            boolean includePrivate, int start, int max) {
        final Query query = createImagesByDatePostedQuery(startDate, endDate, includePrivate);
        query.setFirstResult(start);
        query.setMaxResults(max);
        final List<ImageDescriptor> retval = query.list(); // unchecked
        return retval;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ImageDescriptor> getImageDescriptorsByDateTaken(int year, Integer month,
            boolean includePrivate, int start, int max) {
        final Query query = createImagesByDateTakenQuery(year, month, includePrivate);
        query.setFirstResult(start);
        query.setMaxResults(max);
        final List<ImageDescriptor> retval = query.list(); // unchecked
        return retval;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ImageDescriptor> getImageDescriptorsByYearTaken(int year, boolean includePrivate,
            int start, int max) {
        final Query query = createImagesByYearTakenQuery(year, includePrivate);
        query.setFirstResult(start);
        query.setMaxResults(max);
        final List<ImageDescriptor> retval = query.list(); // unchecked
        return retval;
    }
}