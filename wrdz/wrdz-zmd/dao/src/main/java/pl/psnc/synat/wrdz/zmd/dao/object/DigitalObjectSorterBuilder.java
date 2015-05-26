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
package pl.psnc.synat.wrdz.zmd.dao.object;

import pl.psnc.synat.wrdz.common.dao.GenericQuerySorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;

/**
 * Defines methods producing sorters for queries concerning {@link DigitalObject} entities.
 */
public interface DigitalObjectSorterBuilder extends GenericQuerySorterBuilder<DigitalObject> {

    /**
     * Adds sorting by name.
     * 
     * @param asc whether to sort in an ascending (<code>true</code>) or descending (<code>false</code>) manner
     * @return this builder
     */
    DigitalObjectSorterBuilder byName(boolean asc);


    /**
     * Adds sorting by public identifier.
     * 
     * @param asc whether to sort in an ascending (<code>true</code>) or descending (<code>false</code>) manner
     * @return this builder
     */
    DigitalObjectSorterBuilder byIdentifier(boolean asc);


    /**
     * Adds sorting by database id.
     * 
     * @param asc whether to sort in an ascending (<code>true</code>) or descending (<code>false</code>) manner
     * @return this builder
     */
    DigitalObjectSorterBuilder byId(boolean asc);
}
