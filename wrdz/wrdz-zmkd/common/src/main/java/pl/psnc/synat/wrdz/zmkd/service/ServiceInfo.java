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
package pl.psnc.synat.wrdz.zmkd.service;

import java.util.List;


/**
 * Detailed information about a single service.
 */
public class ServiceInfo {

    /** IRI of the service. */
    private String serviceIri;

    /** Template URI of the service. */
    private String address;

    /** HTTP method (GET, POST, PUT). */
    private HttpMethod method;

    /** Request type. */
    private RequestType requestType;

    /** List of template parameters. */
    private List<ServiceTemplateParamInfo> templateParams;

    /** List of query parameters. */
    private List<ServiceQueryParamInfo> queryParams;

    /** Body parameter. */
    private ServiceBodyParamInfo bodyParam;

    /** List of form parameters. */
    private List<ServiceFormParamInfo> formParams;

    /** List of potential outcomes. */
    private List<ServiceOutcomeInfo> outcomes;


    public String getServiceIri() {
        return serviceIri;
    }


    public void setServiceIri(String serviceIri) {
        this.serviceIri = serviceIri;
    }


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


    public List<ServiceTemplateParamInfo> getTemplateParams() {
        return templateParams;
    }


    public void setTemplateParams(List<ServiceTemplateParamInfo> templateParams) {
        this.templateParams = templateParams;
    }


    public List<ServiceQueryParamInfo> getQueryParams() {
        return queryParams;
    }


    public void setQueryParams(List<ServiceQueryParamInfo> queryParams) {
        this.queryParams = queryParams;
    }


    public ServiceBodyParamInfo getBodyParam() {
        return bodyParam;
    }


    public void setBodyParam(ServiceBodyParamInfo bodyParam) {
        this.bodyParam = bodyParam;
    }


    public List<ServiceFormParamInfo> getFormParams() {
        return formParams;
    }


    public void setFormParams(List<ServiceFormParamInfo> formParams) {
        this.formParams = formParams;
    }


    public List<ServiceOutcomeInfo> getOutcomes() {
        return outcomes;
    }


    public void setOutcomes(List<ServiceOutcomeInfo> outcomes) {
        this.outcomes = outcomes;
    }
}
