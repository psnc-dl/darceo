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
package pl.psnc.synat.wrdz.mdz.dao.plugin;

import java.util.Date;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.dao.GenericDao;
import pl.psnc.synat.wrdz.mdz.entity.plugin.PluginIteration;

/**
 * An interface for a class managing the persistence of {@link PluginIteration} class.
 */
@Local
public interface PluginIterationDao extends GenericDao<PluginIteration, String> {

    /**
     * Returns the last recorded iteration of the given plugin.
     * 
     * @param pluginName
     *            plugin name
     * @return entity instance, or <code>null</code> if no instance exists
     */
    PluginIteration getLast(String pluginName);


    /**
     * Counts the number of iterations of the given plugin.
     * 
     * @param pluginName
     *            pluginName
     * @return the number of recorded iterations
     */
    long countAll(String pluginName);


    /**
     * Removes all iterations of the given plugin from the table.
     * 
     * @param pluginName
     *            plugin name
     */
    void deleteAll(String pluginName);


    /**
     * Returns the date when the first iteration was started.
     * 
     * @param pluginName
     *            plugin name
     * @return the date when the first iteration was started
     */
    Date getFirstStarted(String pluginName);


    /**
     * Returns the date when the last iteration was finished.
     * 
     * @param pluginName
     *            plugin name
     * @return the date when the last iteration was finished
     */
    Date getLastFinished(String pluginName);
}
