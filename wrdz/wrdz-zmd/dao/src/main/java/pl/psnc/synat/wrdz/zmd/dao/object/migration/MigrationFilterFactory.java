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
package pl.psnc.synat.wrdz.zmd.dao.object.migration;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;
import pl.psnc.synat.wrdz.zmd.entity.types.MigrationType;

/**
 * Specified set of filters for queries concerning {@link Migration} entities.
 */
@SuppressWarnings("rawtypes")
public interface MigrationFilterFactory extends GenericQueryFilterFactory<Migration> {

    /**
     * Filters the entities by result identifier of the digital object. Can use only exact string match.
     * 
     * @param identifier
     *            identifier of the object
     * @return current representations of filters set
     */
    QueryFilter<Migration> byResultIdentifier(String identifier);


    /**
     * Filters the entities by source identifier of the digital object. Can use only exact string match.
     * 
     * @param identifier
     *            identifier of the object
     * @return current representations of filters set
     */
    QueryFilter<Migration> bySourceIdentifier(String identifier);


    /**
     * Filters the entities by the resulting object identifier.
     * 
     * @param resultId
     *            result object id.
     * @return current representations of filters set
     */
    QueryFilter<Migration> byResult(long resultId);


    /**
     * Filters the entities by the source object identifier.
     * 
     * @param sourceId
     *            source object id.
     * @return current representations of filters set
     */
    QueryFilter<Migration> bySource(long sourceId);


    /**
     * Filters the entities by the migration type.
     * 
     * @param type
     *            migration type.
     * @return current representations of filters set
     */
    QueryFilter<Migration> byType(MigrationType type);

}
