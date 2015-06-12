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
package pl.psnc.synat.wrdz.common.async;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Timer;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.config.Configuration;
import pl.psnc.synat.wrdz.common.dao.async.AsyncRequestResultDao;
import pl.psnc.synat.wrdz.common.dao.async.AsyncRequestResultFilterFactory;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;

/**
 * Cleaner of responses for asynchronous request.
 */
@Singleton
public class AsyncRequestCleanerBean {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(AsyncRequestCleanerBean.class);

    /**
     * Configuration.
     */
    @Inject
    private Configuration config;

    /**
     * DAO for results of asynchronous requests.
     */
    @EJB
    private AsyncRequestResultDao asyncRequestResultDaoBean;


    /**
     * Cleans responses for asynchronous request. Both in the database and on the disk.
     * 
     * @param timer
     *            timer
     */
    @Schedule(hour = "0", minute = "0", second = "0", dayOfWeek = "*", persistent = true,
            info = "Every day at midnight")
    public void run(Timer timer) {
        int days = config.getAsyncCleaningPeriod();
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        Date date = now.getTime();
        date.setTime(date.getTime() - (days + 1) * 86400000L);
        logger.debug("cleaning all results before " + date + " (cleaning period: " + days + " days");
        AsyncRequestResultFilterFactory queryFilterFactory = asyncRequestResultDaoBean.createQueryModifier()
                .getQueryFilterFactory();
        List<AsyncRequestResult> results = asyncRequestResultDaoBean.findBy(
            queryFilterFactory.byCompletedBefore(date, false), false);
        logger.debug("results to remove: " + results.size());
        String root = config.getAsyncCacheHome();
        for (AsyncRequestResult result : results) {
            logger.debug("result: " + result);
            boolean success = true;
            if (result.hasContent()) {
                File file = new File(root + "/" + result.getId());
                if (file.exists()) {
                    success = file.delete();
                    if (success) {
                        logger.debug("File " + result.getId() + " successfully deleted");
                    } else {
                        logger.debug("Deleting the file " + result.getId() + " failed");
                    }
                } else {
                    logger.debug("File " + result.getId() + " does not exist!");
                }
            }
            if (success) {
                asyncRequestResultDaoBean.delete(result);
            }
        }
    }

}
