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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationPlanDao;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPath;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan;

/**
 * Default implementation of {@link MigrationPlanRetriever}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MigrationPathRetrieverBean implements MigrationPathRetriever {

    /** Migration plan DAO. */
    @EJB
    private MigrationPlanDao planDao;


    @Override
    public MigrationPath retrieveActivePath(long planId) {
        MigrationPlan plan = planDao.findById(planId);
        if (plan == null) {
            throw new IllegalArgumentException("Specified migration plan does not exist");
        }
        MigrationPath path = plan.getActivePath();
        if (path == null) {
            throw new IllegalArgumentException("Specified migration plan does not have an active path");
        }
        return path;
    }

}
