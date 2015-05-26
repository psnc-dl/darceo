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
package pl.psnc.synat.wrdz.mdz.config;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.ejb.ScheduleExpression;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Default implementation of {@link MdzConfigurationBrowser}.
 */
@Stateless
public class MdzConfigurationBrowserBean implements MdzConfigurationBrowser {

    /** Label: format verification. */
    private static final String FORMAT_VERIFICATION = "Format verification";

    /** Label: whether to check file formats from master objects. */
    private static final String ANALYZE_MASTER_OBJECTS = "Assess file formats from master objects";

    /** Label: whether to check file formats from optimized objects. */
    private static final String ANALYZE_OPTIMIZED_OBJECTS = "Assess file formats from optimized objects";

    /** Label: whether to check file formats from converted objects. */
    private static final String ASSESS_CONVERTED_OBJECTS = "Assess file formats from converted objects";

    /** Label: format age threshold. */
    private static final String FORMAT_AGE_THRESHOLD = "Format age threshold (days)";

    /** Label: format work initializer schedule. */
    private static final String FORMAT_WORK_INITIALIZER_SCHEDULE = "Format work initializer schedule";

    /** Label: format worker schedule. */
    private static final String FORMAT_WORKER_SCHEDULE = "Format worker schedule";

    /** Label: integrity verification. */
    private static final String INTEGRITY_VERIFICATION = "Integrity verification";

    /** Label: integrity worker schedule. */
    private static final String INTEGRITY_WORKER_SCHEDULE = "Integrity worker schedule";

    /** Keyword: enabled. */
    private static final String ENABLED = "enabled";

    /** Keyword: disabled. */
    private static final String DISABLED = "disabled";

    /** Keyword: always active. */
    private static final String ALWAYS_ACTIVE = "always active";

    /** Module configuration. */
    @Inject
    private MdzConfiguration configuration;


    @Override
    public Map<String, String> getSummary() {

        Map<String, String> map = new LinkedHashMap<String, String>();

        if (configuration.getCheckFormats()) {
            map.put(FORMAT_VERIFICATION, ENABLED);
            map.put(ANALYZE_MASTER_OBJECTS, "" + configuration.getAnalyzeMasterObjectFileFormats());
            map.put(ANALYZE_OPTIMIZED_OBJECTS, "" + configuration.getAnalyzeOptimizedObjectFileFormats());
            map.put(ASSESS_CONVERTED_OBJECTS, "" + configuration.getAnalyzeConvertedObjectFileFormats());
            map.put(FORMAT_AGE_THRESHOLD, "" + configuration.getFormatVerifierThreshold());
            map.put(FORMAT_WORK_INITIALIZER_SCHEDULE, toString(configuration.getFormatWorkInitializerSchedule()));
            if (configuration.getFormatWorkerAlwaysActive()) {
                map.put(FORMAT_WORKER_SCHEDULE, ALWAYS_ACTIVE);
            } else {
                map.put(FORMAT_WORKER_SCHEDULE, toString(configuration.getFormatWorkerActivationSchedule()) + " - "
                        + toString(configuration.getFormatWorkerDeactivationSchedule()));
            }
        } else {
            map.put(FORMAT_VERIFICATION, DISABLED);
        }

        if (configuration.getCheckIntegrity()) {
            map.put(INTEGRITY_VERIFICATION, ENABLED);
            map.put(INTEGRITY_WORKER_SCHEDULE, toString(configuration.getIntegrityWorkerActivationSchedule()) + " - "
                    + toString(configuration.getIntegrityWorkerDeactivationSchedule()));
        } else {
            map.put(INTEGRITY_VERIFICATION, DISABLED);
        }

        return map;
    }


    /**
     * Converts the given schedule expression to a cron-like string.
     * 
     * @param exp
     *            expression to convert
     * @return cron-like string representing the given expression
     */
    private String toString(ScheduleExpression exp) {
        if (exp == null) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(exp.getMinute()).append(' ');
        builder.append(exp.getHour()).append(' ');
        builder.append(exp.getDayOfMonth()).append(' ');
        builder.append(exp.getMonth()).append(' ');
        builder.append(exp.getDayOfWeek());

        return builder.toString();
    }
}
