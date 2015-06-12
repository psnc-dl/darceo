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
package pl.psnc.synat.wrdz.ms.dao.stats;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.dao.GenericDao;
import pl.psnc.synat.wrdz.ms.entity.stats.BasicStats;

/**
 * An interface for a class managing the persistence of {@link BasicStats} class. It declares additional operations
 * available for {@link BasicStats} object apart from basic contract defined in {@link GenericDao}.
 */
@Local
public interface BasicStatsDao extends GenericDao<BasicStats, Long> {

    /**
     * Returns the basic statistics, either general or for the given user.
     * 
     * @param username
     *            username for a user-based stats, or <code>null</code> for general stats
     * @return found stats, or <code>null</code>
     */
    BasicStats find(String username);


    /**
     * Deletes all statistics.
     */
    void deleteAll();
}
