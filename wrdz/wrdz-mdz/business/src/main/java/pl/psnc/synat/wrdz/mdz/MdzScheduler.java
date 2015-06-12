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
package pl.psnc.synat.wrdz.mdz;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import pl.psnc.synat.wrdz.mdz.config.MdzConfiguration;
import pl.psnc.synat.wrdz.mdz.config.PluginInfo;
import pl.psnc.synat.wrdz.mdz.format.FileFormatWorkInitializer;
import pl.psnc.synat.wrdz.mdz.format.FileFormatWorker;
import pl.psnc.synat.wrdz.mdz.integrity.IntegrityWorker;
import pl.psnc.synat.wrdz.mdz.plugin.PluginExecutor;

/**
 * Class responsible for running the periodic jobs required by the MDZ module at their configured time.
 * 
 * @see MdzConfiguration
 */
@Singleton
@Startup
public class MdzScheduler {

    /** Module configuration. */
    @Inject
    private MdzConfiguration configuration;

    /** Injected timer service. */
    @Resource
    private TimerService timerService;

    /** Bean handling the file format work initialization. */
    @EJB
    private FileFormatWorkInitializer formatInitializer;

    /** Bean handling the actual file format loss risk assessment. */
    @EJB
    private FileFormatWorker formatWorker;

    /** Bean handling the data integrity verification. */
    @EJB
    private IntegrityWorker integrityWorker;

    /** Bean handling plugin executions. */
    @EJB
    private PluginExecutor pluginExecutor;


    /**
     * Creates timers and starts the file format worker if it's configured to be always active.
     */
    @PostConstruct
    protected void init() {
        if (configuration.getCheckFormats()) {
            createInitializeFormatWorkTimer();
            if (!configuration.getFormatWorkerAlwaysActive()) {
                createActivateFormatWorkerTimer();
                createDeactivateFormatWorkerTimer();
            } else {
                // always active, resume previous work
                formatWorker.start();
            }
        }
        if (configuration.getCheckIntegrity()) {
            createActivateIntegrityWorkerTimer();
            createDeactivateIntegrityWorkerTimer();
        }

        createPluginTimers();
    }


    /**
     * Creates the timer to periodically initialize file format loss risk assessment work.
     * 
     * @see EventType#INITIALIZE_FORMAT_WORK
     */
    private void createInitializeFormatWorkTimer() {
        TimerConfig timerConfig = new TimerConfig(EventType.INITIALIZE_FORMAT_WORK, false);
        ScheduleExpression expression = configuration.getFormatWorkInitializerSchedule();
        timerService.createCalendarTimer(expression, timerConfig);
    }


    /**
     * Creates the timer to periodically activate the file format loss risk assessment worker.
     * 
     * @see EventType#ACTIVATE_FORMAT_WORKER
     */
    private void createActivateFormatWorkerTimer() {
        TimerConfig timerConfig = new TimerConfig(EventType.ACTIVATE_FORMAT_WORKER, false);
        ScheduleExpression expression = configuration.getFormatWorkerActivationSchedule();
        timerService.createCalendarTimer(expression, timerConfig);
    }


    /**
     * Creates the timer to periodically deactivate the file format loss risk assessment worker.
     * 
     * @see EventType#DEACTIVATE_FORMAT_WORKER
     */
    private void createDeactivateFormatWorkerTimer() {
        TimerConfig timerConfig = new TimerConfig(EventType.DEACTIVATE_FORMAT_WORKER, false);
        ScheduleExpression expression = configuration.getFormatWorkerDeactivationSchedule();
        timerService.createCalendarTimer(expression, timerConfig);
    }


    /**
     * Creates the timer to periodically activate the data integrity verification worker.
     * 
     * @see EventType#ACTIVATE_INTEGRITY_WORKER
     */
    private void createActivateIntegrityWorkerTimer() {
        TimerConfig timerConfig = new TimerConfig(EventType.ACTIVATE_INTEGRITY_WORKER, false);
        ScheduleExpression expression = configuration.getIntegrityWorkerActivationSchedule();
        timerService.createCalendarTimer(expression, timerConfig);
    }


