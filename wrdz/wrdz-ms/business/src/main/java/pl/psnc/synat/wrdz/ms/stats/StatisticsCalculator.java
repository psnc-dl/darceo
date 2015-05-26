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

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.ms.config.MsConfiguration;
import pl.psnc.synat.wrdz.ms.dao.stats.BasicStatsDao;
import pl.psnc.synat.wrdz.ms.dao.stats.DataFileFormatStatDao;
import pl.psnc.synat.wrdz.ms.dao.stats.MetadataFormatStatDao;
import pl.psnc.synat.wrdz.ms.entity.stats.BasicStats;
import pl.psnc.synat.wrdz.ms.entity.stats.DataFileFormatStat;
import pl.psnc.synat.wrdz.ms.entity.stats.MetadataFormatStat;
import pl.psnc.synat.wrdz.zmd.stats.StatisticsBrowser;
import pl.psnc.synat.wrdz.zu.dto.user.UserDto;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Responsible for calculating and storing statistics.
 * 
 * @see MsConfiguration
 */
@Singleton
@Startup
public class StatisticsCalculator {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(StatisticsCalculator.class);

    /** Module configuration. */
    @Inject
    private MsConfiguration configuration;

    /** Injected timer service. */
    @Resource
    private TimerService timerService;

    /** Injected session context. */
    @Resource
    private SessionContext ctx;

    /** Basic stats DAO. */
    @EJB
    private BasicStatsDao basicStatsDao;

    /** Data file format stats DAO. */
    @EJB
    private DataFileFormatStatDao dataFileFormatStatDao;

    /** Metadata format stats DAO. */
    @EJB
    private MetadataFormatStatDao metadataFormatStatDao;

    /** Statistics browser. */
    @EJB(name = "StatisticsBrowser")
    private StatisticsBrowser statisticsBrowser;

    /** User browser. */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;


    /**
     * Creates and registers the timer for the statistics calculation task.
     */
    @PostConstruct
    protected void init() {
        ScheduleExpression expression = configuration.getStatisticsCalculationSchedule();
        timerService.createCalendarTimer(expression);
    }


    /**
     * Calculates statistics and stores them in the database.
     * 
     * @param timer
     *            timer that triggered the event
     */
    @Timeout
    protected void onTimeout(Timer timer) {
        StatisticsCalculator proxy = ctx.getBusinessObject(StatisticsCalculator.class);

        logger.info("Statistics calculation started");
        long start = System.currentTimeMillis();

        proxy.calculateBasicStatistics();
        proxy.calculateDataFileFormatStatistics();
        proxy.calculateMetadataFormatStatistics();

        logger.info("Statistics calculation finished (took " + (System.currentTimeMillis() - start) + " ms)");
    }


