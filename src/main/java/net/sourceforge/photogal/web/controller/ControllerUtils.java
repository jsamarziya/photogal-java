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
package net.sourceforge.photogal.web.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sourceforge.photogal.Gallery;
import net.sourceforge.photogal.ImageDescriptor;
import net.sourceforge.photogal.hibernate.HibernateEntityManager;


/**
 * Convenience methods for web controller implementations.
 */
public class ControllerUtils {
    private static final String PARAM_GALLERY_ID = "galleryId";
    private static final String PARAM_IMAGE_ID = "imageId";

    private ControllerUtils() {
    }

    public static boolean hasGalleryId(final HttpServletRequest request) {
        return request.getParameter(PARAM_GALLERY_ID) != null;
    }

    public static boolean hasImageId(final HttpServletRequest request) {
        return request.getParameter(PARAM_IMAGE_ID) != null;
    }

    /**
     * Returns the gallery id identified by the request parameter "galleryId".
     */
    public static long getGalleryId(final HttpServletRequest request) {
        return getGalleryId(request.getParameter(PARAM_GALLERY_ID));
    }

    /**
     * Parses the specified string as a gallery id.
     * 
     * @throws IllegalArgumentException if galleryId is not parseable as a long
     */
    public static long getGalleryId(final String galleryId) {
        if (galleryId == null) {
            throw new IllegalArgumentException("galleryId not specified");
        }
        long id;
        try {
            id = Long.parseLong(galleryId);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("invalid galleryId \""
                + galleryId + "\"");
        }
        return id;
    }

    /**
     * Returns the gallery identified by the request parameter "galleryId".
     */
    public static Gallery getGallery(final HttpServletRequest request) {
        return getGallery(getGalleryId(request));
    }

    /**
     * Returns the gallery identified by the specified id
     * 
     * @param galleryId the id of the gallery to return
     * @throws IllegalArgumentException if galleryId is not a long or if no such
     * gallery exists
     */
    public static Gallery getGallery(final String galleryId) {
        return getGallery(getGalleryId(galleryId));
    }

    public static Gallery getGallery(final long galleryId) {
        final Gallery gallery = HibernateEntityManager.getInstance()
                .getGallery(galleryId);
        if (gallery == null) {
            throw new IllegalArgumentException("gallery " + galleryId
                + " not found");
        }
        return gallery;
    }

    /**
     * Returns a sorted list of galleries that contain a specified image.
     */
    public static List<Gallery> getGalleries(final ImageDescriptor descriptor) {
        final Set<Gallery> galleries = descriptor.getGalleries();
        ArrayList<Gallery> retval = new ArrayList<Gallery>();
        if (galleries != null) {
            retval.addAll(galleries);
        }
        Collections.sort(retval);
        return retval;
    }

    public static long getImageId(final HttpServletRequest request) {
        return getImageId(request.getParameter(PARAM_IMAGE_ID));
    }

    public static long getImageId(final String imageId) {
        if (imageId == null) {
            throw new IllegalArgumentException("imageId not specified");
        }
        long id;
        try {
            id = Long.parseLong(imageId);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("invalid imageId \"" + imageId
                + "\"");
        }
        return id;
    }

    /**
     * Returns the image descriptor identified by the request parameter
     * "imageId".
     */
    public static ImageDescriptor getImageDescriptor(
            final HttpServletRequest request) {
        return getImageDescriptor(getImageId(request));
    }

    /**
     * Returns the image descriptor identified by the specified id
     * 
     * @param imageId the id of the image descriptor to return
     * @throws IllegalArgumentException if imageId is not a long or if no such
     * image descriptor exists
     */
    public static ImageDescriptor getImageDescriptor(final String imageId) {
        return getImageDescriptor(getImageId(imageId));
    }

    public static ImageDescriptor getImageDescriptor(final long imageId) {
        final ImageDescriptor descriptor = HibernateEntityManager.getInstance()
                .getImageDescriptor(imageId);
        if (descriptor == null) {
            throw new IllegalArgumentException("image " + imageId
                + " not found");
        }
        return descriptor;
    }

    public static boolean canViewPrivate(HttpServletRequest request) {
        return canEdit(request);
    }

    public static boolean canEdit(HttpServletRequest request) {
        return request.isUserInRole("editor");
    }
}
