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
package pl.psnc.synat.wrdz.zmkd;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.zmkd.config.ZmkdConfiguration;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationPlanDao;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationPlanFilterFactory;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlanStatus;
import pl.psnc.synat.wrdz.zmkd.plan.MigrationPlanProcessorsManager;

/**
 * Performs operations that need to be performed when the module is started (deployed).
 */
@Singleton
@Startup
public class ZmkdInitializer {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ZmkdInitializer.class);

    /** Migration plan DAO. */
    @EJB
    private MigrationPlanDao planDao;

    /** Migration plan processors manager. */
    @EJB
    private MigrationPlanProcessorsManager processorsManager;

    /** Module configuration. */
    @Inject
    private ZmkdConfiguration zmkdConfiguration;


    /**
     * Performs the module initialization.
     */
    @PostConstruct
    protected void init() {
        if (zmkdConfiguration.resumePlans()) {
            resumePlans();
        }
    }


    /**
     * Resumes all migration plans that are in the RUNNING state.
     */
    private void resumePlans() {
        MigrationPlanFilterFactory factory = planDao.createQueryModifier().getQueryFilterFactory();
        List<MigrationPlan> plans = planDao.findBy(factory.byStatus(MigrationPlanStatus.RUNNING), true);
        for (MigrationPlan plan : plans) {
            processorsManager.start(plan.getId(), true);
        }
    }
}
