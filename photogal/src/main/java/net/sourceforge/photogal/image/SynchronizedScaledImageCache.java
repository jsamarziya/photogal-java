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
package net.sourceforge.photogal.image;

import java.awt.image.BufferedImage;

/**
 * A subclass of ScaledImageCache that allows only one thread at a time to
 * create a scaled image. This is used to combat OutOfMemory errors that can
 * occur in a multithreaded environment.
 */
public class SynchronizedScaledImageCache extends ScaledImageCache {
    public SynchronizedScaledImageCache() {
        super();
    }

    @Override
    protected BufferedImage createScaledImage(BufferedImage image, String sizeId) {
        synchronized (this) {
            return super.createScaledImage(image, sizeId);
        }
    }
}
