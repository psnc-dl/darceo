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
package pl.psnc.synat.wrdz.zmd.object;

/**
 * Constants used in <code>ObjectManagerService</code> class.
 */
public final class ObjectManagerServiceConsts {

    /**
     * Private constructor.
     */
    private ObjectManagerServiceConsts() {
    }


    /************************************************************************************************************/

    /**
     * Path URI for the service that fetches responses for asynchronous requests sent to
     * <code>ObjectManagerService</code>.
     */
    static final String ASYNC_REQUEST_FETCHER_SERVICE_PATH_URI = "async/object/";

    /************************************************************************************************************/

}
