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
package pl.psnc.synat.wrdz.zmkd.zdt;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.enterprise.context.SessionScoped;

import pl.psnc.synat.wrdz.zmkd.entity.plan.DeliveryPlan;

/**
 * Session cache that stores generated delivery plans.
 * <p>
 * Can store up to {@value #SIZE} entries simultaneously. Oldest entries are removed if this limit is exceeded.
 */
@ManagedBean
@SessionScoped
public class DeliveryPlanCache implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 6863452857308140354L;

    /** Maximum number of entries. */
    private static final int SIZE = 10;

    /** Storage map. */
    private final LinkedHashMap<String, List<DeliveryPlan>> map;


    /**
     * Default constructor.
     */
    public DeliveryPlanCache() {

        map = new LinkedHashMap<String, List<DeliveryPlan>>((int) Math.ceil(SIZE + 1 / 0.75f), 0.75f) {

            /** Serial version UID. */
            private static final long serialVersionUID = -779133642680240256L;


            @Override
            protected boolean removeEldestEntry(java.util.Map.Entry<String, List<DeliveryPlan>> eldest) {
                return size() > SIZE;
            }
        };
    }


    /**
     * Adds an entry to the cache.
     * 
     * @param key
     *            entry key
     * @param plans
     *            list of delivery plans
     */
    public synchronized void put(String key, List<DeliveryPlan> plans) {
        map.put(key, plans);
    }


    /**
     * Retrieves and removes an entry from the cache.
     * 
     * @param key
     *            entry key
     * @return list of delivery plans, or <code>null</code> if no entry with the given key could be found
     */
    public synchronized List<DeliveryPlan> fetch(String key) {
        List<DeliveryPlan> plans = null;
        if (map.containsKey(key)) {
            plans = map.get(key);
            map.remove(key);
        }
        return plans;
    }
}
