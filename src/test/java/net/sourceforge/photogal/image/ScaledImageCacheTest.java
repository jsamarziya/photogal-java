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

package net.sourceforge.photogal.image;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sixcats.utils.FileUtils;
import org.sixcats.utils.image.ImageScaler;
import org.sixcats.utils.image.ImageUtils;
import org.sixcats.utils.image.NobelImageScaler;

public class ScaledImageCacheTest {
    private ScaledImageCache cache;
    private File cacheDirectory;
    private ScaledImageCalculator scaledImageCalculator;
    private ImageScaler scaler;

    @Before
    public void setUp() throws IOException {
        cacheDirectory = FileUtils.makeTempDirectory("ScaledImageCacheTest", null);
        scaledImageCalculator = new ScaledImageCalculator();
        final Map<String, Integer> sizeMap = new HashMap<String, Integer>();
        sizeMap.put("a", 10);
        sizeMap.put("b", 25);
        sizeMap.put("c", 33);
        sizeMap.put("d", 80);
        scaledImageCalculator.setMaxSizes(sizeMap);
        scaler = new NobelImageScaler();
        cache = new ScaledImageCache();
        cache.setCacheDirectory(cacheDirectory);
        cache.setScaledImageCalculator(scaledImageCalculator);
        cache.setImageScaler(scaler);
        cache.afterPropertiesSet();
    }

    @After
    public void tearDown() {
        org.apache.commons.io.FileUtils.deleteQuietly(cacheDirectory);
    }

    @Test
    public void testGetScaledImage() throws IOException {
        final File imageFile = new File(
                "target/test-classes/net/sourceforge/photogal/image/cat.jpg");
        assertThat(imageFile.exists(), is(true));
        for (String sizeId : scaledImageCalculator.getMaxSizes().keySet()) {
            assertThat(cache.hasScaledImage(imageFile, sizeId), is(false));
        }
        final File scaledImageFile = cache.getScaledImage(imageFile, "c");
        assertThat(scaledImageFile, is(notNullValue()));
        assertThat(scaledImageFile.exists(), is(true));
        for (String sizeId : scaledImageCalculator.getMaxSizes().keySet()) {
            assertThat(cache.hasScaledImage(imageFile, sizeId), is(true));
        }

        BufferedImage scaledImage = ImageUtils.readImage(scaledImageFile);
        assertThat(scaledImage.getWidth(), is(26));
        assertThat(scaledImage.getHeight(), is(33));

        scaledImage = ImageUtils.readImage(cache.getScaledImage(imageFile, "a"));
        assertThat(scaledImage.getWidth(), is(8));
        assertThat(scaledImage.getHeight(), is(10));
    }
}
