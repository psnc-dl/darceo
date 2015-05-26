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
package pl.psnc.synat.wrdz.zmd.dao.object.metadata.operation;

import pl.psnc.synat.wrdz.common.dao.GenericQuerySorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;

/**
 * Defines methods producing sorters for queries concerning {@link Operation} entities.
 */
public interface OperationSorterBuilder extends GenericQuerySorterBuilder<Operation> {

    /**
     * Enables sorting by the date of the associated {@link Operation}.
     * 
     * @param ascendingly
     *            if <code>true</code> the sorting order will be ascending, else it will be descending.
     * @return modified instance of the builder with updated sorters list
     */
    OperationSorterBuilder byDate(boolean ascendingly);

}
