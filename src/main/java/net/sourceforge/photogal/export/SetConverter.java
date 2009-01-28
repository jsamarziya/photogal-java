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

import java.util.HashSet;
import java.util.Set;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * A {@link Converter} that converts any {@link Set} as a {@link HashSet}.
 */
public class SetConverter implements Converter {
    private static final SetConverter INSTANCE = new SetConverter();

    private SetConverter() {
    }

    /**
     * Returns the singleton instance of this class.
     * 
     * @return the SetConverter
     */
    public static final SetConverter getInstance() {
        return INSTANCE;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean canConvert(Class type) {
        return Set.class.isAssignableFrom(type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
        context.convertAnother(new HashSet<Object>((Set) source));
    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        return context.convertAnother(context.currentObject(), HashSet.class);
    }
}
