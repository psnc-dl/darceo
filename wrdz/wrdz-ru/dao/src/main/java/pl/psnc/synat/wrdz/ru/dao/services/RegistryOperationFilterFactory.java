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

import java.util.Date;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.ru.entity.services.RegistryOperation;

/**
 * Specified set of filters for queries concerning {@link RegistryOperation} entities.
 */
public interface RegistryOperationFilterFactory extends GenericQueryFilterFactory<RegistryOperation> {

    /**
     * Filters the operations by the location of the semantic descriptors they describe.
     * 
     * @param locationUrl
     *            location URL of the semantic descriptor described by operation.
     * @return produced filter.
     */
    QueryFilter<RegistryOperation> byLocationUrl(String locationUrl);


    /**
     * Filters the operations by the date of the operation later or equal to the passed argument.
     * 
     * @param from
     *            date marking the beginning of period by which results are filtered.
     * @return produced filter.
     */
    QueryFilter<RegistryOperation> byDateFrom(Date from);


    /**
     * Filters the operations by the identifier of the semantic descriptors they describe.
     * 
     * @param semanticDescriptorId
     *            identifier of the semantic descriptor described by operation.
     * @return produced filter.
     */
    QueryFilter<RegistryOperation> bySemanticDescriptorId(long semanticDescriptorId);

}
