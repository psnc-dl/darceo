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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import pl.psnc.synat.wrdz.ms.entity.stats.BasicStats;

/**
 * Provides basic statistics gathered for the specific user.
 */
@ManagedBean
@ViewScoped
public class UserBasicStatisticsBean extends BasicStatisticsBean {

    /** Serial version UID. */
    private static final long serialVersionUID = -7910360852795446688L;

    /** Statistics manager. */
    @EJB
    private transient StatisticsManager statisticsManager;

    /** Current user. */
    private String username;


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    @Override
    protected Map<String, Number> prepareStatistics() {
        if (username != null) {
            return super.prepareStatistics();
        } else {
            return new LinkedHashMap<String, Number>();
        }
    }


    @Override
    protected BasicStats fetchStatistics() {
        return statisticsManager.getBasicStats(username);
    }
}
