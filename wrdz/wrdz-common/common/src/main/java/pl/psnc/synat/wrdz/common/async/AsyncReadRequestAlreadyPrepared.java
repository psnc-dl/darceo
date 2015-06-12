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
package pl.psnc.synat.wrdz.common.async;

import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;

/**
 * Thrown when an asynchronous read request is about to start but this request is already processed due to earlier
 * request of another user. This exception carries prepared response for this request.
 */
public class AsyncReadRequestAlreadyPrepared extends Exception {

    /** Serial Version UID. */
    private static final long serialVersionUID = 2803446227307086154L;

    /**
     * Result for earlier asynchronous request.
     */
    private AsyncRequestResult result;


    /**
     * Constructs a new <code>AsyncReadRequestAlreadyProcessed</code> with the specified result.
     * 
     * @param result
     *            result for earlier asynchronous request.
     */
    public AsyncReadRequestAlreadyPrepared(AsyncRequestResult result) {
        this.result = result;
    }


    public AsyncRequestResult getResult() {
        return result;
    }

}
