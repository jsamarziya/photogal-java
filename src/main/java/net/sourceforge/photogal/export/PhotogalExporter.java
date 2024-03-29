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
import java.io.Writer;

/**
 * An interface implemented by classes that can export Photogal data.
 */
public interface PhotogalExporter {
    /**
     * Exports the specifed photogal data.
     * 
     * @param data the data to export
     * @param writer the writer to write the exported data to
     * @throws IOException if an I/O error occurs
     */
    public void exportData(PhotogalData data, Writer writer) throws IOException;

    /**
     * Exports the specifed photogal data.
     * 
     * @param data the data to export
     * @param writer the writer to write the exported data to
     * @param encoding the character encoding
     * @throws IOException if an I/O error occurs
     */
    public void exportData(PhotogalData data, Writer writer, String encoding) throws IOException;
}
