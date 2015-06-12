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

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;

/**
 * Abstract builder for some subclass of @link ServiceInfo.
 * 
 * @param <T>
 *            concrete extension of @link ServiceInfo
 */

public abstract class ServiceInfoBuilder<T extends ServiceInfo> {

    /**
     * Object under construction.
     */
    private final T serviceInfo;


    /**
     * Constructor.
     * 
     * @param clazz
     *            Concrete class of the object.
     */
    protected ServiceInfoBuilder(Class<T> clazz) {
        try {
            this.serviceInfo = clazz.newInstance();
        } catch (IllegalAccessException e) {
            throw new WrdzRuntimeException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new WrdzRuntimeException(e.getMessage(), e);
        }
    }


    /**
     * Sets the IRI of the service.
     * 
     * @param serviceIri
     *            service IRI
     * @return this builder
     */
    public ServiceInfoBuilder<T> setServiceIri(String serviceIri) {
        serviceInfo.setServiceIri(serviceIri);
        return this;
    }


    /**
     * Sets the template URI of the service.
     * 
     * @param address
     *            template URI of the service
     * @return this builder
     */
    public ServiceInfoBuilder<T> setAddress(String address) {
        serviceInfo.setAddress(address);
        return this;
    }


    /**
     * Sets the HTTP method.
     * 
     * @param method
     *            HTTP method
     * @return this builder
     */
    public ServiceInfoBuilder<T> setMethod(HttpMethod method) {
        serviceInfo.setMethod(method);
        return this;
    }


    /**
     * Sets the request type.
     * 
     * @param requestType
     *            request type
     * @return this builder
     */
    public ServiceInfoBuilder<T> setRequestType(RequestType requestType) {
        serviceInfo.setRequestType(requestType);
        return this;
    }


    /**
     * Sets the template parameters.
     * 
     * @param templateParams
     *            template parameters
     * @return this builder
     */
    public ServiceInfoBuilder<T> setTemplateParams(List<ServiceTemplateParamInfo> templateParams) {
        serviceInfo.setTemplateParams(templateParams);
        return this;
    }


    /**
     * Sets the matrix parameters.
     * 
     * @param matrixParams
     *            matrix parameters
     * @return this builder
     */
    public ServiceInfoBuilder<T> setMatrixParams(List<ServiceQueryParamInfo> matrixParams) {
        if (serviceInfo.getQueryParams() == null) {
            serviceInfo.setQueryParams(matrixParams);
        } else {
            serviceInfo.getQueryParams().addAll(matrixParams);
        }
        return this;
    }


    /**
     * Sets the query parameters.
     * 
     * @param queryParams
     *            query parameters
     * @return this builder
     */
    public ServiceInfoBuilder<T> setQueryParams(List<ServiceQueryParamInfo> queryParams) {
        if (serviceInfo.getQueryParams() == null) {
            serviceInfo.setQueryParams(queryParams);
        } else {
            serviceInfo.getQueryParams().addAll(queryParams);
        }
        return this;
    }


    /**
     * Sets the body parameter.
     * 
     * @param bodyParam
     *            body parameter
     * @return this builder
     */
    public ServiceInfoBuilder<T> setBodyParam(ServiceBodyParamInfo bodyParam) {
        serviceInfo.setBodyParam(bodyParam);
        return this;
    }


    /**
     * Sets the form parameters.
     * 
     * @param formParams
     *            form parameters
     * @return this builder
     */
    public ServiceInfoBuilder<T> setFormParams(List<ServiceFormParamInfo> formParams) {
        serviceInfo.setFormParams(formParams);
        return this;
    }


    /**
     * Sets the potential outcomes.
     * 
     * @param outcomes
     *            potential outcomes
     * @return this builder
     */
    public ServiceInfoBuilder<T> setOutcomes(List<ServiceOutcomeInfo> outcomes) {
        serviceInfo.setOutcomes(outcomes);
        return this;
    }


    /**
     * Builds the service info.
     * 
     * @return service info
     */
    public T build() {
        return serviceInfo;
    }
}
