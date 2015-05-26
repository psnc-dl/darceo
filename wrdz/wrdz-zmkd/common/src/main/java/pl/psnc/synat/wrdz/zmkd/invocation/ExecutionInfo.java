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
package pl.psnc.synat.wrdz.zmkd.invocation;

import java.util.List;

import pl.psnc.synat.wrdz.zmkd.service.HttpMethod;
import pl.psnc.synat.wrdz.zmkd.service.RequestType;

/**
 * Detailed information about a particular execution.
 */
public class ExecutionInfo {

    /** Address of the service. */
    private String address;

    /** HTTP method (GET or POST). */
    private HttpMethod method;

    /** Request type. */
    private RequestType requestType;

    /** List of template parameters. */
    private List<ExecutionTemplateParam> templateParams;

    /** List of query parameters. */
    private List<ExecutionQueryParam> queryParams;

    /** Request param. */
    private ExecutionBodyParam requestParam;

    /** List of form parameters. */
    private List<ExecutionFormParam> formParams;


    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public HttpMethod getMethod() {
        return method;
    }


    public void setMethod(HttpMethod method) {
        this.method = method;
    }


    public RequestType getRequestType() {
        return requestType;
    }


    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }


    public List<ExecutionTemplateParam> getTemplateParams() {
        return templateParams;
    }


    public void setTemplateParams(List<ExecutionTemplateParam> templateParams) {
        this.templateParams = templateParams;
    }


    public List<ExecutionQueryParam> getQueryParams() {
        return queryParams;
    }


    public void setQueryParams(List<ExecutionQueryParam> queryParams) {
        this.queryParams = queryParams;
    }


    public ExecutionBodyParam getRequestParam() {
        return requestParam;
    }


    public void setRequestParam(ExecutionBodyParam requestParam) {
        this.requestParam = requestParam;
    }


    public List<ExecutionFormParam> getFormParams() {
        return formParams;
    }


    public void setFormParams(List<ExecutionFormParam> formParams) {
        this.formParams = formParams;
    }

}
