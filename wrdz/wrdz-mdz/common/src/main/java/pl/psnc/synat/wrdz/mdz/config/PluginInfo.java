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

import javax.ejb.ScheduleExpression;

import pl.psnc.synat.wrdz.common.config.ScheduleUtils;
import pl.psnc.synat.wrdz.mdz.plugin.VerificationPlugin;

/**
 * Plugin configuration.
 */
public class PluginInfo {

    /** Plugin name. */
    private final String name;

    /** Plugin implementation. */
    private final VerificationPlugin plugin;

    /** Plugin activation schedule. */
    private final ScheduleExpression activationSchedule;

    /** Plugin deactivation schedule. */
    private final ScheduleExpression deactivationSchedule;


    /**
     * Constructor.
     * 
     * @param name
     *            plugin name
     * @param plugin
     *            plugin implementation
     * @param activationSchedule
     *            activation schedule
     * @param deactivationSchedule
     *            deactivation schedule
     */
    public PluginInfo(String name, VerificationPlugin plugin, ScheduleExpression activationSchedule,
            ScheduleExpression deactivationSchedule) {
        this.name = name;
        this.plugin = plugin;
        this.activationSchedule = activationSchedule;
        this.deactivationSchedule = deactivationSchedule;
    }


    public String getName() {
        return name;
    }


    public VerificationPlugin getPlugin() {
        return plugin;
    }


    public ScheduleExpression getActivationSchedule() {
        return ScheduleUtils.clone(activationSchedule);
    }


    public ScheduleExpression getDeactivationSchedule() {
        return ScheduleUtils.clone(deactivationSchedule);
    }
}
