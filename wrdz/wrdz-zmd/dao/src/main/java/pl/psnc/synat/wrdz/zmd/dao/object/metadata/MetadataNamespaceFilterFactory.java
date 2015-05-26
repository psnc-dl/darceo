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
package pl.psnc.synat.wrdz.zmd.dao.object.metadata;

import java.util.List;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataNamespace;

/**
 * Specified set of filters for queries concerning {@link MetadataNamespace} entities.
 */
public interface MetadataNamespaceFilterFactory extends GenericQueryFilterFactory<MetadataNamespace> {

    /**
     * Filters the entities by namespace URI.
     * 
     * @param xmlns
     *            xmlns
     * @return current representations of filters set
     */
    QueryFilter<MetadataNamespace> byXmlns(String xmlns);


    /**
     * Filters the entities by schema location.
     * 
     * @param schemaLocation
     *            schema location
     * @return current representations of filters set
     */
    QueryFilter<MetadataNamespace> bySchemaLocation(String schemaLocation);


    /**
     * Filters the entities by type of metadata namespace.
     * 
     * @param type
     *            type of metadata namespace
     * @return current representations of filters set
     */
    QueryFilter<MetadataNamespace> byType(NamespaceType type);


    /**
     * Filters the entities by types of metadata namespace.
     * 
     * @param types
     *            types of metadata namespace
     * @return current representations of filters set
     */
    QueryFilter<MetadataNamespace> byType(List<NamespaceType> types);

}
