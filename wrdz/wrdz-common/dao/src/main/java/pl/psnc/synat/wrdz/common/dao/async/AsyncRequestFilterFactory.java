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

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequest;

/**
 * Specified set of filters for queries concerning {@link AsyncRequest} entities.
 */
public interface AsyncRequestFilterFactory extends GenericQueryFilterFactory<AsyncRequest> {

    /**
     * Filters the entities by requested URL of the asynchronous request. Can use only exact string match.
     * 
     * @param requestedUrl
     *            requested URL of the asynchronous request
     * @return current representations of filters set
     */
    QueryFilter<AsyncRequest> byRequestedUrl(String requestedUrl);


    /**
     * Filters the entities by in progress field.
     * 
     * @param inProgress
     *            whether the request is in progress
     * @return current representations of filters set
     */
    QueryFilter<AsyncRequest> byInProgress(Boolean inProgress);

}
