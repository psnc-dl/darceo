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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.psnc.synat.wrdz.ms.entity.stats.MetadataFormatStat;

/**
 * Bean that handles metadata format statistics.
 */
@SuppressWarnings("serial")
public abstract class MetadataFormatBean implements Serializable {

    /** Object counts. */
    private Map<String, Long> objectCounts;

    /** Data file counts. */
    private Map<String, Long> dataFileCounts;

    /** Metadata formats for objects. */
    private List<String> objectFormats;

    /** Metadata formats for data files. */
    private List<String> dataFileFormats;

    /** When the statistics were computed. */
    private Date computedOn;


    /**
     * Refreshes the currently cached statistics.
     */
    public void refresh() {

        List<MetadataFormatStat> stats = fetchStatistics();

        computedOn = !stats.isEmpty() ? stats.get(0).getComputedOn() : null;

        objectCounts = new HashMap<String, Long>();
        dataFileCounts = new HashMap<String, Long>();

        for (MetadataFormatStat stat : stats) {
            if (stat.getObjects() > 0) {
                objectCounts.put(stat.getFormatName(), stat.getObjects());
            }
            if (stat.getDataFiles() > 0) {
                dataFileCounts.put(stat.getFormatName(), stat.getDataFiles());
            }
        }

        objectFormats = new ArrayList<String>(objectCounts.keySet());
        Collections.sort(objectFormats);
        objectFormats = Collections.unmodifiableList(objectFormats);

        dataFileFormats = new ArrayList<String>(dataFileCounts.keySet());
        Collections.sort(dataFileFormats);
        dataFileFormats = Collections.unmodifiableList(dataFileFormats);
    }


    /**
     * Returns the available object counts.
     * 
     * @return available object counts
     */
    public Map<String, Long> getObjectCounts() {
        if (objectCounts == null) {
            refresh();
        }
        return objectCounts;
    }


    /**
     * Returns the available data file counts.
     * 
     * @return available data file counts
     */
    public Map<String, Long> getDataFileCounts() {
        if (dataFileCounts == null) {
            refresh();
        }
        return dataFileCounts;
    }


    /**
     * Returns the list of available object metadata formats.
     * 
     * @return list of available object metadata formats
     */
    public List<String> getObjectFormats() {
        if (objectFormats == null) {
            refresh();
        }
        return objectFormats;
    }


    /**
     * Returns the list of available data file metadata formats.
     * 
     * @return list of available data file metadata formats
     */
    public List<String> getDataFileFormats() {
        if (dataFileFormats == null) {
            refresh();
        }
        return dataFileFormats;
    }


    public Date getComputedOn() {
        return computedOn;
    }


    /**
     * Fetches the statistics from database.
     * 
     * @return the statistics
     */
    protected abstract List<MetadataFormatStat> fetchStatistics();
}
