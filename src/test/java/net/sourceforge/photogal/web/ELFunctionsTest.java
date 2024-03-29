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
package net.sourceforge.photogal.web;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class ELFunctionsTest {
    @Test
    public void testPageCountZeroItems() {
        assertEquals(0, ELFunctions.pageCount(0, 10));
    }

    @Test
    public void testPageCountOneItem() {
        for (int itemsPerPage = 1; itemsPerPage < 20; itemsPerPage++) {
            assertEquals("for itemsPerPage=" + itemsPerPage, 1, ELFunctions.pageCount(1,
                    itemsPerPage));
        }
    }

    @Test
    public void testPageCountTwoItems() {
        assertEquals(2, ELFunctions.pageCount(2, 1));
        for (int itemsPerPage = 2; itemsPerPage < 20; itemsPerPage++) {
            assertEquals("for itemsPerPage=" + itemsPerPage, 1, ELFunctions.pageCount(2,
                    itemsPerPage));
        }
    }

    @Test
    public void testPageCountNineItems() {
        assertEquals(9, ELFunctions.pageCount(9, 1));
        assertEquals(5, ELFunctions.pageCount(9, 2));
        assertEquals(3, ELFunctions.pageCount(9, 3));
        assertEquals(3, ELFunctions.pageCount(9, 4));
        assertEquals(2, ELFunctions.pageCount(9, 5));
        assertEquals(2, ELFunctions.pageCount(9, 6));
        assertEquals(2, ELFunctions.pageCount(9, 7));
        assertEquals(2, ELFunctions.pageCount(9, 8));
        for (int itemsPerPage = 9; itemsPerPage < 20; itemsPerPage++) {
            assertEquals("for itemsPerPage=" + itemsPerPage, 1, ELFunctions.pageCount(9,
                    itemsPerPage));
        }
    }

    @Test
    public void testNormalizedPath() {
        assertThat(ELFunctions.normalizedPath(null), is(nullValue()));
        assertThat(ELFunctions.normalizedPath(""), is(""));
        assertThat(ELFunctions.normalizedPath("/"), is("/"));
        assertThat(ELFunctions.normalizedPath("\\"), is("/"));
        assertThat(ELFunctions.normalizedPath("foo"), is("foo"));
        assertThat(ELFunctions.normalizedPath("foo/bar.xml"), is("foo/bar.xml"));
        assertThat(ELFunctions.normalizedPath("foo\\bar.xml"), is("foo/bar.xml"));
    }
}
