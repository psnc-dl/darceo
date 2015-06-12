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
package pl.psnc.synat.wrdz.mdz.message;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.mdz.entity.format.FileFormat;
import pl.psnc.synat.wrdz.mdz.plugin.PluginExecutionReport;

/**
 * Responsible for communication with other modules.
 */
@Local
public interface MdzMessenger {

    /**
     * Notifies the system monitor that the given file format requires migration.
     * 
     * @param format
     *            format that requires migration
     */
    void notifyMigrationRequired(FileFormat format);


    /**
     * Notifies the system monitor that the given digital object is corrupted.
     * 
     * @param identifier
     *            identifier of the object that is corrupted
     */
    void notifyObjectCorrupted(String identifier);


    /**
     * Forwards the plugin execution report to plugin topic subscribers.
     * 
     * @param report
     *            plugin execution report
     */
    void forwardPluginReport(PluginExecutionReport report);
}
