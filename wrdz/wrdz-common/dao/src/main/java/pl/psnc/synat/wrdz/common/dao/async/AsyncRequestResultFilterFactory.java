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
package pl.psnc.synat.wrdz.common.dao.async;

import java.util.Date;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;

/**
 * Specified set of filters for queries concerning {@link AsyncRequestResult} entities.
 */
public interface AsyncRequestResultFilterFactory extends GenericQueryFilterFactory<AsyncRequestResult> {

    /**
     * Filters the result by request id.
     * 
     * @param requestId
     *            request id
     * @return current representations of filters set
     */
    QueryFilter<AsyncRequestResult> byRequestId(String requestId);


    /**
     * Filters the results by the completed date matching all completed before the given date.
     * 
     * @param date
     *            reference date
     * @param inclusive
     *            if <code>true</code> will cause referenced date to be included in searched boundary, else it would
     *            exclude it.
     * @return current representations of filters set
     */
    QueryFilter<AsyncRequestResult> byCompletedBefore(Date date, boolean inclusive);


    /**
     * Filters the results by the completed date matching all completed after the given date.
     * 
     * @param date
     *            reference date
     * @param inclusive
     *            if <code>true</code> will cause referenced date to be included in searched boundary, else it would
     *            exclude it.
     * @return current representations of filters set
     */
    QueryFilter<AsyncRequestResult> byCompletedAfter(Date date, boolean inclusive);


    /**
     * Filters the entities by requested URL of the asynchronous request. Can use only exact string match.
     * 
     * @param requestedUrl
     *            requested URL of the asynchronous request
     * @return current representations of filters set
     */
    QueryFilter<AsyncRequestResult> byRequestedUrl(String requestedUrl);


    /**
     * Filters the entities by HTTP code.
     * 
     * @param code
     *            HTTP code of the response
     * @return current representations of filters set
     */
    QueryFilter<AsyncRequestResult> byCode(Integer code);

}
