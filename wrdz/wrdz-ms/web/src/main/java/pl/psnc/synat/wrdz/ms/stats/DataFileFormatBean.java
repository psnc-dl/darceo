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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import pl.psnc.synat.wrdz.ms.entity.stats.DataFileFormatStat;
import pl.psnc.synat.wrdz.zmd.dto.format.DataFileFormatDto;
import pl.psnc.synat.wrdz.zmd.format.DataFileFormatBrowser;

/**
 * Bean that handles data file format statistics.
 */
@SuppressWarnings("serial")
public abstract class DataFileFormatBean implements Serializable {

    /** Data file format statistics. */
    private List<DataFileFormatStat> stats;

    /** Data file format information. */
    private Map<String, DataFileFormatDto> formats;

    /** Data file format browser. */
    @EJB(name = "DataFileFormatBrowser")
    private transient DataFileFormatBrowser formatBrowser;

    /** When the statistics were computed. */
    private Date computedOn;


    /**
     * Refreshes the currently cached statistics.
     */
    public void refresh() {

        stats = fetchStatistics();

        computedOn = !stats.isEmpty() ? stats.get(0).getComputedOn() : null;

        formats = new HashMap<String, DataFileFormatDto>();
        for (DataFileFormatDto format : formatBrowser.getFormats()) {
            formats.put(format.getPuid(), format);
        }
    }


    /**
     * Returns the currently cached statistics, refreshing them if necessary.
     * 
     * @return statistics
     */
    public List<DataFileFormatStat> getStatistics() {
        if (stats == null) {
            refresh();
        }
        return stats;
    }


    public Date getComputedOn() {
        return computedOn;
    }


    /**
     * Returns the map with format puids and their data.
     * 
     * @return a <format puid, format data> map
     */
    public Map<String, DataFileFormatDto> getFormats() {
        if (formats == null) {
            refresh();
        }
        return formats;
    }


    /**
     * Fetches the statistics from database.
     * 
     * @return the statistics
     */
    protected abstract List<DataFileFormatStat> fetchStatistics();
}
