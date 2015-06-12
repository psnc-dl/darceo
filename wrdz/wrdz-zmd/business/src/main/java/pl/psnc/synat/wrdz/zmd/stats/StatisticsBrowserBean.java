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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileFormatDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileExtractedMetadataDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.FileProvidedMetadataDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.MetadataNamespaceDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectExtractedMetadataDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.ObjectProvidedMetadataDao;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileFormat;

/**
 * Default implementation of {@link StatisticsBrowser}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class StatisticsBrowserBean implements StatisticsBrowser {

    /** Digital object DAO. */
    @EJB
    private DigitalObjectDao digitalObjectDao;

    /** Data file DAO. */
    @EJB
    private DataFileDao dataFileDao;

    /** Data file format DAO. */
    @EJB
    private DataFileFormatDao dataFileFormatDao;

    /** File extracted metadata DAO. */
    @EJB
    private FileExtractedMetadataDao fileExtractedMetadataDao;

    /** Object extracted metadata DAO. */
    @EJB
    private ObjectExtractedMetadataDao objectExtractedMetadataDao;

    /** File provided metadata DAO. */
    @EJB
    private FileProvidedMetadataDao fileProvidedMetadataDao;

    /** Object provided metadata DAO. */
    @EJB
    private ObjectProvidedMetadataDao objectProvidedMetadataDao;

    /** Namespace DAO. */
    @EJB
    private MetadataNamespaceDao namespaceDao;


    @Override
    public Map<Long, Long> getUserObjectCounts() {
        return digitalObjectDao.countAllGroupByOwner();
    }


    @Override
    public Map<Long, Long> getUserDataFileCounts() {
        return dataFileDao.countAllGroupByOwner();
    }


    @Override
    public Map<Long, Long> getUserDataFileSizes() {
        return dataFileDao.getSizeGroupByOwner();
    }


    @Override
    public Map<Long, Long> getUserExtractedMetadataFileCounts() {
        Map<Long, Long> files = fileExtractedMetadataDao.countAllGroupByOwner();
        Map<Long, Long> objects = objectExtractedMetadataDao.countAllGroupByOwner();
        return combine(files, objects);
    }


    @Override
    public Map<Long, Long> getUserExtractedMetadataFileSizes() {
        Map<Long, Long> files = fileExtractedMetadataDao.getSizeGroupByOwner();
        Map<Long, Long> objects = objectExtractedMetadataDao.getSizeGroupByOwner();
        return combine(files, objects);
    }


    @Override
    public Map<Long, Long> getUserProvidedMetadataFileCounts() {
        Map<Long, Long> files = fileProvidedMetadataDao.countAllGroupByOwner();
        Map<Long, Long> objects = objectProvidedMetadataDao.countAllGroupByOwner();
        return combine(files, objects);
    }


    @Override
    public Map<Long, Long> getUserProvidedMetadataFileSizes() {
        Map<Long, Long> files = fileProvidedMetadataDao.getSizeGroupByOwner();
        Map<Long, Long> objects = objectProvidedMetadataDao.getSizeGroupByOwner();
        return combine(files, objects);
    }


    @Override
    public Map<String, Map<Long, Long>> getDataFileFormatUserObjectCounts() {
        return transformDataFileFormatMap(dataFileFormatDao.countObjectsGroupByOwner());
    }


    @Override
    public Map<String, Map<Long, Long>> getDataFileFormatUserDataFileCounts() {
        return transformDataFileFormatMap(dataFileFormatDao.countDataFilesGroupByOwner());
    }


    @Override
    public Map<String, Map<Long, Long>> getDataFileFormatUserDataFileSizes() {
        return transformDataFileFormatMap(dataFileFormatDao.getDataFileSizesGroupByOwner());
    }


    @Override
    public Map<String, Long> getMetadataFormatObjectCounts() {
        return transform(namespaceDao.getObjectCounts());
    }


    @Override
    public Map<String, Long> getMetadataFormatDataFileCounts() {
        return transform(namespaceDao.getDataFileCounts());
    }


    /**
     * Combines the maps by adding values for the same keys.
     * 
     * @param map1
     *            first map
     * @param map2
     *            second map
     * 
     * @return combined map
     */
    private Map<Long, Long> combine(Map<Long, Long> map1, Map<Long, Long> map2) {
        Set<Long> keys = new HashSet<Long>();
        keys.addAll(map1.keySet());
        keys.addAll(map2.keySet());

        Map<Long, Long> result = new HashMap<Long, Long>();
        for (Long key : keys) {
            long value = 0;
            if (map1.containsKey(key)) {
                value += map1.get(key);
            }
            if (map2.containsKey(key)) {
                value += map2.get(key);
            }
            result.put(key, value);
        }

        return result;
    }


    /**
     * Transforms the given map, changing its keys to their toString() representation.
     * 
     * If the transformation results in key collisions, the latter value is kept.
     * 
     * @param <T>
     *            value type
     * @param map
     *            source map
     * @return transformed map
     */
    private <T> Map<String, T> transform(Map<?, T> map) {
        Map<String, T> results = new TreeMap<String, T>();
        for (Entry<?, T> entry : map.entrySet()) {
            results.put(entry.getKey().toString(), entry.getValue());
        }
        return results;
    }


    /**
     * Transforms the given map, replacing DataFileFormat keys with their puids.
     * 
     * Formats without puids are discarded. If the transformation results in key collisions, the latter value is kept.
     * 
     * @param <T>
     *            value type
     * @param map
     *            source map
     * @return transformed map
     */
    private <T> Map<String, T> transformDataFileFormatMap(Map<DataFileFormat, T> map) {
        Map<String, T> results = new TreeMap<String, T>();
        for (Entry<DataFileFormat, T> entry : map.entrySet()) {
            String puid = entry.getKey().getPuid();
            if (puid != null) {
                results.put(entry.getKey().getPuid(), entry.getValue());
            }
        }
        return results;
    }
}
