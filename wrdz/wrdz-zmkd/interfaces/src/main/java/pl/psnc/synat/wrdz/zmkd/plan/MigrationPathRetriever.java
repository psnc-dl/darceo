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
package pl.psnc.synat.wrdz.zmkd.plan;

import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPath;

/**
 * This class is used to pre-process migration paths and gather all extra information that is required to perform the
 * particular migration operation.
 */
public interface MigrationPathRetriever {

    /**
     * Retrieves the active migration path from the given plan.
     * 
     * @param planId
     *            migration plan identifier
     * @return detailed information of active path's migration hops, in the order they should be performed
     */
    MigrationPath retrieveActivePath(long planId);

}
