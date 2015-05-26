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
package pl.psnc.synat.wrdz.zmd.dao.object.content;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDao;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileFormat;

/**
 * An interface for a class managing the persistence of {@link DataFileFormat} class. It declares additional operations
 * available for {@link DataFileFormat} object apart from basic contract defined in {@link ExtendedGenericDao}.
 */
@Local
public interface DataFileFormatDao extends
        ExtendedGenericDao<DataFileFormatFilterFactory, DataFileFormatSorterBuilder, DataFileFormat, Long> {

    /**
     * Returns a map containing data file formats and the number of objects containing files in those formats, grouped
     * by their owner.
     * 
     * Only the formats that have a non-zero object count are included in the map.
     * 
     * @return a <format, number of objects containing files in that format> map
     */
    Map<DataFileFormat, Map<Long, Long>> countObjectsGroupByOwner();


    /**
     * Returns a map containing data file formats and the number of data files in those formats, grouped by their owner.
     * 
     * Only the formats that have a non-zero data file count are included in the map.
     * 
     * @return a <format, number of data files in that format> map
     */
    Map<DataFileFormat, Map<Long, Long>> countDataFilesGroupByOwner();


    /**
     * Returns a map containing data file formats and the total size of data files in those formats, grouped by their
     * owner.
     * 
     * Only the formats that have a non-zero data file count are included in the map.
     * 
     * @return a <format, size of data files in that format> map
     */
    Map<DataFileFormat, Map<Long, Long>> getDataFileSizesGroupByOwner();


    /**
     * Finds active formats for the given types of digital objects.
     * 
     * A format is considered active if it is used by a file belonging to the current version of a digital object that
     * has not been migrated from. The type of migration checked depends on the type of the object:
     * <ul>
     * <li>MasterObject - transformation
     * <li>OptimizedObject - optimization
     * <li>ConvertedObject - conversion
     * </ul>
     * 
     * @param checkMasterObjects
     *            whether to check MasterObjects for active formats
     * @param checkOptimizedObjects
     *            whether to check OptimizedObjects for active formats
     * @param checkConvertedObjects
     *            whether to check ConvertedObjects for active formats
     * @return a set of active formats
     */
    Set<DataFileFormat> findActive(boolean checkMasterObjects, boolean checkOptimizedObjects,
            boolean checkConvertedObjects);


    /**
     * Finds all formats with not-null puid. This method also eagerly fetches format names.
     * 
     * @return formats with not-null puid.
     */
    List<DataFileFormat> findAllWithPuidFetchNames();


    /**
     * Finds all formats used by the data files in the current version of the object with the given identifier.
     * 
     * @param objectIdentifier
     *            object identifier
     * @return formats
     */
    List<DataFileFormat> findByObjectId(String objectIdentifier);
}
