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
package net.sourceforge.photogal.web.form;

import java.util.List;
import java.util.Map;


import net.sourceforge.photogal.Gallery;

import org.sixcats.utils.CalendarDate;
import org.sixcats.utils.web.form.BreadcrumbForm;

public class EditImageForm extends BreadcrumbForm {
    private Long imageId;
    private Long galleryId;
    private String location;
    private String title;
    private String description;
    private CalendarDate imageCreationDate;
    private String keywords;
    private boolean isGalleryImage;
    private int width;
    private int height;
    private Map<String, Boolean> availableSizes;
    private List<Gallery> galleries;
    private boolean isPublic;

    public Long getGalleryId() {
        return galleryId;
    }

    public void setGalleryId(Long galleryId) {
        this.galleryId = galleryId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CalendarDate getImageCreationDate() {
        return imageCreationDate;
    }

    public void setImageCreationDate(CalendarDate imageCreationDate) {
        this.imageCreationDate = imageCreationDate;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        if (location == null) {
            this.location = null;
        } else {
            this.location = location.replace('\\', '/');
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isGalleryImage() {
        return isGalleryImage;
    }

    public void setGalleryImage(boolean b) {
        this.isGalleryImage = b;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Returns a map containing information about which sizes the image is
     * available to be displayed in.
     */
    public Map<String, Boolean> getAvailableSizes() {
        return availableSizes;
    }

    /**
     * Sets a map containing information about which sizes the image is
     * available to be displayed in.
     */
    public void setAvailableSizes(Map<String, Boolean> sizes) {
        this.availableSizes = sizes;
    }

    /**
     * Returns the galleries that contain the image.
     */
    public List<Gallery> getGalleries() {
        return galleries;
    }

    /**
     * Sets the galleries that contain the image.
     * 
     * @param galleries
     */
    public void setGalleries(List<Gallery> galleries) {
        this.galleries = galleries;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean b) {
        isPublic = b;

    }
}
