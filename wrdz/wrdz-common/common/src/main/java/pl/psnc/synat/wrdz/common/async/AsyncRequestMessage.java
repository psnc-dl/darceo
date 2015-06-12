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
import java.security.Principal;

/**
 * Wrapper of asynchronous request sent to asynchronous queues.
 */
public class AsyncRequestMessage implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -3960374823462941954L;

    /**
     * Asynchronous request.
     */
    private Serializable request;

    /**
     * Request id.
     */
    private String requestId;

    /**
     * Request type.
     */
    private String requestType;

    /**
     * User who sent this request.
     */
    private Principal userPrincipal;

    /**
     * Http status code the request ended with.
     */
    private Integer resultCode;


    /**
     * Constructs AsyncRequestMessage.
     * 
     * @param request
     *            request
     * @param requestId
     *            request id
     * @param requestType
     *            request type
     * @param userPrincipal
     *            user's principal
     * 
     */
    public AsyncRequestMessage(Serializable request, String requestId, String requestType, Principal userPrincipal) {
        this.request = request;
        this.requestId = requestId;
        this.requestType = requestType;
        this.userPrincipal = userPrincipal;
    }


    public Serializable getRequest() {
        return request;
    }


    public void setRequest(Serializable request) {
        this.request = request;
    }


    public String getRequestId() {
        return requestId;
    }


    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }


    public String getRequestType() {
        return requestType;
    }


    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }


    public Principal getUserPrincipal() {
        return userPrincipal;
    }


    public void setUserPrincipal(Principal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }


    public Integer getResultCode() {
        return resultCode;
    }


    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }
}
