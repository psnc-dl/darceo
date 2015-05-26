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

import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlanStatus;
import pl.psnc.synat.wrdz.zmkd.plan.validation.InvalidFormatException;
import pl.psnc.synat.wrdz.zmkd.plan.validation.InvalidObjectException;
import pl.psnc.synat.wrdz.zmkd.plan.validation.InvalidPathException;
import pl.psnc.synat.wrdz.zmkd.plan.validation.NoObjectsException;
import pl.psnc.synat.wrdz.zmkd.plan.validation.NoPathException;

/**
 * Specifies interface of migration plan manager.
 */
@Local
public interface MigrationPlanManager {

    /**
     * Creates a new migration plan based upon a parsed plan provided in an XML file.
     * 
     * @param migrationPlan
     *            parsed plan provided in an XML file
     * @return persisted migration plan
     * @throws InvalidFormatException
     *             if any of the included format puids are invalid
     * @throws InvalidObjectException
     *             if a non-existent object, or one that does not include any files in the plan's input format was
     *             designated for migration
     * @throws InvalidPathException
     *             if the specified migration path includes unknown services or the specified formats do not match the
     *             service descriptors
     * @throws NoObjectsException
     *             if there are no objects the plan can be applied to
     * @throws NoPathException
     *             if no valid migration path could be constructed from known services
     */
    MigrationPlan createMigrationPlan(pl.psnc.darceo.migration.MigrationPlan migrationPlan)
            throws InvalidFormatException, InvalidObjectException, InvalidPathException, NoObjectsException,
            NoPathException;


    /**
     * Returns list of filtered migration plans.
     * 
     * @param status
     *            status filter value
     * @return list of filtered migration plans.
     * 
     */
    List<MigrationPlan> getMigrationPlans(MigrationPlanStatus status);


    /**
     * Returns list of all migration plans.
     * 
     * @return list of all migration plans
     */
    List<MigrationPlan> getMigrationPlans();


    /**
     * Returns list of migration plans that belong to the given user.
     * 
     * @param username
     *            name of the plan owner
     * @return list of migration plans that belong to the given user
     */
    List<MigrationPlan> getMigrationPlans(String username);


    /**
     * Returns migration plan entity by specified id.
     * 
     * @param migrationPlanId
     *            entity id
     * @return migration plan entity.
     * @throws MigrationPlanNotFoundException
     *             if entity was not found
     */
    MigrationPlan getMigrationPlanById(long migrationPlanId)
            throws MigrationPlanNotFoundException;


    /**
     * Logs that a processor of the migration plan is waiting for a digital object.
     * 
     * @param planId
     *            migration plan id
     * @param objectIdentifier
     *            digital object identifier
     */
    void logWaitingForObject(long planId, String objectIdentifier);


    /**
     * Clear the flag that a processor of the migration plan is waiting for a digital object.
     * 
     * @param planId
     *            migration plan id
     */
    void clearWaitingForObject(long planId);


    /**
     * Finds migration plans that are waiting for a digital object with the specified identifier.
     * 
     * @param objectIdentifier
     *            digital object identifier
     * @return list of migration plans
     */
    List<MigrationPlan> findPlansWaitingForObject(String objectIdentifier);


    /**
     * Checks if plan can be started.
     * 
     * @param planId
     *            migration plan id
     * @return whether a plan can be started
     */
    boolean isStartable(long planId);


    /**
     * Logs that the plan had started.
     * 
     * @param planId
     *            migration plan id
     */
    void logStarted(long planId);


    /**
     * Checks if plan can be paused.
     * 
     * @param planId
     *            migration plan id
     * @return whether a plan can be paused
     */
    boolean isPausable(long planId);


    /**
     * Logs that the plan had paused.
     * 
     * @param planId
     *            migration plan id
     */
    void logPaused(long planId);


    /**
     * Checks if plan can be finished.
     * 
     * @param planId
     *            migration plan id
     * @return whether a plan can be finished
     */
    boolean isFinishable(long planId);


    /**
     * Logs that the plan had finished.
     * 
     * @param planId
     *            migration plan id
     */
    void logFinished(long planId);


    /**
     * Deletes migration plan specified by id.
     * 
     * @param planId
     *            specified entity id
     * @throws MigrationPlanDeletionException
     *             if manager was unable to delete entity
     */
    void deletePlan(long planId)
            throws MigrationPlanDeletionException;


    /**
     * Sets the active migration path.
     * 
     * @param planId
     *            migration plan id
     * @param pathId
     *            migration path id
     * @throws MigrationPlanNotFoundException
     *             when the plan with the given id does not exist
     * @throws MigrationPlanStateException
     *             when the plan with the given id is neither NEW nor READY
     * @throws MigrationPathNotFoundException
     *             if the given plan does not include a path with the given id
     */
    void setActivePath(long planId, long pathId)
            throws MigrationPlanNotFoundException, MigrationPlanStateException, MigrationPathNotFoundException;
}
