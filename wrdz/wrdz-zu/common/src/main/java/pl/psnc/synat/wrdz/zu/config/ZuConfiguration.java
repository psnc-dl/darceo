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
package pl.psnc.synat.wrdz.zu.config;

import javax.annotation.PostConstruct;
import javax.ejb.ScheduleExpression;
import javax.inject.Singleton;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.config.ScheduleUtils;
import pl.psnc.synat.wrdz.common.exception.WrdzConfigurationError;

/**
 * Class representing ZU configuration, loads the configuration file and provides methods for reading it.
 */
@Singleton
public class ZuConfiguration {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ZuConfiguration.class);

    /** Configuration file. */
    private static final String CONFIG_FILE = "zu-wrdz-config.xml";

    /** Configuration file element path for {@link #certificateCheckThreshold} (absolute). */
    private static final String CERTIFICATE_CHECK_THRESHOLD = "certificate-check.threshold";

    /** Configuration file element path for {@link #certificateCheckSchedule} (absolute). */
    private static final String CERTIFICATE_CHECK_SCHEDULE = "certificate-check.schedule";

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

    /** Certificate check threshold (in days). */
    private int certificateCheckThreshold;

    /** Certificate check schedule. */
    private ScheduleExpression certificateCheckSchedule;

    /** Configuration object. */
    private org.apache.commons.configuration.Configuration config;


    /**
     * Creates the configuration object form the {@value #CONFIG_FILE} file.
     */
    @PostConstruct
    public void init() {
        try {
            config = new XMLConfiguration(CONFIG_FILE);
            certificateCheckThreshold = config.getInt(CERTIFICATE_CHECK_THRESHOLD);
            certificateCheckSchedule = readSchedule(config.subset(CERTIFICATE_CHECK_SCHEDULE));
        } catch (ConfigurationException e) {
            logger.error("There was a problem with loading the configuration.", e);
            throw new WrdzConfigurationError(e);
        }
    }


    /**
     * Returns the digest algorithm for password hashing.
     * 
     * @return digest algorithm
     */
    public String getPasswordDigestAlgorithm() {
        return config.getString("password-hashing.digest-algorithm");
    }


    /**
     * Returns the encoding for password hashing.
     * 
     * @return encoding of a password
     */
    public String getPasswordEncoding() {
        return config.getString("password-hashing.encoding");
    }


    /**
     * Returns the charset for password hashing.
     * 
     * @return charset of a password
     */
    public String getPasswordCharset() {
        return config.getString("password-hashing.charset");
    }


    public int getCertificateCheckThreshold() {
        return certificateCheckThreshold;
    }


    public ScheduleExpression getCertificateCheckSchedule() {
        return ScheduleUtils.clone(certificateCheckSchedule);
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
