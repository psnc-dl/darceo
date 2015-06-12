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
package pl.psnc.synat.wrdz.zmd.stats;

import java.util.Map;

import javax.ejb.Remote;

/**
 * Provides access to various statistics.
 */
@Remote
public interface StatisticsBrowser {

    /**
     * Returns the total number of objects grouped by their owner id.
     * <p>
     * The result map contains only non-zero values. Users with with no objects are omitted.
     * 
     * @return a <user id, count> map representing object counts per user
     */
    Map<Long, Long> getUserObjectCounts();


    /**
     * Returns the total number of data files grouped by their owner id.
     * <p>
     * The result map contains only non-zero values. Users with with no data files are omitted.
     * 
     * @return a <user id, count> map representing data file counts per user
     */
    Map<Long, Long> getUserDataFileCounts();


    /**
     * Returns the total size of data files grouped by their owner id.
     * <p>
     * The result map contains only non-zero values. Users with with no data files are omitted.
     * 
     * @return a <user id, count> map representing total data file sizes per user
     */
    Map<Long, Long> getUserDataFileSizes();


    /**
     * Returns the total number of extracted metadata files grouped by their owner id.
     * <p>
     * The result map contains only non-zero values. Users with with no extracted metadata files are omitted.
     * 
     * @return a <user id, count> map representing total extracted metadata file counts per user
     */
    Map<Long, Long> getUserExtractedMetadataFileCounts();


    /**
     * Returns the total size of extracted metadata files grouped by their owner id.
     * <p>
     * The result map contains only non-zero values. Users with with no extracted metadata files are omitted.
     * 
     * @return a <user id, count> map representing total extracted metadata file sizes per user
     */
    Map<Long, Long> getUserExtractedMetadataFileSizes();


    /**
     * Returns the total number of provided metadata files grouped by their owner id.
     * <p>
     * The result map contains only non-zero values. Users with with no provided metadata files are omitted.
     * 
     * @return a <user id, count> map representing total provided metadata file counts per user
     */
    Map<Long, Long> getUserProvidedMetadataFileCounts();


    /**
     * Returns the total size of provided metadata files grouped by their owner id.
     * <p>
     * The result map contains only non-zero values. Users with with no provided metadata files are omitted.
     * 
     * @return a <user id, count> map representing total provided metadata file sizes per user
     */
    Map<Long, Long> getUserProvidedMetadataFileSizes();


    /**
     * Returns a map containing data file formats and the number of objects containing files in those formats, grouped
     * by their owner id.
     * <p>
     * The result maps contain only non-zero values. Formats with no data files are omitted from the outer map, and
     * users with no data files in a given format are omitted from the inner maps.
     * 
     * @return a <format's puid, <user id, number of user objects containing files in that format>> map
     */
    Map<String, Map<Long, Long>> getDataFileFormatUserObjectCounts();


    /**
     * Returns a map containing data file formats and the number of data files in those formats, grouped by their owner
     * id.
     * <p>
     * The result maps contain only non-zero values. Formats with no data files are omitted from the outer map, and
     * users with no data files in a given format are omitted from the inner maps.
     * 
     * @return a <format's puid, <user id, number of user data files in that format>> map
     */
    Map<String, Map<Long, Long>> getDataFileFormatUserDataFileCounts();


    /**
     * Returns a map containing data file formats and the total size of data files in those formats, grouped by their
     * owner id.
     * <p>
     * The result maps contain only non-zero values. Formats with no data files are omitted from the outer map, and
     * users with no data files in a given format are omitted from the inner maps.
     * 
     * @return a <format's puid, <user id, total size of user data files in that format>> map
     */
    Map<String, Map<Long, Long>> getDataFileFormatUserDataFileSizes();


    /**
     * Returns a map containing metadata formats and the number of objects described by them.
     * 
     * Only the formats that have a non-zero object count are included in the map.
     * 
     * @return a <format type, number of objects described by it> map
     */
    Map<String, Long> getMetadataFormatObjectCounts();


    /**
     * Returns a map containing metadata formats and the number of data files described by them.
     * 
     * Only the formats that have a non-zero data file count are included in the map.
     * 
     * @return a <format type, number of data files described by it> map
     */
    Map<String, Long> getMetadataFormatDataFileCounts();
}
