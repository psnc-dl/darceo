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
package pl.psnc.synat.wrdz.ms.config;

import javax.annotation.PostConstruct;
import javax.ejb.ScheduleExpression;
import javax.enterprise.inject.Default;
import javax.inject.Singleton;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import pl.psnc.synat.wrdz.common.config.ScheduleUtils;
import pl.psnc.synat.wrdz.common.exception.WrdzConfigurationError;

/**
 * The System Monitor (MS) module configuration.
 */
@Singleton
@Default
public class MsConfiguration {

    /** Configuration file. */
    private static final String CONFIG_FILE = "ms-wrdz-config.xml";

    /** Configuration file element path for {@link #statisticsCalculationSchedule} (absolute). */
    private static final String STATISTICS_CALCULATION_SCHEDULE = "statistics.calculation-schedule";

    /** Configuration file element containing the hour part of {@link ScheduleExpression} (relative). */
    private static final String SCHEDULE_HOUR = "hour";

    /** Configuration file element containing the minute part of {@link ScheduleExpression} (relative). */
    private static final String SCHEDULE_MINUTE = "minute";

    /** Configuration file element containing the dayOfWeek part of {@link ScheduleExpression} (relative). */
    private static final String SCHEDULE_DAY_OF_WEEK = "day-of-week";

    /** Configuration file element containing the dayOfMonth part of {@link ScheduleExpression} (relative). */
    private static final String SCHEDULE_DAY_OF_MONTH = "day-of-month";

    /** Configuration file element containing the month part of {@link ScheduleExpression} (relative). */
    private static final String SCHEDULE_MONTH = "month";

    /** Statistics calculation schedule. */
    private ScheduleExpression statisticsCalculationSchedule;


    /**
     * Reads the configuration form the {@value #CONFIG_FILE} file.
     */
    @PostConstruct
    protected void init() {
        try {
            Configuration config = new XMLConfiguration(CONFIG_FILE);
            statisticsCalculationSchedule = readSchedule(config.subset(STATISTICS_CALCULATION_SCHEDULE));
        } catch (ConfigurationException e) {
            throw new WrdzConfigurationError("Error while loading the MS configuration.", e);
        } catch (IllegalArgumentException e) {
            throw new WrdzConfigurationError("Error while loading the MS configuration.", e);
        } catch (NullPointerException e) {
            throw new WrdzConfigurationError("Error while loading the MS configuration.", e);
        }
    }


    public ScheduleExpression getStatisticsCalculationSchedule() {
        return ScheduleUtils.clone(statisticsCalculationSchedule);
    }


    /**
     * Reads the schedule definition from the given configuration subset. Handles the hour, minute, day of week, day of
     * month, and month values.
     * 
     * @param config
     *            configuration subset
     * @return configured schedule object to be used with a timer
     */
    private ScheduleExpression readSchedule(Configuration config) {
        ScheduleExpression expression = new ScheduleExpression();
        if (config.containsKey(SCHEDULE_HOUR)) {
            expression.hour(config.getString(SCHEDULE_HOUR));
        }
        if (config.containsKey(SCHEDULE_MINUTE)) {
            expression.minute(config.getString(SCHEDULE_MINUTE));
        }
        if (config.containsKey(SCHEDULE_DAY_OF_WEEK)) {
            expression.dayOfWeek(config.getString(SCHEDULE_DAY_OF_WEEK));
        }
        if (config.containsKey(SCHEDULE_DAY_OF_MONTH)) {
            expression.dayOfMonth(config.getString(SCHEDULE_DAY_OF_MONTH));
        }
        if (config.containsKey(SCHEDULE_MONTH)) {
            expression.month(config.getString(SCHEDULE_MONTH));
        }
        return expression;
    }
}
