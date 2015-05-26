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
package pl.psnc.synat.wrdz.zmkd.dao.plan;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlanStatus;

/**
 * Specified set of filters for queries concerning {@link MigrationPlan} entities.
 * 
 */
public interface MigrationPlanFilterFactory extends GenericQueryFilterFactory<MigrationPlan> {

    /**
     * Filters the entities by the owner id.
     * 
     * @param ownerId
     *            id of the owner of a migration plan
     * @return current representations of filters set
     */
    QueryFilter<MigrationPlan> byOwnerId(Long ownerId);


    /**
     * Filters the entities by the status.
     * 
     * @param status
     *            status of a migration plan
     * @return current representations of filters set
     */
    QueryFilter<MigrationPlan> byStatus(MigrationPlanStatus status);


    /**
     * Filters the entities that are waiting for a digital object.
     * 
     * @param objectIdentifier
     *            digital object identifier
     * @return current representations of filters set
     */
    QueryFilter<MigrationPlan> byObjectIdentifierAwaited(String objectIdentifier);

}
