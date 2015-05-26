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
package pl.psnc.synat.wrdz.zmd.download;

import java.io.File;
import java.util.GregorianCalendar;

import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timer;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;

import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;

/**
 * Bean ensuring periodic cleaning of stale objects in download cache.
 */
@Singleton
@Startup
public class CacheCleanerBean {

    /**
     * Represents the time unit that is used for cache cleaning expressed in number of milliseconds. Multiplied by the
     * number included in module configuration it gives cache objects age after which those objects became stale and
     * should be removed. For instance value <code>86400000L</code> means that this period is counted in days.
     */
    private static final long TIME_UNIT_MULTIPLIER = 3600000L;

    /**
     * ZMD module's configuration POJO.
     */
    @Inject
    ZmdConfiguration zmdConfiguration;


    /**
     * Runs the scheduled cache cleaning every 12 hours starting from 1AM.
     * 
     * @param timer
     *            schedule's timer.
     */
    @Schedule(hour = "*", minute = "0", second = "0", dayOfWeek = "*", persistent = true, info = "Every full hour")
    public void run(Timer timer) {
        Integer invalidationPeriod = zmdConfiguration.getCacheStaleAfter();
        String cacheHome = zmdConfiguration.getCacheHome();
        File home = new File(cacheHome);
        File[] cachedSessions = home.listFiles();
        Long invalidatedFrom = (new GregorianCalendar().getTimeInMillis()) - invalidationPeriod * TIME_UNIT_MULTIPLIER;
        for (File file : cachedSessions) {
            if (file.lastModified() <= invalidatedFrom) {
                FileUtils.deleteQuietly(file);
            }
        }
    }
}
