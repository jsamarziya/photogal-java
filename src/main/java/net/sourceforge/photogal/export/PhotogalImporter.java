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

package net.sourceforge.photogal.export;

import java.io.IOException;
import java.io.Reader;

/**
 * An interface implemented by classes that can import Photogal data.
 */
public interface PhotogalImporter {
    /**
     * Imports the specifed photogal data.
     * 
     * @param reader the reader to read the data from
     * @return the imported data
     * @throws IOException if an I/O error occurs
     */
    public PhotogalData importData(Reader reader) throws IOException;
}
