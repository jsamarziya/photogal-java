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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.sixcats.utils.CalendarDate;
import org.sixcats.utils.hibernate.HibernateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of EntityManager that uses Hibernate to access the photogal
 * data.
 */
public class HibernateEntityManager implements EntityManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(HibernateEntityManager.class);

    private static final HibernateEntityManager INSTANCE = new HibernateEntityManager();

    private HibernateEntityManager() {
    }

    public static HibernateEntityManager getInstance() {
        return INSTANCE;
    }

    private Session getSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    @SuppressWarnings("unchecked")
    public List<Gallery> getGalleries(final boolean includePrivate) {
        final Criteria criteria = getSession().createCriteria(Gallery.class).addOrder(
                Order.asc("orderIndex"));
        if (!includePrivate) {
            criteria.add(Restrictions.eq("public", Boolean.TRUE));
        }
        List<Gallery> retval = criteria.list();
        return retval;
    }

    public int getGalleryCount() {
        Integer retval = (Integer) getSession().createCriteria(Gallery.class).setProjection(
                Projections.rowCount()).uniqueResult();
        return retval.intValue();
    }

    public Gallery getGallery(final long id) {
        Gallery retval = (Gallery) getSession().createCriteria(Gallery.class).add(
                Restrictions.eq("id", id)).uniqueResult();
        return retval;
    }

    @SuppressWarnings("unchecked")
    public List<ImageDescriptor> getImageDescriptors() {
        final Criteria criteria = getSession().createCriteria(ImageDescriptor.class).addOrder(
                Order.asc("id"));
        List<ImageDescriptor> retval = criteria.list();
        return retval;
    }

    public ImageDescriptor getImageDescriptor(final long imageId) {
        ImageDescriptor retval = (ImageDescriptor) getSession().createCriteria(
                ImageDescriptor.class).add(Restrictions.eq("id", imageId)).uniqueResult();
        return retval;
    }

    public ImageDescriptor getImageDescriptor(final String location) {
        ImageDescriptor retval = (ImageDescriptor) getSession().createCriteria(
                ImageDescriptor.class).add(Restrictions.eq("location", location)).uniqueResult();
        return retval;
    }

    public boolean galleryExists(final long id) {
        Integer retval = (Integer) getSession().createCriteria(Gallery.class).add(
                Restrictions.eq("id", id)).setProjection(Projections.rowCount()).uniqueResult();
        return retval > 0;
    }

    public int getImageGalleryCount(long imageId) {
        int retval = (Integer) getSession().getNamedQuery("getImageGalleryCount").setLong(
                "imageId", imageId).uniqueResult();
        LOGGER.debug("image " + imageId + " is contained in " + retval + " galleries");
        return retval;
    }

    @SuppressWarnings("unchecked")
    public void deleteGallery(final long galleryId, boolean deleteOrphanImages) {
        final Gallery gallery = getGallery(galleryId);
        if (deleteOrphanImages) {
            deleteOrphanImages(gallery);
        }
        getSession().delete(gallery);
        List<Gallery> list = getSession().createCriteria(Gallery.class).add(
                Restrictions.gt("orderIndex", gallery.getOrderIndex())).list();
        for (Gallery g : list) {
            final int oldIndex = g.getOrderIndex();
            final int newIndex = oldIndex - 1;
            LOGGER.debug("moving gallery#" + g.getId() + " from " + oldIndex + " to " + newIndex);
            g.setOrderIndex(newIndex);
        }
    }

    private void deleteOrphanImages(final Gallery gallery) {
        for (ImageDescriptor descriptor : gallery.getImages()) {
            if (getImageGalleryCount(descriptor.getId()) <= 1) {
                LOGGER.debug("image " + descriptor.getId() + " is now orphaned, deleting it");
                getSession().delete(descriptor);
            }
        }
    }

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
        final Gallery gallery = (Gallery) getSession().createCriteria(Gallery.class).add(
                Restrictions.eq("orderIndex", fromIndex)).uniqueResult();
        List<Gallery> list = getSession().createCriteria(Gallery.class).add(
                Restrictions.gt("orderIndex", fromIndex)).add(
                Restrictions.le("orderIndex", toIndex)).list();
        for (Gallery g : list) {
            final int oldIndex = g.getOrderIndex();
            final int newIndex = oldIndex - 1;
            LOGGER.debug("moving gallery#" + g.getId() + " from " + oldIndex + " to " + newIndex);
            g.setOrderIndex(newIndex);
        }
        LOGGER.debug("moving gallery#" + gallery.getId() + " from " + gallery.getOrderIndex()
                + " to " + toIndex);
        gallery.setOrderIndex(toIndex);
    }

    @SuppressWarnings("unchecked")
    private void moveGalleryUp(final int fromIndex, final int toIndex) {
        final Gallery gallery = (Gallery) getSession().createCriteria(Gallery.class).add(
                Restrictions.eq("orderIndex", fromIndex)).uniqueResult();
        final List<Gallery> list = getSession().createCriteria(Gallery.class).add(
                Restrictions.ge("orderIndex", toIndex)).add(
                Restrictions.lt("orderIndex", fromIndex)).list();
        for (Gallery g : list) {
            final int oldIndex = g.getOrderIndex();
            final int newIndex = oldIndex + 1;
            LOGGER.debug("moving gallery#" + g.getId() + " from " + oldIndex + " to " + newIndex);
            g.setOrderIndex(newIndex);
        }
        LOGGER.debug("moving gallery#" + gallery.getId() + " from " + gallery.getOrderIndex()
                + " to " + toIndex);
        gallery.setOrderIndex(toIndex);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Integer> getKeywords(boolean includePrivate) {
        final List<String> keywords = getSession().getNamedQuery(
                includePrivate ? "getAllKeywords" : "getPublicKeywords").list();
        return CollectionUtils.getCardinalityMap(keywords);
    }

    public int getKeywordImageCount(String keyword, boolean includePrivate) {
        Query query = getSession().getNamedQuery(
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
    public Query createGetImagesByKeywordQuery(final String keyword, boolean includePrivate) {
        Query query = getSession().getNamedQuery(
                includePrivate ? "getAllImagesByKeyword" : "getPublicImagesByKeyword");
        query.setString("keyword", keyword);
        return query;
    }

    @SuppressWarnings("unchecked")
    public Map<Long, CalendarDate> getImageCreationDates(final boolean includePrivate) {
        Map<Long, CalendarDate> retval = new HashMap<Long, CalendarDate>();
        final List<Object[]> results = getSession().getNamedQuery(
                includePrivate ? "getAllImageCreationDates" : "getPublicImageCreationDates").list();
        for (Object[] r : results) {
            retval.put((Long) r[0], (CalendarDate) r[1]);
        }
        return retval;
    }

    @SuppressWarnings("unchecked")
    public Map<Long, Date> getDescriptorCreationDates(boolean includePrivate) {
        Map<Long, Date> retval = new HashMap<Long, Date>();
        final List<Object[]> results = getSession().getNamedQuery(
                includePrivate ? "getAllDescriptorCreationDates"
                        : "getPublicDescriptorCreationDates").list();
        for (Object[] r : results) {
            retval.put((Long) r[0], (Date) r[1]);
        }
        return retval;
    }

    public int getImagesByDateTakenCount(int year, Integer month, boolean includePrivate) {
        Query query = getSession().getNamedQuery(
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
     * @param includePrivate if true, include private images
     */
    public Query createImagesByDateTakenQuery(int year, Integer month, boolean includePrivate) {
        Query query = getSession().getNamedQuery(
                includePrivate ? "getAllImagesByDateTaken" : "getPublicImagesByDateTaken");
        query.setInteger("year", year);
        query.setParameter("month", month);
        return query;
    }

    public int getImagesByYearTakenCount(int year, boolean includePrivate) {
        Query query = getSession().getNamedQuery(
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
    public Query createImagesByYearTakenQuery(int year, boolean includePrivate) {
        Query query = getSession().getNamedQuery(
                includePrivate ? "getAllImagesByYearTaken" : "getPublicImagesByYearTaken");
        query.setInteger("year", year);
        return query;
    }

    public int getImagesByDatePostedCount(final Date startDate, final Date endDate,
            final boolean includePrivate) {
        final Query query = getSession().getNamedQuery(
                includePrivate ? "countAllImagesByDatePosted" : "countPublicImagesByDatePosted");
        query.setDate("startDate", startDate);
        query.setDate("endDate", endDate);
        final Integer retval = (Integer) query.uniqueResult();
        return retval;
    }

    public Query createImagesByDatePostedQuery(final Date startDate, final Date endDate,
            final boolean includePrivate) {
        final Query query = getSession().getNamedQuery(
                includePrivate ? "getAllImagesByDatePosted" : "getPublicImagesByDatePosted");
        query.setDate("startDate", startDate);
        query.setDate("endDate", endDate);
        return query;
    }

    public PhotogalData getData() {
        final PhotogalData data = new PhotogalData();
        data.setExportDate(new Date());
        data.setImageDescriptors(getImageDescriptors());
        data.setGalleries(getGalleries(true));
        return data;
    }
}