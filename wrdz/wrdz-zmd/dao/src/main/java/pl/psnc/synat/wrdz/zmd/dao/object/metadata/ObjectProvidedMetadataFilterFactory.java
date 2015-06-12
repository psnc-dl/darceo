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

import java.util.Set;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata;

/**
 * Specified set of filters for queries concerning {@link ObjectProvidedMetadata} entities.
 */
public interface ObjectProvidedMetadataFilterFactory extends GenericQueryFilterFactory<ObjectProvidedMetadata> {

    /**
     * Filters the metadata entities by versions they are included in.
     * 
     * @param versionIds
     *            IDs of versions in which metadata has been included.
     * @return current representation of filters set.
     * @throws IllegalArgumentException
     *             if no version ID is given.
     */
    QueryFilter<ObjectProvidedMetadata> byIncludedInVersions(Set<Long> versionIds)
            throws IllegalArgumentException;


    /**
     * Filters the metadata entities by version they are included in.
     * 
     * @param versionId
     *            ID of versions in which metadata has been included.
     * @return current representation of filters set.
     */
    QueryFilter<ObjectProvidedMetadata> byIncludedInVersion(long versionId);


    /**
     * Filters the metadata entities by version they are included in.
     * 
     * @param versionNo
     *            number of versions in which metadata has been included.
     * @return current representation of filters set.
     */
    QueryFilter<ObjectProvidedMetadata> byIncludedInVersionNo(int versionNo);


    /**
     * Filters the metadata entities by metadata file name.
     * 
     * @param name
     *            metadata file name
     * @return current representation of filters set.
     */
    QueryFilter<ObjectProvidedMetadata> byMetadataFileName(String name);

}
