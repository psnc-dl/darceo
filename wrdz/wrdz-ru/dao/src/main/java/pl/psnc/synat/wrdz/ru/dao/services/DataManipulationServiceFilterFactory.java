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

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.ru.entity.services.DataManipulationService;
import pl.psnc.synat.wrdz.ru.entity.types.ServiceType;

/**
 * Specified set of filters for queries concerning {@link DataManipulationService} entities.
 */
public interface DataManipulationServiceFilterFactory extends GenericQueryFilterFactory<DataManipulationService> {

    /**
     * Filters services by their types.
     * 
     * @param type
     *            type of service.
     * @return produced filter.
     */
    QueryFilter<DataManipulationService> byType(ServiceType type);


    /**
     * Filters the services by their IRIs.
     * 
     * @param iri
     *            IRI of data manipulation service.
     * @return produced filter.
     */
    QueryFilter<DataManipulationService> byIri(String iri);


    /**
     * Filters the services by their address.
     * 
     * @param locationUrl
     *            address of data manipulation service.
     * @return produced filter.
     */
    QueryFilter<DataManipulationService> byLocationUrl(String locationUrl);


    /**
     * Filters services by their name.
     * 
     * @param name
     *            the service name
     * @return produced filter
     */
    QueryFilter<DataManipulationService> byName(String name);


    /**
     * Filters services by their semantic descriptor's context.
     * 
     * @param context
     *            semantic descriptor context
     * @return produced filter
     */
    QueryFilter<DataManipulationService> byContext(String context);
}
