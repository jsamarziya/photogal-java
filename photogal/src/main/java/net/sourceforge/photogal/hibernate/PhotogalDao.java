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

package net.sourceforge.photogal.hibernate;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sourceforge.photogal.Gallery;
import net.sourceforge.photogal.ImageDescriptor;
import net.sourceforge.photogal.export.PhotogalData;

import org.sixcats.utils.CalendarDate;

/**
 * Defines the Photogal DAO interface methods.
 */
public interface PhotogalDao {
    /**
     * Returns the galleries, sorted by orderIndex.
     * 
     * @param includePrivate if <code>true</code>, return private galleries too
     * @return the galleries contained in the database
     */
    public List<Gallery> getGalleries(boolean includePrivate);

    /**
     * Returns the number of galleries.
     * 
     * @param includePrivate if <code>true</code>, count private galleries too
     * @return the number of galleries contained in the database
     */
    public int getGalleryCount(boolean includePrivate);

    /**
     * Returns the specified gallery.
     * 
     * @param id the id of the gallery to retrieve
     * @return the specified gallery, or <code>null</code> if no such descriptor
     *         exists
     */
    public Gallery getGallery(long id);

    /**
     * Returns the image descriptors, sorted by id.
     * 
     * @return the image descriptors contained in the database
     */
    public List<ImageDescriptor> getImageDescriptors();

    /**
     * Returns the image descriptors that have the specified keyword.
     * 
     * @param keyword the keyword
     * @param includePrivate if <code>true</code>, return private images too
     * @param start the index of the first image descriptor to return (indexing
     *            starts with 0)
     * @param max the maximum number of image descriptors to return
     * @return a list containing the specified image descriptors
     */
    public List<ImageDescriptor> getImageDescriptors(String keyword, boolean includePrivate,
            int start, int max);

    /**
     * Returns the image descriptors for the images that were posted in the
     * specified date range.
     * 
     * @param startDate the start of the date range
     * @param endDate the end of the date range
     * @param includePrivate if <code>true</code>, return private images too
     * @param start the index of the first image descriptor to return (indexing
     *            starts with 0)
     * @param max the maximum number of image descriptors to return
     * @return a list containing the specified image descriptors
     */
    public List<ImageDescriptor> getImageDescriptorsByDatePosted(Date startDate, Date endDate,
            boolean includePrivate, int start, int max);

    /**
     * Returns the image descriptors for the images that were created in the
     * specified month and year.
     * 
     * @param year the year
     * @param month the month
     * @param includePrivate if <code>true</code>, return private images too
     * @param start the index of the first image descriptor to return (indexing
     *            starts with 0)
     * @param max the maximum number of image descriptors to return
     * @return a list containing the specified image descriptors
     */
    public List<ImageDescriptor> getImageDescriptorsByDateTaken(int year, Integer month,
            boolean includePrivate, int start, int max);

    /**
     * Returns the image descriptors for the images that were created in the
     * specified year.
     * 
     * @param year the year
     * @param includePrivate if <code>true</code>, return private images too
     * @param start the index of the first image descriptor to return (indexing
     *            starts with 0)
     * @param max the maximum number of image descriptors to return
     * @return a list containing the specified image descriptors
     */
    public List<ImageDescriptor> getImageDescriptorsByYearTaken(int year, boolean includePrivate,
            int start, int max);

    /**
     * Returns the specified image descriptor.
     * 
     * @param imageId the id of the descriptor to retrieve
     * @return the specified image descriptor, or <code>null</code> if no such
     *         descriptor exists
     */
    public ImageDescriptor getImageDescriptor(long imageId);

    /**
     * Returns the image descriptor for the specified image file.
     * 
     * @param location the location of the image file
     * @return the descriptor for the specified image file, or <code>null</code>
     *         if no such descriptor exists
     */
    public ImageDescriptor getImageDescriptor(String location);

    public boolean galleryExists(long id);

    /**
     * Returns the number of galleries that contain the specified image.
     * 
     * @param imageId the id of the image
     * @return the number of galleries that contain the specified image
     */
    public int getGalleryCountForImage(long imageId);

    /**
     * Deletes all galleries.
     * 
     * @return the number of galleries deleted
     */
    public int deleteAllGalleries();

    /**
     * Deletes all image descriptors.
     * 
     * @return the number of image descriptors deleted
     */
    public int deleteAllImageDescriptors();

    /**
     * Deletes the specified gallery, optionally deleting image descriptors that
     * are only contained in the gallery.
     * 
     * @param galleryId the id of the gallery
     * @param deleteOrphanImages <code>true</code> if image descriptors that are
     *            contained only in the specified gallery are to be deleted
     */
    public void deleteGallery(long galleryId, boolean deleteOrphanImages);

    /**
     * Moves a gallery from one position to another.
     * 
     * @param fromIndex the orderIndex of the gallery to move
     * @param toIndex the orderIndex to move the gallery to
     */
    public void moveGallery(int fromIndex, int toIndex);

    /**
     * Returns a mapping of keywords to occurence count.
     * 
     * @param includePrivate if <code>true</code>, return keywords for private
     *            images too
     * @return a map whose keys are keywords and whose values are the number of
     *         images that are tagged with the keyword
     */
    public Map<String, Integer> getKeywords(boolean includePrivate);

    /**
     * Returns the number of images that have the specified keyword.
     * 
     * @param keyword the keyword
     * @param includePrivate <code>true</code> if private images are to be
     *            included in the count
     * @return the number of images that have the specified keyword
     */
    public int getImageCountForKeyword(String keyword, boolean includePrivate);

    /**
     * Returns a mapping of image descriptor ids to image creation dates.
     * 
     * @param includePrivate if <code>true</code>, include private images
     */
    public Map<Long, CalendarDate> getImageCreationDates(boolean includePrivate);

    /**
     * Returns a mapping of image descriptor ids to descriptor creation dates.
     * 
     * @param includePrivate if <code>true</code>, include private images
     */
    public Map<Long, Date> getDescriptorCreationDates(boolean includePrivate);

    /**
     * Returns the number of images that were taken in the specified month and
     * year.
     * 
     * @param year the year
     * @param month the month
     * @param includePrivate if true, include private images
     */
    public int getImageCountByDateTaken(int year, Integer month, boolean includePrivate);

    /**
     * Returns the number of images that were taken in the specified year.
     * 
     * @param year the year
     * @param includePrivate if <code>true</code>, include private images
     */
    public int getImageCountByYearTaken(int year, boolean includePrivate);

    /**
     * Returns the number of images posted between the specified dates.
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @param includePrivate <code>true</code> if private images are to be
     *            included in the count
     * @return the number of images posted between the specified dates
     */
    public int getImageCountByDatePosted(Date startDate, Date endDate, boolean includePrivate);

    /**
     * Returns the data contained in the database.
     * 
     * @return the photogal data
     */
    public PhotogalData getData();

    /**
     * Deletes the specified persistent object.
     * 
     * @param object the persistent object to delete
     */
    public void delete(Object object);

    /**
     * Saves or updates the specified objects.
     * 
     * @param objects the objects to save or update
     */
    public void saveOrUpdate(Object... objects);

    /**
     * Updates the specified object.
     * 
     * @param entity the object to update
     */
    public void update(Object object);

    /**
     * Returns the number of image descriptors.
     * 
     * @param includePrivate if <code>true</code>, count private images too
     * @return the number of image descriptors contained in the database
     */
    public int getImageDescriptorCount(boolean includePrivate);

    /**
     * Imports the specified data into the database.
     * 
     * @param data the import data
     */
    public void importData(PhotogalData data);
}