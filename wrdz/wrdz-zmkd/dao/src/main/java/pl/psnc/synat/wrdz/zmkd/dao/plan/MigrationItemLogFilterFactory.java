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
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationItemLog;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationItemStatus;

/**
 * Specified set of filters for queries concerning {@link MigrationItemLog} entities.
 * 
 */
public interface MigrationItemLogFilterFactory extends GenericQueryFilterFactory<MigrationItemLog> {

    /**
     * Filters the entities by the status.
     * 
     * @param status
     *            status of a digital object migration
     * @return current representations of filters set
     */
    QueryFilter<MigrationItemLog> byStatus(MigrationItemStatus status);


    /**
     * Filters the entities by the digital object identifier.
     * 
     * @param objectIdentifier
     *            digital object identifier
     * @return current representations of filters set
     */
    QueryFilter<MigrationItemLog> byObjectIdentifier(String objectIdentifier);


    /**
     * Filters the entities by the migration plan id.
     * 
     * @param planId
     *            migration plan id
     * @return current representations of filters set
     */
    QueryFilter<MigrationItemLog> byMigrationPlan(Long planId);


    /**
     * Filters the entities by the creation request id.
     * 
     * @param requestId
     *            ZMD object creation request id
     * @return current representation of filters set
     */
    QueryFilter<MigrationItemLog> byRequestId(String requestId);
}
