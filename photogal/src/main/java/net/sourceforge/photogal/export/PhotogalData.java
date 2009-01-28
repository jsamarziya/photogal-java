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

package net.sourceforge.photogal.export;

import java.util.Date;
import java.util.List;

import net.sourceforge.photogal.Gallery;
import net.sourceforge.photogal.ImageDescriptor;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * A class used to represent the data contained in the Photogal database.
 */
@XStreamAlias("photogal-data")
public class PhotogalData {
    private List<ImageDescriptor> imageDescriptors;
    private List<Gallery> galleries;
    @XStreamAsAttribute
    @XStreamAlias("date")
    private Date exportDate;

    /**
     * Creates a new PhotogalData.
     */
    public PhotogalData() {
        super();
    }

    /**
     * Returns the list of image descriptors.
     * 
     * @return the image descriptors
     */
    public List<ImageDescriptor> getImageDescriptors() {
        return imageDescriptors;
    }

    /**
     * Sets the list of image descriptors.
     * 
     * @param descriptors the image descriptors
     */
    public void setImageDescriptors(List<ImageDescriptor> descriptors) {
        this.imageDescriptors = descriptors;
    }

    /**
     * Returns the list of galleries.
     * 
     * @return the galleries
     */
    public List<Gallery> getGalleries() {
        return galleries;
    }

    /**
     * Sets the list of galleries.
     * 
     * @param galleries the galleries
     */
    public void setGalleries(List<Gallery> galleries) {
        this.galleries = galleries;
    }

    /**
     * Returns the date that this data was exported.
     * 
     * @return the export date
     */
    public Date getExportDate() {
        return exportDate;
    }

    /**
     * Sets the date that this data was exported.
     * 
     * @param exportDate the export date
     */
    public void setExportDate(Date exportDate) {
        this.exportDate = exportDate;
    }
}
