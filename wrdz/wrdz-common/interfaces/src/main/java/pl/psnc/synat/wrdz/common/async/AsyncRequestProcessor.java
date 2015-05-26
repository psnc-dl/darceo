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

import java.io.Serializable;

import javax.ejb.Local;

/**
 * Specifies the interface of the general bean that processes asynchronous requests.
 * 
 * @param <T>
 *            set of subtypes of the request that beans which implement this interface can handle
 */
@Local
public interface AsyncRequestProcessor<T extends AsyncRequestEnum> {

    /**
     * Processes the (modification) request asynchronously. Generate request identifier, save the state of the request
     * for fetching services and put the request to a JMS queue in order to further process.
     * 
     * @param requestType
     *            request type
     * @param requestObject
     *            request object
     * @return request id
     */
    String processRequestAsynchronously(T requestType, Serializable requestObject);


    /**
     * Processes the (read) request asynchronously. Generate request identifier, save the state of the request for
     * fetching services (together with the unique requested URL) and put the request to a JMS queue in order to further
     * process.
     * 
     * @param requestType
     *            request type
     * @param requestObject
     *            request object
     * @param requestedUrl
     *            requested URL
     * @return request id
     */
    String processRequestAsynchronously(T requestType, Serializable requestObject, String requestedUrl);

}
