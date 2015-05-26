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
 * Responsible for initializing a single cycle of loss risk assessment work that will be carried out by the worker when
 * it's active.
 * 
 * Work initializer populates the database with FileFormat entity instances that represent formats requiring loss risk
 * assessment.
 */
@Local
public interface FileFormatWorkInitializer {

    /**
     * Imports the file formats that require loss risk assessment and saves them in the database as FileFormat
     * instances.
     * 
     * If the previous work cycle has not yet finished and there are still some FileFormat instances left in the
     * database, this method does nothing.
     */
    void initializeWork();
}
