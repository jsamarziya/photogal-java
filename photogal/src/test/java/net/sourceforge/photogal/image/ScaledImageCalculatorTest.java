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
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class ScaledImageCalculatorTest {
    private ScaledImageCalculator calculator;
    private Map<String, Integer> maxSizes;

    @Before
    public void setUp() {
        maxSizes = new HashMap<String, Integer>();
        maxSizes.put("a", 10);
        maxSizes.put("b", 50);
        maxSizes.put("c", 350);

        calculator = new ScaledImageCalculator();
        calculator.setMaxSizes(maxSizes);
        calculator.afterPropertiesSet();
    }

    @Test
    public void testAfterPropertiesSet() {
        calculator = new ScaledImageCalculator();
        try {
            calculator.afterPropertiesSet();
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
        calculator.setMaxSizes(maxSizes);
        calculator.afterPropertiesSet();
    }

    @Test
    public void testGetMaxSizes() {
        assertThat(calculator.getMaxSizes(), is(not(sameInstance(maxSizes))));
        assertThat(calculator.getMaxSizes(), is(maxSizes));
    }

    @Test
    public void testIsAvailableSize() {
        try {
            calculator.isAvailableSize("foo", 1, 1);
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
        assertThat(calculator.isAvailableSize("a", 1, 1), is(false));
        assertThat(calculator.isAvailableSize("a", 1, 10), is(false));
        assertThat(calculator.isAvailableSize("a", 1, 11), is(true));
        assertThat(calculator.isAvailableSize("a", 10, 10), is(false));
        assertThat(calculator.isAvailableSize("a", 11, 10), is(true));
        assertThat(calculator.isAvailableSize("a", 11, 11), is(true));
        assertThat(calculator.isAvailableSize("b", 11, 11), is(false));
        assertThat(calculator.isAvailableSize("b", 51, 1), is(true));
    }

    @Test
    public void testGetAvailableSizes() {
        final Map<String, Boolean> expectedSizes = new HashMap<String, Boolean>();
        expectedSizes.put("a", false);
        expectedSizes.put("b", false);
        expectedSizes.put("c", false);
        assertThat(calculator.getAvailableSizes(1, 1), is(expectedSizes));
        expectedSizes.put("a", true);
        assertThat(calculator.getAvailableSizes(11, 1), is(expectedSizes));
        expectedSizes.put("b", true);
        assertThat(calculator.getAvailableSizes(51, 1), is(expectedSizes));
        expectedSizes.put("c", true);
        assertThat(calculator.getAvailableSizes(351, 1), is(expectedSizes));
    }

    @Test
    public void testGetMaxSize() {
        try {
            calculator.getMaxSize("foo");
            fail("IllegalArgumentException expected");
        } catch (IllegalArgumentException expected) {
        }
        assertThat(calculator.getMaxSize("a"), is(10));
        assertThat(calculator.getMaxSize("b"), is(50));
        assertThat(calculator.getMaxSize("c"), is(350));
    }

    @Test
    public void testGetScaledSize() {
        assertThat(calculator.getScaledSize(new Dimension(1, 1), "a"), is(new Dimension(1, 1)));
        assertThat(calculator.getScaledSize(new Dimension(20, 5), "a"), is(new Dimension(10, 3)));
        assertThat(calculator.getScaledSize(new Dimension(30, 10), "a"), is(new Dimension(10, 3)));
        assertThat(calculator.getScaledSize(new Dimension(100, 50), "a"), is(new Dimension(10, 5)));
        assertThat(calculator.getScaledSize(new Dimension(1, 1), "b"), is(new Dimension(1, 1)));
        assertThat(calculator.getScaledSize(new Dimension(100, 20), "b"), is(new Dimension(50, 10)));
    }
}
