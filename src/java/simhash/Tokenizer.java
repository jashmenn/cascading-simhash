/*
    Copyright 2010 Nate Murray
 
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
 
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
 
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package simhash;
import java.util.Iterator;

/**
 * An interface for filtering {@link File} objects based on their names
 * or the directory they reside in.
 * 
 */
public interface Tokenizer<T> {

    /**
     * Indicates if a specific filename matches this filter.
     * 
     * @param dir
     *            the directory in which the {@code filename} was found.
     * @param filename
     *            the name of the file in {@code dir} to test.
     * @return  {@code true} if the filename matches the filter
     *            and can be included in the list, {@code false}
     *            otherwise.
     */
    public abstract Iterator<T> tokenize(T input);
}
