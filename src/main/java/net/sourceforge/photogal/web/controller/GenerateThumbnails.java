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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import net.sourceforge.photogal.image.ThumbnailGenerator;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class GenerateThumbnails extends AbstractController {
    private static Thread generatorThread;

    private ThumbnailGenerator generator;

    public ThumbnailGenerator getThumbnailGenerator() {
        return generator;
    }

    public void setThumbnailGenerator(ThumbnailGenerator generator) {
        this.generator = generator;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final Map<String, Object> model = new HashMap<String, Object>();
        synchronized (getClass()) {
            if (generatorThread == null) {
                startGenerator();
                model.put("status", "started");
            } else {
                model.put("status", "alreadyRunning");
            }
        }
        return new ModelAndView("edit/generateThumbnailsDone", model);
    }

    private void startGenerator() {
        generatorThread = new Thread(getThumbnailGenerator(),
                                     "Thumbnail generator");
        generatorThread.setDaemon(true);
        generatorThread.setPriority(Thread.MIN_PRIORITY);
        generatorThread.start();
    }
}