/**
 * Copyright 2015 Poznań Supercomputing and Networking Center
 *
 * Licensed under the GNU General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.gnu.org/licenses/gpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.psnc.synat.sra;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementation of single tuple of a SPARQL select query's result.
 */
public class SparqlSelectTupleImpl implements SparqlSelectTuple {

    /**
     * Map of variable names and values of a tuple.
     */
    private Map<String, String> values;


    /**
     * Constructs a tuple.
     */
    public SparqlSelectTupleImpl() {
        values = new LinkedHashMap<String, String>();
    }


    /**
     * Sets a value of a tuple for a specified name of a variable.
     * 
     * @param name
     *            name of a variable
     * @param value
     *            value
     */
    public void setValue(String name, String value) {
        values.put(name, value);
    }


    @Override
    public String getValue(String name) {
        return values.get(name);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SparqlSelectTupleImpl [values=").append(values).append("]");
        return sb.toString();
    }

}