    /**
     * Calculates basic statistics.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void calculateBasicStatistics() {

        Map<Long, String> userMap = new TreeMap<Long, String>();
        for (UserDto user : userBrowser.getUsers()) {
            userMap.put(user.getId(), user.getUsername());
        }

        Map<Long, Long> objectCounts = statisticsBrowser.getUserObjectCounts();
        Map<Long, Long> dataFileCounts = statisticsBrowser.getUserDataFileCounts();
        Map<Long, Long> dataFileSizes = statisticsBrowser.getUserDataFileSizes();
        Map<Long, Long> extractedMetadataCounts = statisticsBrowser.getUserExtractedMetadataFileCounts();
        Map<Long, Long> extractedMetadataSizes = statisticsBrowser.getUserExtractedMetadataFileSizes();
        Map<Long, Long> providedMetadataCounts = statisticsBrowser.getUserProvidedMetadataFileCounts();
        Map<Long, Long> providedMetadataSizes = statisticsBrowser.getUserProvidedMetadataFileSizes();

        Date computedOn = new Date();

        basicStatsDao.deleteAll();

        BasicStats global = new BasicStats();
        global.setComputedOn(computedOn);

        for (Entry<Long, String> user : userMap.entrySet()) {
            BasicStats stats = new BasicStats();
            stats.setUsername(user.getValue());
            stats.setComputedOn(computedOn);

            stats.setObjects(getValue(objectCounts, user.getKey()));
            stats.setDataFiles(getValue(dataFileCounts, user.getKey()));
            stats.setDataSize(getValue(dataFileSizes, user.getKey()));
            stats.setExtractedMetadataFiles(getValue(extractedMetadataCounts, user.getKey()));
            stats.setExtractedMetadataSize(getValue(extractedMetadataSizes, user.getKey()));
            stats.setProvidedMetadataFiles(getValue(providedMetadataCounts, user.getKey()));
            stats.setProvidedMetadataSize(getValue(providedMetadataSizes, user.getKey()));

            basicStatsDao.persist(stats);

            global.setObjects(global.getObjects() + stats.getObjects());
            global.setDataFiles(global.getDataFiles() + stats.getDataFiles());
            global.setDataSize(global.getDataSize() + stats.getDataSize());
            global.setExtractedMetadataFiles(global.getExtractedMetadataFiles() + stats.getExtractedMetadataFiles());
            global.setExtractedMetadataSize(global.getExtractedMetadataSize() + stats.getExtractedMetadataSize());
            global.setProvidedMetadataFiles(global.getProvidedMetadataFiles() + stats.getProvidedMetadataFiles());
            global.setProvidedMetadataSize(global.getProvidedMetadataSize() + stats.getProvidedMetadataSize());
        }

        basicStatsDao.persist(global);
    }


    /**
     * Calculates data file format statistics.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void calculateDataFileFormatStatistics() {

        Map<Long, String> userMap = new TreeMap<Long, String>();
        for (UserDto user : userBrowser.getUsers()) {
            userMap.put(user.getId(), user.getUsername());
        }

        Map<String, Map<Long, Long>> objectCounts = statisticsBrowser.getDataFileFormatUserObjectCounts();
        Map<String, Map<Long, Long>> dataFileCounts = statisticsBrowser.getDataFileFormatUserDataFileCounts();
        Map<String, Map<Long, Long>> dataFileSizes = statisticsBrowser.getDataFileFormatUserDataFileSizes();

        Date computedOn = new Date();

        dataFileFormatStatDao.deleteAll();

        Set<String> formats = new TreeSet<String>();
        formats.addAll(objectCounts.keySet());
        formats.addAll(dataFileCounts.keySet());
        formats.addAll(dataFileSizes.keySet());

        for (String format : formats) {
            Set<Long> users = new TreeSet<Long>();
            if (objectCounts.containsKey(format)) {
                users.addAll(objectCounts.get(format).keySet());
            }
            if (dataFileCounts.containsKey(format)) {
                users.addAll(dataFileCounts.get(format).keySet());
            }
            if (dataFileSizes.containsKey(format)) {
                users.addAll(dataFileSizes.get(format).keySet());
            }

            DataFileFormatStat global = new DataFileFormatStat();
            global.setFormatPuid(format);
            global.setComputedOn(computedOn);

            for (Long user : users) {
                DataFileFormatStat stat = new DataFileFormatStat();
                stat.setFormatPuid(format);
                stat.setUsername(userMap.get(user));
                stat.setComputedOn(computedOn);

                stat.setObjects(getValue(objectCounts.get(format), user));
                stat.setDataFiles(getValue(dataFileCounts.get(format), user));
                stat.setDataSize(getValue(dataFileSizes.get(format), user));

                dataFileFormatStatDao.persist(stat);

                global.setObjects(global.getObjects() + stat.getObjects());
                global.setDataFiles(global.getDataFiles() + stat.getDataFiles());
                global.setDataSize(global.getDataSize() + stat.getDataSize());
            }

            dataFileFormatStatDao.persist(global);
        }
    }


    /**
     * Calculates metadata format statistics.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void calculateMetadataFormatStatistics() {
        Map<String, Long> objectCounts = statisticsBrowser.getMetadataFormatObjectCounts();
        Map<String, Long> dataFileCounts = statisticsBrowser.getMetadataFormatDataFileCounts();

        Set<String> formats = new TreeSet<String>();
        formats.addAll(objectCounts.keySet());
        formats.addAll(dataFileCounts.keySet());

        Date computedOn = new Date();

        metadataFormatStatDao.deleteAll();

        for (String format : formats) {
            MetadataFormatStat stat = new MetadataFormatStat();
            stat.setFormatName(format);
            stat.setDataFiles(dataFileCounts.containsKey(format) ? dataFileCounts.get(format) : 0);
            stat.setObjects(objectCounts.containsKey(format) ? objectCounts.get(format) : 0);
            stat.setComputedOn(computedOn);
            metadataFormatStatDao.persist(stat);
        }
    }


    /**
     * Returns the value from the map if neither the map nor the value is <code>null</code>, otherwise 0.
     * 
     * @param map
     *            map
     * @param key
     *            key
     * @return a not-<code>null</code> value from the map, or 0
     */
    private long getValue(Map<Long, Long> map, Long key) {
        Long value = null;
        if (map != null) {
            value = map.get(key);
        }
        return value != null ? value : 0;
    }
}
