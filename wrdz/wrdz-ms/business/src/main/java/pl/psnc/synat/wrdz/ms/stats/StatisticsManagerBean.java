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

import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import pl.psnc.synat.wrdz.ms.dao.stats.BasicStatsDao;
import pl.psnc.synat.wrdz.ms.dao.stats.DataFileFormatStatDao;
import pl.psnc.synat.wrdz.ms.dao.stats.MetadataFormatStatDao;
import pl.psnc.synat.wrdz.ms.entity.stats.BasicStats;
import pl.psnc.synat.wrdz.ms.entity.stats.DataFileFormatStat;
import pl.psnc.synat.wrdz.ms.entity.stats.MetadataFormatStat;

/**
 * Default implementation of the {@link StatisticsManager}.
 */
@Stateless
public class StatisticsManagerBean implements StatisticsManager {

    /** Basic stats DAO. */
    @EJB
    private BasicStatsDao basicDao;

    /** Data file format stats DAO. */
    @EJB
    private DataFileFormatStatDao dataFormatDao;

    /** Metadata format stats DAO. */
    @EJB
    private MetadataFormatStatDao metadataFormatDao;


    @Override
    public BasicStats getBasicStats() {
        return basicDao.find(null);
    }


    @Override
    public BasicStats getBasicStats(String username) {
        if (username == null) {
            return null;
        }
        return basicDao.find(username);
    }


    @Override
    public List<DataFileFormatStat> getDataFileFormatStats() {
        return dataFormatDao.findAll(null);
    }


    @Override
    public List<DataFileFormatStat> getDataFileFormatStats(String username) {
        if (username != null) {
            return dataFormatDao.findAll(username);
        }
        return Collections.emptyList();
    }


    @Override
    public List<MetadataFormatStat> getMetadataFormatStats() {
        return metadataFormatDao.findAll();
    }
}
