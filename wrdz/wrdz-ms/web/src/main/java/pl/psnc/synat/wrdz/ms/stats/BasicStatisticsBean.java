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
package pl.psnc.synat.wrdz.ms.stats;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import pl.psnc.synat.wrdz.ms.entity.stats.BasicStats;
import pl.psnc.synat.wrdz.ms.i18n.MsMessageUtils;

/**
 * Bean that handles basic statistics (object and file counts/sizes).
 */
@SuppressWarnings("serial")
public abstract class BasicStatisticsBean implements Serializable {

    /** Statistics. */
    private Map<String, Number> statistics;

    /** When the statistics were computed. */
    private Date computedOn;


    /**
     * Refreshes the currently cached statistics.
     */
    public void refresh() {
        statistics = prepareStatistics();
    }


    /**
     * Returns the available statistics.
     * 
     * @return available statistics
     */
    public Map<String, Number> getStatistics() {
        if (statistics == null) {
            refresh();
        }
        return statistics;
    }


    public Date getComputedOn() {
        return computedOn;
    }


    /**
     * Returns the list of available statistics.
     * 
     * @return list of available statistics
     */
    public List<String> getStatisticsList() {
        if (statistics == null) {
            refresh();
        }
        return new ArrayList<String>(statistics.keySet());
    }


    /**
     * Prepares statistics. Base statistics are fetched from ZMD, the rest are calculated based on that data.
     * 
     * @return up-to-date statistics
     */
    protected Map<String, Number> prepareStatistics() {

        BasicStats stats = fetchStatistics();

        if (stats == null) {
            computedOn = null;
            return Collections.emptyMap();
        }

        computedOn = stats.getComputedOn();

        long objectCount = stats.getObjects();

        long dataFileCount = stats.getDataFiles();
        long extractedMetadataFileCount = stats.getExtractedMetadataFiles();
        long providedMetadataFileCount = stats.getProvidedMetadataFiles();

        long dataFileSize = stats.getDataSize();
        long extractedMetadataFileSize = stats.getExtractedMetadataSize();
        long providedMetadataFileSize = stats.getProvidedMetadataSize();

        long totalFileCount = dataFileCount + extractedMetadataFileCount + providedMetadataFileCount;
        long totalFileSize = dataFileSize + extractedMetadataFileSize + providedMetadataFileSize;

        Map<String, Number> map = new LinkedHashMap<String, Number>();

        map.put(MsMessageUtils.getMessage("basic.stats.objects"), objectCount);
        map.put(MsMessageUtils.getMessage("basic.stats.files"), totalFileCount);
        map.put(MsMessageUtils.getMessage("basic.stats.files.data"), dataFileCount);
        map.put(MsMessageUtils.getMessage("basic.stats.files.extracted"), extractedMetadataFileCount);
        map.put(MsMessageUtils.getMessage("basic.stats.files.provided"), providedMetadataFileCount);
        map.put(MsMessageUtils.getMessage("basic.stats.size"), totalFileSize);
        map.put(MsMessageUtils.getMessage("basic.stats.size.data"), dataFileSize);
        map.put(MsMessageUtils.getMessage("basic.stats.size.extracted"), extractedMetadataFileSize);
        map.put(MsMessageUtils.getMessage("basic.stats.size.provided"), providedMetadataFileSize);

        if (objectCount > 0) {
            double averageFileCount = (double) totalFileCount / objectCount;
            double averageDataFileCount = (double) dataFileCount / objectCount;
            double averageExtractedMetadataFileCount = (double) extractedMetadataFileCount / objectCount;
            double averageProvidedMetadataFileCount = (double) providedMetadataFileCount / objectCount;

            long averageFileSize = Math.round((double) totalFileSize / objectCount);
            long averageDataFileSize = Math.round((double) dataFileSize / objectCount);
            long averageExtractedMetadataFileSize = Math.round((double) extractedMetadataFileSize / objectCount);
            long averageProvidedMetadataFileSize = Math.round((double) providedMetadataFileSize / objectCount);

            map.put(MsMessageUtils.getMessage("basic.stats.average.files"), averageFileCount);
            map.put(MsMessageUtils.getMessage("basic.stats.average.files.data"), averageDataFileCount);
            map.put(MsMessageUtils.getMessage("basic.stats.average.files.extracted"), averageExtractedMetadataFileCount);
            map.put(MsMessageUtils.getMessage("basic.stats.average.files.provided"), averageProvidedMetadataFileCount);
            map.put(MsMessageUtils.getMessage("basic.stats.average.size"), averageFileSize);
            map.put(MsMessageUtils.getMessage("basic.stats.average.size.data"), averageDataFileSize);
            map.put(MsMessageUtils.getMessage("basic.stats.average.size.extracted"), averageExtractedMetadataFileSize);
            map.put(MsMessageUtils.getMessage("basic.stats.average.size.provided"), averageProvidedMetadataFileSize);
        }

        return map;
    }


    /**
     * Fetches the statistics from database.
     * 
     * @return the statistics
     */
    protected abstract BasicStats fetchStatistics();
}
