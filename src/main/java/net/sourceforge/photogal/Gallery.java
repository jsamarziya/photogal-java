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
package net.sourceforge.photogal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class Gallery implements Comparable<Gallery> {
    private Long id;
    private List<ImageDescriptor> images;
    private String name;
    private String description;
    private Date lastModified;
    private Date creationDate;
    private ImageDescriptor galleryImage;
    private int orderIndex;
    private boolean isPublic;

    public Gallery() {
        images = new ArrayList<ImageDescriptor>();
        isPublic = false;
        setCreationDate(new Date());
    }

    public Long getId() {
        return id;
    }

    @SuppressWarnings("unused")
    private void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Date getLastModified() {
        return lastModified == null ? null : (Date) lastModified.clone();
    }

    @SuppressWarnings("unused")
    private void setLastModified(final Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Returns the creation date for this gallery.
     */
    public Date getCreationDate() {
        return creationDate;
    }

    private void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }


    public List<ImageDescriptor> getImages() {
        return images == null ? null : Collections.unmodifiableList(images);
    }

    @SuppressWarnings("unused")
    private void setImages(List<ImageDescriptor> images) {
        this.images = images;
    }

    public int getImageCount() {
        return images == null ? 0 : images.size();
    }

    public ImageDescriptor getGalleryImage() {
        return galleryImage;
    }

    public void setGalleryImage(final ImageDescriptor image) {
        galleryImage = image;
    }

    public ImageDescriptor getGalleryImageOrDefault() {
        ImageDescriptor retval = getGalleryImage();
        if (retval == null) {
            retval = getDefaultGalleryImage();
        }
        return retval;
    }

    public ImageDescriptor getDefaultGalleryImage() {
        ImageDescriptor retval = null;
        if (getImageCount() > 0) {
            retval = getImages().get(0);
        }
        return retval;
    }

    /**
     * Returns the ordering index of this gallery. The ordering index is used to
     * determine the order in which galleries are listed.
     * 
     * @return the ordering index
     */
    public int getOrderIndex() {
        return orderIndex;
    }

    /**
     * Sets the ordering index of this gallery.
     * 
     * @param index the ordering index
     */
    public void setOrderIndex(final int index) {
        orderIndex = index;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(final boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void addImage(final ImageDescriptor descriptor) {
        images.add(descriptor);
    }

    public void removeImage(ImageDescriptor imageDescriptor) {
        images.remove(imageDescriptor);
    }

    public boolean containsImage(ImageDescriptor imageDescriptor) {
        return images.contains(imageDescriptor);
    }

    public void moveImage(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            return;
        }
        Validate.isTrue(fromIndex >= 0, "fromIndex cannot be negative: ",
                        fromIndex);
        Validate.isTrue(toIndex >= 0, "toIndex cannot be negative: ", toIndex);
        Validate.isTrue(fromIndex < getImageCount(),
                        "fromIndex must be less than the image count: ",
                        fromIndex);
        Validate.isTrue(toIndex < getImageCount(),
                        "toIndex must be less than the image count: ", toIndex);
        final ImageDescriptor descriptor = images.remove(fromIndex);
        images.add(toIndex, descriptor);
    }

    public boolean isGalleryImage(final ImageDescriptor descriptor) {
        final ImageDescriptor galleryImage = getGalleryImage();
        return galleryImage != null
            && ObjectUtils.equals(galleryImage.getId(), descriptor.getId());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", getId()).append("name", getName()).toString();
    }

    public int compareTo(Gallery o) {
        return new CompareToBuilder()
                .append(getOrderIndex(), o.getOrderIndex()).toComparison();
    }
}
