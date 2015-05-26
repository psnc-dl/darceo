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
package pl.psnc.synat.wrdz.ru.dao.services;

import pl.psnc.synat.wrdz.common.dao.GenericQuerySorterBuilder;
import pl.psnc.synat.wrdz.ru.entity.services.DataManipulationService;

/**
 * Defines methods producing sorters for queries concerning {@link DataManipulationService} entities.
 */
public interface DataManipulationServiceSorterBuilder extends GenericQuerySorterBuilder<DataManipulationService> {

    /**
     * Sorts services by their location URLs.
     * 
     * @param ascendingly
     *            whether the entries should be sorted in ascending (if <code>true</code>) order or not (if
     *            <code>false</code>).
     * @return updated instance of {@link DataManipulationServiceSorterBuilder}.
     */
    DataManipulationServiceSorterBuilder byLocationUrl(boolean ascendingly);

}
