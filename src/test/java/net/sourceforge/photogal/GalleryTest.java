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

import java.util.List;

import junit.framework.TestCase;

public class GalleryTest extends TestCase {
    private Gallery gallery;
    private static final ImageDescriptor DESCRIPTOR1 = new ImageDescriptor();
    private static final ImageDescriptor DESCRIPTOR2 = new ImageDescriptor();
    private static final ImageDescriptor DESCRIPTOR3 = new ImageDescriptor();
    private static final ImageDescriptor DESCRIPTOR4 = new ImageDescriptor();

    static {
        DESCRIPTOR1.setTitle("DESCRIPTOR 1");
        DESCRIPTOR2.setTitle("DESCRIPTOR 2");
        DESCRIPTOR3.setTitle("DESCRIPTOR 3");
        DESCRIPTOR4.setTitle("DESCRIPTOR 4");
    }

    @Override
    protected void setUp() {
        gallery = new Gallery();
        gallery.addImage(DESCRIPTOR1);
        gallery.addImage(DESCRIPTOR2);
        gallery.addImage(DESCRIPTOR3);
        gallery.addImage(DESCRIPTOR4);
    }

    public void testMoveImage1() {
        gallery.moveImage(0, 1);
        List<ImageDescriptor> images = gallery.getImages();
        assertSame(DESCRIPTOR2, images.get(0));
        assertSame(DESCRIPTOR1, images.get(1));
        assertSame(DESCRIPTOR3, images.get(2));
        assertSame(DESCRIPTOR4, images.get(3));
    }

    public void testMoveImage2() {
        gallery.moveImage(1, 3);
        List<ImageDescriptor> images = gallery.getImages();
        assertSame(DESCRIPTOR1, images.get(0));
        assertSame(DESCRIPTOR3, images.get(1));
        assertSame(DESCRIPTOR4, images.get(2));
        assertSame(DESCRIPTOR2, images.get(3));
    }

    public void testMoveImage3() {
        gallery.moveImage(1, 0);
        List<ImageDescriptor> images = gallery.getImages();
        assertSame(DESCRIPTOR2, images.get(0));
        assertSame(DESCRIPTOR1, images.get(1));
        assertSame(DESCRIPTOR3, images.get(2));
        assertSame(DESCRIPTOR4, images.get(3));
    }

    public void testMoveImage4() {
        gallery.moveImage(3,1);
        List<ImageDescriptor> images = gallery.getImages();
        assertSame(DESCRIPTOR1, images.get(0));
        assertSame(DESCRIPTOR4, images.get(1));
        assertSame(DESCRIPTOR2, images.get(2));
        assertSame(DESCRIPTOR3, images.get(3));
    }
}
