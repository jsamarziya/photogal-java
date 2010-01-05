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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.sourceforge.photogal.hibernate.HibernateIdGenerator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.sixcats.utils.CalendarDate;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * An object that contains information about an image.
 */
@XStreamAlias("imageDescriptor")
public class ImageDescriptor {
    @XStreamAsAttribute()
    private Long id;
    private Date creationDate;
    private Date lastModified;
    private String title;
    private String description;
    private String location;
    private int width;
    private int height;
    private CalendarDate imageCreationDate;
    @XStreamOmitField
    private boolean isPublic;
    @XStreamImplicit(itemFieldName = "keyword")
    private List<String> keywords;
    @XStreamOmitField
    private Set<Gallery> galleries;

    public ImageDescriptor() {
        setCreationDate(new Date());
        setPublic(false);
    }

    /**
     * Returns the unique id of this image descriptor.
     * 
     * @return the unique id
     */
    public synchronized Long getId() {
        if (id == null) {
            setId(HibernateIdGenerator.getInstance().getNextId(ImageDescriptor.class));
        }
        return id;
    }

    private void setId(final Long id) {
        this.id = id;
    }

    /**
     * Returns the path to the image file.
     */
    public String getLocation() {
        return location;
    }

    /**
     * Sets the path to the image file.
     * 
     * @param location the filesystem path to the image file
     */
    public void setLocation(final String location) {
        this.location = location;
    }

    /**
     * Returns the width of the image.
     * 
     * @return the width of the image
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the width of the image.
     * 
     * @param width the width
     */
    public void setWidth(final int width) {
        this.width = width;
    }

    /**
     * Returns the height of the image
     * 
     * @return the height of the image
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of the image.
     * 
     * @param height the height of the image
     */
    public void setHeight(final int height) {
        this.height = height;
    }

    public Dimension getSize() {
        return new Dimension(getWidth(), getHeight());
    }

    /**
     * Returns the title of the image.
     * 
     * @return the title of the image
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the image.
     * 
     * @param title the title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Returns the description (caption) of the image.
     * 
     * @return the description (caption) of the image
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description (caption) of the image.
     * 
     * @param description the description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Returns the date that the image was created.
     * 
     * @return the date that the image was created
     */
    public CalendarDate getImageCreationDate() {
        return imageCreationDate;
    }

    /**
     * Sets the date that the image was created.
     * 
     * @param date the creation date
     */
    public void setImageCreationDate(final CalendarDate date) {
        this.imageCreationDate = date;
    }

    /**
     * Returns the keywords associated with this descriptor. This method never
     * returns <code>null</code>.
     * 
     * @return the keywords associated with this descriptor
     */
    public List<String> getKeywords() {
        List<String> retval;
        if (keywords == null) {
            retval = Collections.emptyList();
        } else {
            retval = Collections.unmodifiableList(keywords);
        }
        return retval;
    }

    /**
     * Returns the keywords associated with this descriptor as a space-delimited
     * string.
     */
    public String getKeywordsAsString() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> i = getKeywords().iterator();
        while (i.hasNext()) {
            sb.append(i.next());
            if (i.hasNext()) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    public void setKeywords(final List<String> keywords) {
        if (keywords == null) {
            this.keywords = null;
        } else {
            this.keywords = new ArrayList<String>(keywords);
        }
    }

    /**
     * Sets the keywords as a whitespace-delimited string.
     * 
     * @param keywords the keywords as a whitespace-delimited string
     */
    public void setKeywordsAsString(final String keywords) {
        List<String> keywordList;
        if (keywords == null) {
            keywordList = null;
        } else {
            keywordList = new ArrayList<String>();
            for (String keyword : StringUtils.split(keywords)) {
                keywordList.add(keyword);
            }
        }
        setKeywords(keywordList);
    }

    /**
     * Returns the last modified date for this descriptor.
     */
    public Date getLastModified() {
        return lastModified;
    }

    @SuppressWarnings("unused")
    private void setLastModified(final Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Returns the creation date for this descriptor.
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date for this descriptor.
     * 
     * <p>
     * Note: you do not normally need to call this method - the constructor will
     * set this to the current time when the ImageDescriptor is created.
     * </p>
     * 
     * @param creationDate the creation date
     */
    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isPublic() {
        return isPublic;
    }

    private void setPublic(final boolean isPublic) {
        this.isPublic = isPublic;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", getId())
                .append("title", getTitle()).toString();
    }

    public Set<Gallery> getGalleries() {
        return galleries == null ? null : Collections.unmodifiableSet(galleries);
    }
}
