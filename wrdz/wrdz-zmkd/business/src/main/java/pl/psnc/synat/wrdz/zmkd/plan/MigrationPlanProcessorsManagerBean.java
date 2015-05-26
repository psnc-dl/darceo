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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlanStatus;

/**
 * Manager of migration plan processors.
 * 
 * Manages the processing performed by migration plan's processors by providing means to start, pause, resume and
 * terminate them.
 */
@Singleton
@Startup
public class MigrationPlanProcessorsManagerBean implements MigrationPlanProcessorsManager {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MigrationPlanProcessorsManagerBean.class);

    /** Migration plan processor. */
    @EJB
    private MigrationPlanProcessor migrationPlanProcessor;

    /** Migration plan's manager. */
    @EJB
    private MigrationPlanManager migrationPlanManager;

    /**
     * Future objects returned by the called {@link MigrationPlanProcessor#processAll} method of migration plan
     * processors.
     */
    private ConcurrentHashMap<Long, Future<Void>> processingResults = new ConcurrentHashMap<Long, Future<Void>>();


    @Override
    public synchronized void start(long planId, boolean force) {
        logger.debug("MigrationPlanProcessorsManagerBean.start " + planId);
        try {
            if (force || migrationPlanManager.isStartable(planId)) {
                migrationPlanManager.logStarted(planId);
                Future<Void> processingResult = processingResults.get(planId);
                if (processingResult == null || processingResult.isDone()) {
                    processingResult = migrationPlanProcessor.processAll(planId);
                    processingResults.put(planId, processingResult);
                }
            }
        } catch (Exception e) {
            logger.error("Exception while starting the plan " + planId, e);
            Future<Void> processingResult = processingResults.remove(planId);
            if (processingResult != null) {
                processingResult.cancel(true);
            }
        }
    }


    @Override
    public synchronized void pause(long planId) {
        logger.debug("MigrationPlanProcessorsManagerBean.pause " + planId);
        try {
            if (migrationPlanManager.isPausable(planId)) {
                migrationPlanManager.logPaused(planId);
                Future<Void> processingResult = processingResults.get(planId);
                if (processingResult != null) {
                    processingResult.cancel(true);
                }
            }
        } catch (Exception e) {
            logger.error("Exception while pausing the plan " + planId, e);
            Future<Void> processingResult = processingResults.remove(planId);
            if (processingResult != null) {
                processingResult.cancel(true);
            }
        }
    }


    @Override
    public synchronized void finish(long planId) {
        logger.debug("MigrationPlanProcessorsManagerBean.finish " + planId);
        try {
            if (migrationPlanManager.isFinishable(planId)) {
                migrationPlanManager.logFinished(planId);
                Future<Void> processingResult = processingResults.get(planId);
                if (processingResult != null) {
                    processingResult.cancel(true);
                }
            }
        } catch (Exception e) {
            logger.error("Exception while finishing the plan " + planId, e);
            Future<Void> processingResult = processingResults.remove(planId);
            if (processingResult != null) {
                processingResult.cancel(true);
            }
        }
    }


    @Override
    public synchronized void notifyObjectAvailable(MigrationPlan plan) {
        logger.debug("MigrationPlanProcessorsManagerBean.notifyObjectAvailable " + plan.getId());
        migrationPlanManager.clearWaitingForObject(plan.getId());
        try {
            if (plan.getStatus().equals(MigrationPlanStatus.RUNNING)) {
                Future<Void> processingResult = processingResults.get(plan.getId());
                if (processingResult == null || processingResult.isDone()) {
                    processingResult = migrationPlanProcessor.processAll(plan.getId());
                    processingResults.put(plan.getId(), processingResult);
                }
            }
        } catch (Exception e) {
            logger.error("Exception while resuming the plan " + plan.getId(), e);
            Future<Void> processingResult = processingResults.remove(plan.getId());
            if (processingResult != null) {
                processingResult.cancel(true);
            }
        }
    }

}
