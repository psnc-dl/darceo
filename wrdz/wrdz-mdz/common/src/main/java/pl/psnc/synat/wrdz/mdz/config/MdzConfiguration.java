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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.ScheduleExpression;
import javax.enterprise.inject.Default;
import javax.inject.Singleton;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.config.ScheduleUtils;
import pl.psnc.synat.wrdz.common.exception.WrdzConfigurationError;
import pl.psnc.synat.wrdz.mdz.plugin.VerificationPlugin;

/**
 * The Source Data Monitor (MDZ) module configuration.
 */
@Singleton
@Default
public class MdzConfiguration {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MdzConfiguration.class);

    /** Configuration file. */
    private static final String CONFIG_FILE = "mdz-wrdz-config.xml";

    /** Configuration file element path for {@link #checkFormats} (absolute). */
    private static final String FORMAT_ENABLED = "formats.enabled";

    /** Configuration file element path for {@link #checkIntegrity} (absolute). */
    private static final String INTEGRITY_ENABLED = "integrity.enabled";

    /** Configuration file element path for {@link #analyzeMasterObjectFileFormats} (absolute). */
    private static final String FORMAT_ANALYZE_MASTER = "formats.objects.master";

    /** Configuration file element path for {@link #analyzeOptimizedObjectFileFormats} (absolute). */
    private static final String FORMAT_ANALYZE_OPTIMIZED = "formats.objects.optimized";

    /** Configuration file element path for {@link #analyzeConvertedObjectFileFormats} (absolute). */
    private static final String FORMAT_ANALYZE_CONVERTED = "formats.objects.converted";

    /** Configuration file element path for {@link #formatWorkerAlwaysActive} (absolute). */
    private static final String FORMAT_WORKER_ALWAYS_ACTIVE = "formats.worker.always-active";

    /** Configuration file element path for {@link #formatWorkerActivationSchedule} (absolute). */
    private static final String FORMAT_WORKER_ACTIVATION_SCHEDULE = "formats.worker.activation-schedule";

    /** Configuration file element path for {@link #formatWorkerDeactivationSchedule} (absolute). */
    private static final String FORMAT_WORKER_DEACTIVATION_SCHEDULE = "formats.worker.deactivation-schedule";

    /** Configuration file element path for {@link #formatWorkInitializerSchedule} (absolute). */
    private static final String FORMAT_INITIALIZER_SCHEDULE = "formats.initializer.schedule";

    /** Configuration file element path for @{link {@link #formatVerifierThreshold} (absolute). */
    private static final String FORMAT_VERIFIER_THRESHOLD = "formats.verifier.threshold";

    /** Configuration file element path for {@link #formatWorkerActivationSchedule} (absolute). */
    private static final String INTEGRITY_WORKER_ACTIVATION_SCHEDULE = "integrity.worker.activation-schedule";

    /** Configuration file element path for {@link #formatWorkerDeactivationSchedule} (absolute). */
    private static final String INTEGRITY_WORKER_DEACTIVATION_SCHEDULE = "integrity.worker.deactivation-schedule";

    /** Configuration file element path for {@link #zmdObjectUrl} (absolute). */
    private static final String INTEGRITY_ZMD_OBJECT_URL = "integrity.zmd-object-url";

    /** Configuration file element for {@link #plugins} (absolute). */
    private static final String PLUGINS = "plugins.plugin";

    /** Configuration file element containing the plugin name (relative to {@link #PLUGINS}). */
    private static final String PLUGIN_NAME = "name";

    /** Configuration file element containing the plugin class (relative to {@link #PLUGINS}). */
    private static final String PLUGIN_CLASS = "class";

    /** Configuration file element containing the plugin activation schedule (relative to {@link #PLUGINS}). */
    private static final String PLUGIN_ACTIVATION_SCHEDULE = "activation-schedule";

    /** Configuration file element containing the plugin deactivation schedule (relative to {@link #PLUGINS}). */
    private static final String PLUGIN_DEACTIVATION_SCHEDULE = "deactivation-schedule";

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

    /** Whether to perform format verification. */
    private boolean checkFormats;

    /** Whether to perform integrity verification. */
    private boolean checkIntegrity;

    /** Whether to assess risk for MasterObjects' active file formats. */
    private boolean analyzeMasterObjectFileFormats;

    /** Whether to assess risk for OptimizedObjects' active file formats. */
    private boolean analyzeOptimizedObjectFileFormats;

    /** Whether to assess risk for ConvertedObjects' active file formats. */
    private boolean analyzeConvertedObjectFileFormats;

    /** Whether the worker has to be manually activated or is always on. */
    private boolean formatWorkerAlwaysActive;

    /** When to activate the format worker. */
    private ScheduleExpression formatWorkerActivationSchedule;

    /** When to deactivate the format worker. */
    private ScheduleExpression formatWorkerDeactivationSchedule;

    /** When to run the format work initializer. */
    private ScheduleExpression formatWorkInitializerSchedule;

    /** How many days must pass since format's release before it is considered old. */
    private int formatVerifierThreshold;

    /** When to activate the integrity worker. */
    private ScheduleExpression integrityWorkerActivationSchedule;

    /** When to deactivate the integrity worker. */
    private ScheduleExpression integrityWorkerDeactivationSchedule;

    /** URL used to fetch objects from ZMD. */
    private String zmdObjectUrl;

    /** Configured plugins. */
    private List<PluginInfo> plugins;


    /**
     * Reads the configuration form the {@value #CONFIG_FILE} file.
     */
    @PostConstruct
    protected void init() {
        try {
            XMLConfiguration config = new XMLConfiguration(CONFIG_FILE);
            checkFormats = config.getBoolean(FORMAT_ENABLED, true);
            checkIntegrity = config.getBoolean(INTEGRITY_ENABLED, true);
            analyzeMasterObjectFileFormats = config.getBoolean(FORMAT_ANALYZE_MASTER, true);
            analyzeOptimizedObjectFileFormats = config.getBoolean(FORMAT_ANALYZE_OPTIMIZED, false);
            analyzeConvertedObjectFileFormats = config.getBoolean(FORMAT_ANALYZE_CONVERTED, false);
            formatWorkerAlwaysActive = config.getBoolean(FORMAT_WORKER_ALWAYS_ACTIVE, false);
            formatWorkerActivationSchedule = readSchedule(config.subset(FORMAT_WORKER_ACTIVATION_SCHEDULE));
            formatWorkerDeactivationSchedule = readSchedule(config.subset(FORMAT_WORKER_DEACTIVATION_SCHEDULE));
            formatWorkInitializerSchedule = readSchedule(config.subset(FORMAT_INITIALIZER_SCHEDULE));
            formatVerifierThreshold = config.getInt(FORMAT_VERIFIER_THRESHOLD);
            integrityWorkerActivationSchedule = readSchedule(config.subset(INTEGRITY_WORKER_ACTIVATION_SCHEDULE));
            integrityWorkerDeactivationSchedule = readSchedule(config.subset(INTEGRITY_WORKER_DEACTIVATION_SCHEDULE));
            zmdObjectUrl = config.getString(INTEGRITY_ZMD_OBJECT_URL);

            plugins = new ArrayList<PluginInfo>();

            @SuppressWarnings("unchecked")
            List<HierarchicalConfiguration> pluginConfigs = config.configurationsAt(PLUGINS);
            for (HierarchicalConfiguration pluginConfig : pluginConfigs) {
                String name = pluginConfig.getString(PLUGIN_NAME);
                String clazz = pluginConfig.getString(PLUGIN_CLASS);

                VerificationPlugin plugin;
                try {
                    plugin = (VerificationPlugin) Class.forName(clazz).newInstance();
                } catch (ClassNotFoundException e) {
                    logger.warn("Could not load the plugin class", e);
                    continue;
                } catch (InstantiationException e) {
                    logger.warn("Could not load the plugin class", e);
                    continue;
                } catch (IllegalAccessException e) {
                    logger.warn("Could not load the plugin class", e);
                    continue;
                } catch (ClassCastException e) {
                    logger.warn("Could not load the plugin class", e);
                    continue;
                }

                ScheduleExpression activationSchedule = readSchedule(pluginConfig.subset(PLUGIN_ACTIVATION_SCHEDULE));
                ScheduleExpression deactivationSchedule = readSchedule(pluginConfig
                        .subset(PLUGIN_DEACTIVATION_SCHEDULE));

                plugins.add(new PluginInfo(name, plugin, activationSchedule, deactivationSchedule));
            }

            plugins = Collections.unmodifiableList(plugins);

        } catch (ConfigurationException e) {
            throw new WrdzConfigurationError("Error while loading the MDZ configuration.", e);
        }
    }


    /**
     * Returns <code>true</code> if format verification should be performed.
     * 
     * @return <code>true</code> if format verification should be performed; <code>false</code> otherwise
     */
    public boolean getCheckFormats() {
        return checkFormats;
    }


    /**
     * Returns <code>true</code> if data integrity verification should be performed.
     * 
     * @return <code>true</code> if data integrity verification should be performed; <code>false</code> otherwise
     */
    public boolean getCheckIntegrity() {
        return checkIntegrity;
    }


    /**
     * Returns <code>true</code> if MasterObjects' active file formats should be included in loss risk assessment.
     * 
     * @return <code>true</code> if MasterObjects should be included in loss risk assessment; <code>false</code>
     *         otherwise
     */
    public boolean getAnalyzeMasterObjectFileFormats() {
        return analyzeMasterObjectFileFormats;
    }


    /**
     * Returns <code>true</code> if OptimizedObjects' active file formats should be included in loss risk assessment.
     * 
     * @return <code>true</code> if OptimizedObjects should be included in loss risk assessment; <code>false</code>
     *         otherwise
     */
    public boolean getAnalyzeOptimizedObjectFileFormats() {
        return analyzeOptimizedObjectFileFormats;
    }


    /**
     * Returns <code>true</code> if ConvertedObjects' active file formats should be included in loss risk assessment.
     * 
     * @return <code>true</code> if ConvertedObjects should be included in loss risk assessment; <code>false</code>
     *         otherwise
     */
    public boolean getAnalyzeConvertedObjectFileFormats() {
        return analyzeConvertedObjectFileFormats;
    }


    /**
     * Returns <code>true</code> if the file format risk assessment worker is always active and does not have to be
     * activated/deactivated.
     * 
     * @return <code>true</code> if the file format risk assessment worker is always active; <code>false</code>
     *         otherwise
     */
    public boolean getFormatWorkerAlwaysActive() {
        return formatWorkerAlwaysActive;
    }


    public ScheduleExpression getFormatWorkerActivationSchedule() {
        return ScheduleUtils.clone(formatWorkerActivationSchedule);
    }


    public ScheduleExpression getFormatWorkerDeactivationSchedule() {
        return ScheduleUtils.clone(formatWorkerDeactivationSchedule);
    }


    public ScheduleExpression getFormatWorkInitializerSchedule() {
        return ScheduleUtils.clone(formatWorkInitializerSchedule);
    }


    public int getFormatVerifierThreshold() {
        return formatVerifierThreshold;
    }


    public ScheduleExpression getIntegrityWorkerActivationSchedule() {
        return integrityWorkerActivationSchedule;
    }


    public ScheduleExpression getIntegrityWorkerDeactivationSchedule() {
        return integrityWorkerDeactivationSchedule;
    }


    /**
     * Returns the base object URL that can be used to create an object in ZMD.
     * 
     * @return URL
     */
    public String getZmdObjectUrl() {
        if (zmdObjectUrl == null) {
            throw new IllegalStateException("ZMD object url is not set");
        }
        return zmdObjectUrl;
    }


    /**
     * Returns an URL that can be used to fetch the object with the given identifier.
     * 
     * @param objectIdentifier
     *            identifier of the digital object
     * 
     * @return URL
     */
    public String getZmdObjectUrl(String objectIdentifier) {
        if (zmdObjectUrl == null) {
            throw new IllegalStateException("ZMD object url is not set");
        }
        return zmdObjectUrl + "/" + objectIdentifier;
    }


    public List<PluginInfo> getPlugins() {
        return plugins;
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
