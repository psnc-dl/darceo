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
package pl.psnc.synat.wrdz.zmkd.invocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for values for parameters of the URL query type.
 */
public class ExecutionQueryParam {

    /** Name of the parameter. */
    private String name;

    /** Values of the parameter. */
    private List<String> values;


    public void setName(String name) {
        this.name = name;
    }


    public String getName() {
        return name;
    }


    /**
     * Adds value.
     * 
     * @param value
     *            value
     */
    public void addValue(String value) {
        if (values == null) {
            values = new ArrayList<String>();
        }
        values.add(value);
    }


    public List<String> getValues() {
        return this.values;
    }

}
