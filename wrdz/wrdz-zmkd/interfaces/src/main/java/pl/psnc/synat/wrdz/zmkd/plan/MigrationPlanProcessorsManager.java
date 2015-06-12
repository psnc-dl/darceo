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

import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan;

/**
 * Manages migration plan's processors.
 */
@Local
public interface MigrationPlanProcessorsManager {

    /**
     * Starts (resume) the migration plan processing. Processing will be started (resumed) only if it is not yet
     * running.
     * 
     * @param planId
     *            migration plan id
     * @param force
     *            force the plan to start regardless of its current state
     */
    void start(long planId, boolean force);


    /**
     * Pause the migration plan processing.
     * 
     * @param planId
     *            migration plan id
     */
    void pause(long planId);


    /**
     * Finish the migration plan processing. It cannot be resumed again.
     * 
     * @param planId
     *            migration plan id
     */
    void finish(long planId);


    /**
     * Notifies the processors manager that the digital object for which some processor is waiting for has become ready.
     * It resumes processing of this plan.
     * 
     * @param plan
     *            migration plan
     */
    void notifyObjectAvailable(MigrationPlan plan);

}
