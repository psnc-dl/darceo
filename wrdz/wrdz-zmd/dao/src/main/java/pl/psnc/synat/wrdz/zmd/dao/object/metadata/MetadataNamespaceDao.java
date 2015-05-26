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

import java.util.Map;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDao;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataNamespace;

/**
 * An interface for a class managing the persistence of {@link MetadataNamespace} class. It declares additional
 * operations available for {@link MetadataNamespace} object apart from basic contract defined in
 * {@link ExtendedGenericDao}.
 */
@Local
public interface MetadataNamespaceDao extends
        ExtendedGenericDao<MetadataNamespaceFilterFactory, MetadataNamespaceSorterBuilder, MetadataNamespace, Long> {

    /**
     * Returns a map containing metadata formats and the number of object metadata files using them.
     * 
     * Only the formats that have a non-zero metadata file count are included in the map.
     * 
     * @return a <format type, number of object metadata files using it> map
     */
    Map<NamespaceType, Long> getObjectCounts();


    /**
     * Returns a map containing metadata formats and the number of data file metadata files using them.
     * 
     * Only the formats that have a non-zero metadata file count are included in the map.
     * 
     * @return a <format type, number of data file metadata files using it> map
     */
    Map<NamespaceType, Long> getDataFileCounts();
}
