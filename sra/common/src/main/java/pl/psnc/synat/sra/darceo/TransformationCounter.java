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
package pl.psnc.synat.sra.darceo;

import java.util.HashMap;
import java.util.Map;

/**
 * Counter of transformations performed by services.
 */
public class TransformationCounter {

    /**
     * Zeros trace.
     */
    private static final String ZERO_TRACE = "0000";

    /**
     * Counter.
     */
    private Map<String, Integer> counter;


    /**
     * Constructor which set the counter to 0.
     */
    public TransformationCounter() {
        this.counter = new HashMap<String, Integer>();
    }


    /**
     * Gets next value.
     * 
     * @param service
     *            service name
     * @return next value as a string for the specified service.
     * 
     */
    public String getNext(String service) {
        if (!counter.containsKey(service)) {
            counter.put(service, 0);
        }
        int n = counter.get(service);
        String result = Integer.toString(++n);
        result = ZERO_TRACE.substring(result.length()).concat(result);
        counter.put(service, n);
        return result;
    }

}
