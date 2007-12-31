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

public class ImageDescriptorTest extends TestCase {
    private ImageDescriptor descriptor;

    @Override
    protected void setUp() {
        descriptor = new ImageDescriptor();
    }

    public void testGetKeywords() {
        List<String> keywords = descriptor.getKeywords();
        assertNotNull(keywords);
        assertTrue(keywords.isEmpty());
    }

    public void testSetKeywordsAsString1() {
        descriptor.setKeywordsAsString(null);
        List<String> keywords = descriptor.getKeywords();
        assertNotNull(keywords);
        assertTrue(keywords.isEmpty());
    }

    public void testSetKeywordsAsString2() {
        descriptor.setKeywordsAsString("");
        List<String> keywords = descriptor.getKeywords();
        assertNotNull(keywords);
        assertTrue(keywords.isEmpty());
    }
    
    public void testSetKeywordsAsString3() {
        descriptor.setKeywordsAsString(" ");
        List<String> keywords = descriptor.getKeywords();
        assertNotNull(keywords);
        assertTrue(keywords.isEmpty());
    }

    public void testSetKeywordsAsString4() {
        descriptor.setKeywordsAsString("a keyword");
        List<String> keywords = descriptor.getKeywords();
        assertNotNull(keywords);
        assertEquals(2, keywords.size());
        assertEquals("a", keywords.get(0));
        assertEquals("keyword", keywords.get(1));
    }

}
