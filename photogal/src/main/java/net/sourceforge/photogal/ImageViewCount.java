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

public class ImageViewCount {
    private Long id;
    private ImageDescriptor image;
    private int viewCount;

    public ImageViewCount() {
        viewCount = 0;
    }

    public Long getId() {
        return id;
    }

    @SuppressWarnings("unused")
    private void setId(final Long id) {
        this.id = id;
    }

    public void setImage(final ImageDescriptor image) {
        this.image = image;
    }

    public ImageDescriptor getImage() {
        return image;
    }

    /**
     * Returns the number of times the image has been viewed.
     * 
     * @return the number of times the image has been viewed
     */
    public int getViewCount() {
        return viewCount;
    }

    /**
     * Increments the view counter.
     */
    public void incrementViewCount() {
        viewCount++;
    }
}