    /**
     * Creates the timer to periodically deactivate the data integrity verification worker.
     * 
     * @see EventType#DEACTIVATE_INTEGRITY_WORKER
     */
    private void createDeactivateIntegrityWorkerTimer() {
        TimerConfig timerConfig = new TimerConfig(EventType.DEACTIVATE_INTEGRITY_WORKER, false);
        ScheduleExpression expression = configuration.getIntegrityWorkerDeactivationSchedule();
        timerService.createCalendarTimer(expression, timerConfig);
    }


    /**
     * Creates the timers to periodically activate and deactivate the configured plugins.
     */
    private void createPluginTimers() {
        for (PluginInfo info : configuration.getPlugins()) {
            pluginExecutor.registerPlugin(info.getName(), info.getPlugin());

            TimerConfig timerConfig = new TimerConfig(new PluginEvent(info.getName(), PluginEventType.ACTIVATE), false);
            ScheduleExpression expression = info.getActivationSchedule();
            timerService.createCalendarTimer(expression, timerConfig);

            timerConfig = new TimerConfig(new PluginEvent(info.getName(), PluginEventType.DEACTIVATE), false);
            expression = info.getDeactivationSchedule();
            timerService.createCalendarTimer(expression, timerConfig);
        }
    }


    /**
     * Handles all the timer events defined for this class. The event type can be obtained using the
     * {@link Timer#getInfo()} method.
     * 
     * @param timer
     *            timer that triggered the event
     */
    @Timeout
    protected void onTimeout(Timer timer) {
        if (timer.getInfo() instanceof EventType) {
            switch ((EventType) timer.getInfo()) {
                case INITIALIZE_FORMAT_WORK:
                    formatInitializer.initializeWork();
                    formatWorker.start();
                    break;
                case ACTIVATE_FORMAT_WORKER:
                    formatWorker.activate();
                    break;
                case DEACTIVATE_FORMAT_WORKER:
                    formatWorker.deactivate();
                    break;
                case ACTIVATE_INTEGRITY_WORKER:
                    integrityWorker.activate();
                    break;
                case DEACTIVATE_INTEGRITY_WORKER:
                    integrityWorker.deactivate();
                    break;
                default:
                    throw new RuntimeException("Unexpected EventType value: " + timer.getInfo());
            }
        } else if (timer.getInfo() instanceof PluginEvent) {
            PluginEvent event = (PluginEvent) timer.getInfo();
            switch (event.type) {
                case ACTIVATE:
                    pluginExecutor.start(event.pluginName);
                    break;
                case DEACTIVATE:
                    pluginExecutor.stop(event.pluginName);
                    break;
                default:
                    throw new RuntimeException("Unexpected PluginEventType value: " + event.type);
            }
        }
    }


    /**
     * General events handled by this class. To be stored within the {@link Timer} as its info.
     */
    private enum EventType {

        /** Run the file format work initializer. */
        INITIALIZE_FORMAT_WORK,

        /** Activate the file format worker. */
        ACTIVATE_FORMAT_WORKER,

        /** Deactivate the file format worker. */
        DEACTIVATE_FORMAT_WORKER,

        /** Activate the data integrity worker. */
        ACTIVATE_INTEGRITY_WORKER,

        /** Deactivate the data integrity worker. */
        DEACTIVATE_INTEGRITY_WORKER;
    }


    /**
     * Plugin events handled by this class. To be stored within the {@link Timer} as its info.
     */
    private final class PluginEvent implements Serializable {

        /** Serial version UID. */
        private static final long serialVersionUID = -640135026242236953L;

        /** Name of the plugin. */
        private final String pluginName;

        /** Type of the event. */
        private final PluginEventType type;


        /**
         * Constructor.
         * 
         * @param pluginName
         *            plugin name
         * @param type
         *            event type
         */
        private PluginEvent(String pluginName, PluginEventType type) {
            this.pluginName = pluginName;
            this.type = type;
        }
    }


    /**
     * Plugin event type.
     */
    private enum PluginEventType {

        /** Activate the plugin. */
        ACTIVATE,

        /** Deactivate the plugin. */
        DEACTIVATE;
    }
}
