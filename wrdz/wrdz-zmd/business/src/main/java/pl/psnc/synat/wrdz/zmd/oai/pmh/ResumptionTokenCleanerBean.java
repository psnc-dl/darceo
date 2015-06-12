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
package pl.psnc.synat.wrdz.zmd.oai.pmh;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.zmd.dao.oai.ResumptionTokenDao;

/**
 * Provides the functionality for cleaning outdated resumption tokens.
 */
@Singleton
public class ResumptionTokenCleanerBean {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ResumptionTokenCleanerBean.class);

    /**
     * Resumption token DAO.
     */
    @EJB
    private ResumptionTokenDao resumptionTokenDao;


    /**
     * Runs resumption tokens cleaning periodically.
     * 
     * @param timer
     *            timer object.
     */
    @Schedule(hour = "1", minute = "0", second = "0", dayOfWeek = "*", persistent = true, info = "Every day at 1am")
    public void run(Timer timer) {
        int count = resumptionTokenDao.deleteStaleTokens(resumptionTokenDao.getDatabaseDate());
        logger.debug("Stale token cleaner found and deleted " + count + " outdated tokens.");
    }
}
