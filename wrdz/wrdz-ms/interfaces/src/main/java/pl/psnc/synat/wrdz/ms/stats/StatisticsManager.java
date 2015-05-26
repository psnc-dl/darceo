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

import java.util.List;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.ms.entity.stats.BasicStats;
import pl.psnc.synat.wrdz.ms.entity.stats.DataFileFormatStat;
import pl.psnc.synat.wrdz.ms.entity.stats.MetadataFormatStat;

/**
 * Manages the statistics entities.
 */
@Local
public interface StatisticsManager {

    /**
     * Returns the general basic statistics, or <code>null</code> if they have not yet been calculated.
     * 
     * @return the general basic statistics
     */
    BasicStats getBasicStats();


    /**
     * Returns the basic statistics for the given user, or <code>null</code> if they have not yet been calculated.
     * 
     * @param username
     *            username
     * @return the basic statistics for the given user
     */
    BasicStats getBasicStats(String username);


    /**
     * Returns the general data file format statistics.
     * 
     * @return the general data file format statistics
     */
    List<DataFileFormatStat> getDataFileFormatStats();


    /**
     * Returns the data file format statistics for the given user.
     * 
     * @param username
     *            username
     * @return the data file format statistics for the given user
     */
    List<DataFileFormatStat> getDataFileFormatStats(String username);


    /**
     * Returns the metadata format statistics.
     * 
     * @return the metadata format statistics
     */
    List<MetadataFormatStat> getMetadataFormatStats();
}
