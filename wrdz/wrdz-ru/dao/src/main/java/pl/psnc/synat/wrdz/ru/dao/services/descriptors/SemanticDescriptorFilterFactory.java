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
package pl.psnc.synat.wrdz.ru.dao.services.descriptors;

import java.util.Collection;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;

/**
 * Specified set of filters for queries concerning {@link SemanticDescriptor} entities.
 */
public interface SemanticDescriptorFilterFactory extends GenericQueryFilterFactory<SemanticDescriptor> {

    /**
     * Filters services' semantic descriptors by their schema names.
     * 
     * @param name
     *            name of the schema.
     * @return produced filter.
     */
    QueryFilter<SemanticDescriptor> bySchemaName(String name);


    /**
     * Filters services' semantic descriptors by the service identifier.
     * 
     * @param serviceId
     *            identifier of the service.
     * @return produced filter.
     */
    QueryFilter<SemanticDescriptor> byService(long serviceId);


    /**
     * Filters descriptors by their visibility.
     * 
     * @param exposed
     *            if <code>true</code> searches for public services, otherwise searches for private ones.
     * @return produced filter.
     */
    QueryFilter<SemanticDescriptor> byVisibility(boolean exposed);


    /**
     * Filters descriptors by their location.
     * 
     * @param local
     *            if <code>true</code> searches for locally defined services, otherwise for the remote (harvested) ones.
     * @return produced filter.
     */
    QueryFilter<SemanticDescriptor> byLocation(boolean local);


    /**
     * Filters the descriptors by their deletion marker.
     * 
     * @param deleted
     *            whether or not the semantic descriptor is marked as deleted.
     * @return produced filter.
     */
    QueryFilter<SemanticDescriptor> byDeleted(boolean deleted);


    /**
     * Filters the descriptors by their location.
     * 
     * @param locationUrl
     *            location of semantic descriptor.
     * @return produced filter.
     */
    QueryFilter<SemanticDescriptor> byLocationUrl(String locationUrl);


    /**
     * Filters the descriptors by primary keys.
     * 
     * @param ids
     *            collection of primary key values.
     * @return produced filter.
     */
    QueryFilter<SemanticDescriptor> byIdIn(Collection<Long> ids);

}
