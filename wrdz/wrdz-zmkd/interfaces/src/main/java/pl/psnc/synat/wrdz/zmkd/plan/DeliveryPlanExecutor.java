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

import javax.ejb.Local;

/**
 * Starts and restarts delivery plan executions.
 * <p>
 * Delivery plan executor can be notified of the fact that a particular plan is waiting for an object to become
 * available. Waiting status consists of two phases:
 * <ul>
 * <li>unconfirmed wait - indicates that the plan might be waiting for the object and any notifications regarding the
 * object's availability should be recorded; the plan is not automatically restarted if the object becomes available
 * <li>confirmed wait - indicates that the plan's processing has been paused because of the object not being available
 * and it should be restarted as soon as the notification of the object's availability arrives; if such notification
 * arrived during the unconfirmed wait phase, the plan should be restarted immediately
 * </ul>
 * In order to avoid memory leaks, the plan's processor should cancel the unconfirmed wait status if the object turns
 * out to be immediately available and the processing can continue without the need for a restart.
 * <p>
 * The implementation of this interface should be a synchronized singleton, as it is its responsibility to ensure that
 * every delivery plan is processed exactly once.
 */
@Local
public interface DeliveryPlanExecutor {

    /**
     * Begins the execution of the plan with the given id.
     * 
     * @param planId
     *            plan identifier
     */
    void start(String planId);


    /**
     * Puts the plan with the given id in the unconfirmed wait phase, waiting for the object with the given identifier.
     * The plan will not be restarted until it confirms its wait status.
     * 
     * @param planId
     *            plan identifier
     * @param objectIdentifier
     *            object identifier
     */
    void setWait(String planId, String objectIdentifier);


    /**
     * Moves the plan from the unconfirmed wait to the confirmed wait phase. The plan's processing will be restarted as
     * soon as the object becomes available; if the notification of the object's availability came during the
     * unconfirmed wait phase, it will be restarted immediately.
     * <p>
     * Calls to this method <strong>MUST</strong> be preceded by a call to {@link #setWait(String, String)}.
     * 
     * @param planId
     *            plan identifier
     */
    void confirmWait(String planId);


    /**
     * Clears the plan's wait status, indicating that it is no longer waiting for any objects.
     * <p>
     * If the plan is not in either of the wait phases, this method does nothing.
     * 
     * @param planId
     *            plan identifier
     */
    void clearWait(String planId);


    /**
     * Notifies the executor that the object with the given identifier became available.
     * 
     * @param objectIdentifier
     *            object identifier
     */
    void notifyAvailable(String objectIdentifier);
}
