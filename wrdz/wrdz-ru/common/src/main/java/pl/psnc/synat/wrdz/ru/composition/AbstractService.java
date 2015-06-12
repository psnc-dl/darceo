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
package pl.psnc.synat.wrdz.ru.composition;

import java.io.Serializable;
import java.util.List;

/**
 * Abstract service.
 */
public abstract class AbstractService implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 8977307406516971004L;

    /**
     * Semantic descriptor id of OWL-S descriptor for a service.
     */
    private long descriptorId;

    /**
     * IRI part of an OWL-S ontology ID (context of the semantic descriptor - graph name). Most location URL.
     */
    private String ontologyIri;

    /**
     * Service id.
     */
    private long serviceId;

    /**
     * Service IRI.
     */
    private String serviceIri;

    /**
     * Service name.
     */
    private String serviceName;

    /**
     * Parameters of the service and their values.
     */
    private List<ServiceParam> parameters;

    /**
     * Outcomes of the service.
     */
    private List<ServiceOutcome> outcomes;


    public long getDescriptorId() {
        return descriptorId;
    }


    public void setDescriptorId(long descriptorId) {
        this.descriptorId = descriptorId;
    }


    public String getOntologyIri() {
        return ontologyIri;
    }


    public void setOntologyIri(String ontologyIri) {
        this.ontologyIri = ontologyIri;
    }


    public long getServiceId() {
        return serviceId;
    }


    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }


    public String getServiceIri() {
        return serviceIri;
    }


    public void setServiceIri(String serviceIri) {
        this.serviceIri = serviceIri;
    }


    public String getServiceName() {
        return serviceName;
    }


    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }


    public List<ServiceParam> getParameters() {
        return parameters;
    }


    public void setParameters(List<ServiceParam> parameters) {
        this.parameters = parameters;
    }


    public List<ServiceOutcome> getOutcomes() {
        return outcomes;
    }


    public void setOutcomes(List<ServiceOutcome> outcomes) {
        this.outcomes = outcomes;
    }

}
