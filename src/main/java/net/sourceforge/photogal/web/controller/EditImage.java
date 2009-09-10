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

import java.awt.Dimension;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sourceforge.photogal.Gallery;
import net.sourceforge.photogal.ImageDescriptor;
import net.sourceforge.photogal.image.ImageOperations;
import net.sourceforge.photogal.image.ScaledImageCalculator;
import net.sourceforge.photogal.web.form.EditImageForm;

import org.apache.commons.lang.StringUtils;
import org.sixcats.utils.CalendarDate;
import org.sixcats.utils.FileAccessManager;
import org.sixcats.utils.image.ImageMetadataUtils;
import org.sixcats.utils.image.ImageUtils;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.validation.BindException;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.servlet.ModelAndView;

public class EditImage extends PhotogalDaoAwareFormController {
    private FileAccessManager fileAccessManager;
    private ScaledImageCalculator scaledImageCalculator;

    /**
     * Returns the file access manager used to manage access to image files.
     */
    public FileAccessManager getFileAccessManager() {
        return fileAccessManager;
    }

    /**
     * Sets the file access manager used to manage access to image files.
     */
    public void setFileAccessManager(FileAccessManager fileAccessManager) {
        this.fileAccessManager = fileAccessManager;
    }

    public ScaledImageCalculator getScaledImageCalculator() {
        return scaledImageCalculator;
    }

    public void setScaledImageCalculator(ScaledImageCalculator calculator) {
        this.scaledImageCalculator = calculator;
    }

    @Override
    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
            throws Exception {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
    }

    @Override
    protected ModelAndView showForm(HttpServletRequest request, HttpServletResponse response,
            BindException errors) throws Exception {
        EditImageForm form = (EditImageForm) errors.getTarget();
        ImageDescriptor descriptor;
        if (form.getImageId() == null) {
            if (StringUtils.isEmpty(form.getLocation())) {
                throw new ServletException("no image id or location specified");
            }
            descriptor = getPhotogalDao().getImageDescriptor(form.getLocation());
        } else {
            descriptor = ControllerUtils.getImageDescriptor(getPhotogalDao(), form.getImageId());
        }
        if (descriptor == null) {
            logger.debug("preparing EditImage form for adding image");
            File imageFile = getFileAccessManager().getFile(form.getLocation());
            if (!ImageUtils.isImage(imageFile)) {
                logger.error(imageFile.getCanonicalPath() + " is not a supported image file");
                throw new ServletException("not a supported image file");
            }
            Date imageCreationDate = ImageOperations.getImageCreationDate(imageFile);
            logger.debug("image creation date is " + imageCreationDate);
            if (imageCreationDate != null) {
                form.setImageCreationDate(new CalendarDate(imageCreationDate));
            }
            Dimension d = ImageMetadataUtils.getSize(imageFile);
            form.setWidth(d.width);
            form.setHeight(d.height);
            form.setGalleryImage(false);
            form.setPublic(false);
        } else {
            logger.debug("preparing EditImage form for image \"" + form.getImageId() + "\"");
            form.setImageId(descriptor.getId());
            form.setLocation(descriptor.getLocation());
            form.setTitle(descriptor.getTitle());
            form.setDescription(descriptor.getDescription());
            form.setImageCreationDate(descriptor.getImageCreationDate());
            form.setKeywords(descriptor.getKeywordsAsString());
            form.setWidth(descriptor.getWidth());
            form.setHeight(descriptor.getHeight());
            form.setGalleries(ControllerUtils.getGalleries(descriptor));
            form.setPublic(descriptor.isPublic());
            if (form.getGalleryId() == null) {
                form.setGalleryImage(false);
            } else {
                Gallery gallery = ControllerUtils.getGallery(getPhotogalDao(), form.getGalleryId());
                form.setGalleryImage(gallery.isGalleryImage(descriptor));
            }
        }
        form.setAvailableSizes(getScaledImageCalculator().getAvailableSizes(form.getWidth(),
                form.getHeight()));
        return showForm(request, errors, "edit/editImage");
    }

    @Override
    protected ModelAndView processFormSubmission(HttpServletRequest request,
            HttpServletResponse response, Object command, BindException errors) throws Exception {
        EditImageForm form = (EditImageForm) command;
        String action = request.getParameter("action");
        if (action == null) {
            throw new ServletException("no action specified");
        } else if (action.equals("save")) {
            return handleSaveRequest(form);
        } else if (action.equals("cancel")) {
            return handleCancelRequest(form);
        } else {
            throw new ServletException("Unknown action " + action);
        }
    }

    private ModelAndView handleSaveRequest(final EditImageForm form) throws Exception {
        // TODO check for errors
        ImageDescriptor descriptor;
        if (form.getImageId() == null) {
            descriptor = new ImageDescriptor();
            descriptor.setLocation(form.getLocation());
            File imageFile = getFileAccessManager().getFile(form.getLocation());
            Dimension d = ImageMetadataUtils.getSize(imageFile);
            descriptor.setWidth(d.width);
            descriptor.setHeight(d.height);
        } else {
            descriptor = ControllerUtils.getImageDescriptor(getPhotogalDao(), form.getImageId());
        }
        descriptor.setTitle(form.getTitle());
        descriptor.setDescription(form.getDescription());
        descriptor.setImageCreationDate(form.getImageCreationDate());
        descriptor.setKeywordsAsString(form.getKeywords());

        getPhotogalDao().saveOrUpdate(descriptor);

        boolean imageAddedToGallery = false;
        if (form.getGalleryId() != null) {
            final Gallery gallery = ControllerUtils.getGallery(getPhotogalDao(), form
                    .getGalleryId());
            if (!gallery.containsImage(descriptor)) {
                gallery.addImage(descriptor);
                imageAddedToGallery = true;
                logger.debug("added image descriptor (id=" + descriptor.getId() + ") to gallery "
                        + gallery.getId());
            }
            if (form.isGalleryImage()) {
                gallery.setGalleryImage(descriptor);
            } else {
                if (gallery.isGalleryImage(descriptor)) {
                    gallery.setGalleryImage(null);
                }
            }
            getPhotogalDao().saveOrUpdate(gallery);
        }

        if (form.getImageId() == null) {
            logger.debug("created new image descriptor (id=" + descriptor.getId() + ")");
        } else {
            logger.debug("updated image descriptor (id=" + descriptor.getId() + ")");
        }
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("image", descriptor);
        model.put("editAction", imageAddedToGallery ? "add" : "edit");
        model.put("galleryId", form.getGalleryId());
        return new ModelAndView(form.getReturnTo(), model);
    }

    private ModelAndView handleCancelRequest(final EditImageForm form) throws Exception {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("imageId", form.getImageId());
        model.put("galleryId", form.getGalleryId());
        return new ModelAndView(form.getCancelTo(), model);
    }
}
