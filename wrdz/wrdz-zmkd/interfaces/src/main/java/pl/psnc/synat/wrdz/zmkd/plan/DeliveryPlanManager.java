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

import java.util.List;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.zmkd.ddr.ClientCapabilities;
import pl.psnc.synat.wrdz.zmkd.entity.plan.DeliveryPlan;
import pl.psnc.synat.wrdz.zmkd.entity.plan.DeliveryPlanStatus;

/**
 * Handles advanced data delivery plans.
 */
@Local
public interface DeliveryPlanManager {

    /**
     * Generates plans which can be used to deliver the object with the given identifier to a client with the given
     * capabilities.
     * 
     * @param capabilities
     *            capabilities of the client's device/browser
     * @param objectIdentifier
     *            object identifier
     * @return a list of delivery plans
     */
    List<DeliveryPlan> generateDeliveryPlans(ClientCapabilities capabilities, String objectIdentifier);


    /**
     * Persists the given plan and returns its generated id.
     * 
     * @param plan
     *            the plan to save
     * @return the persisted plan's generated id
     */
    String saveDeliveryPlan(DeliveryPlan plan);


    /**
     * Retrieves the plan with the given id.
     * 
     * @param planId
     *            plan identifier
     * @return plan
     */
    DeliveryPlan getDeliveryPlan(String planId);


    /**
     * Changes the plan's status.
     * 
     * @param planId
     *            plan identifier
     * @param status
     *            new status
     */
    void changeStatus(String planId, DeliveryPlanStatus status);


    /**
     * Sets the client location and complete the plan.
     * 
     * @param planId
     *            plan identifier
     * @param clientLocation
     *            client location
     */
    void completePlan(String planId, String clientLocation);

}
