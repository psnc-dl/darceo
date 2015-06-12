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
package pl.psnc.synat.wrdz.zmkd.plan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * Default implementation of {@link DeliveryPlanExecutor}.
 */
@Singleton
@Startup
public class DeliveryPlanExecutorBean implements DeliveryPlanExecutor {

    /** Delivery plan processor. */
    @EJB
    private DeliveryPlanProcessor planProcessor;

    /** Lookup map: objectIdentifier -> ids of plans that are waiting for that object. */
    private final Map<String, Set<String>> objectToPlans = new HashMap<String, Set<String>>();

    /** Lookup map: planId -> objectIdentifier. */
    private final Map<String, String> planToObject = new HashMap<String, String>();

    /** Plans that have confirmed their waiting status. */
    private final Set<String> confirmed = new HashSet<String>();

    /** Plans that are ready to be restarted as soon as they confirm their waiting status. */
    private final Set<String> ready = new HashSet<String>();


    @Override
    public synchronized void start(String planId) {
        planProcessor.process(planId);
    }


    @Override
    public synchronized void setWait(String planId, String objectIdentifier) {

        if (!objectToPlans.containsKey(objectIdentifier)) {
            objectToPlans.put(objectIdentifier, new HashSet<String>());
        }
        objectToPlans.get(objectIdentifier).add(planId);
        planToObject.put(planId, objectIdentifier);
    }


    @Override
    public synchronized void confirmWait(String planId) {

        if (ready.contains(planId)) {
            ready.remove(planId);
            start(planId);
        } else {
            confirmed.add(planId);
        }
    }


    @Override
    public synchronized void clearWait(String planId) {

        ready.remove(planId);
        confirmed.remove(planId);

        String objectIdentifier = planToObject.remove(planId);
        if (objectIdentifier != null) {
            Set<String> plans = objectToPlans.get(objectIdentifier);
            plans.remove(planId);
            if (plans.isEmpty()) {
                objectToPlans.remove(objectIdentifier);
            }
        }
    }


    @Override
    public synchronized void notifyAvailable(String objectIdentifier) {

        Set<String> plans = objectToPlans.get(objectIdentifier);
        if (plans != null) {
            for (Iterator<String> i = plans.iterator(); i.hasNext();) {
                String planId = i.next();

                i.remove();
                planToObject.remove(planId);

                if (confirmed.contains(planId)) {
                    confirmed.remove(planId);
                    start(planId);
                } else {
                    ready.add(planId);
                }
            }
            if (plans.isEmpty()) {
                objectToPlans.remove(objectIdentifier);
            }
        }
    }
}
