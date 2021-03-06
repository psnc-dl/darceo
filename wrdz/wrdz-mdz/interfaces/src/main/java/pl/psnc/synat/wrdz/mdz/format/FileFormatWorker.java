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
package pl.psnc.synat.wrdz.mdz.format;

import javax.ejb.Local;

/**
 * Manages the actual loss risk assessment work after a work cycle has been initialized.
 * 
 * Implementations must be able to resume their work if the worker is stopped and then started again.
 */
@Local
public interface FileFormatWorker {

    /**
     * Activates the worker and starts it if possible.
     */
    void activate();


    /**
     * Deactivates the worker and stops it if it is currently running.
     */
    void deactivate();


    /**
     * Starts the worker. Worker will be started only if it is currently active and not yet running.
     */
    void start();


    /**
     * Stops the worker.
     */
    void stop();
}
