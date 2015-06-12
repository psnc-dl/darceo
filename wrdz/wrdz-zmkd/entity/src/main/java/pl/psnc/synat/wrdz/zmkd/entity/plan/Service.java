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
package pl.psnc.synat.wrdz.zmkd.entity.plan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Single service call.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "S_TYPE", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue("SERVICE")
@Table(name = "ZMKD_SERVICES", schema = "darceo")
public class Service implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 8944769154931024477L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "serviceSequenceGenerator", sequenceName = "darceo.ZMKD_S_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "serviceSequenceGenerator")
    @Column(name = "S_ID", unique = true, nullable = false)
    protected long id;

    /**
     * IRI of an OWL-S ontology describing a service.
     */
    @Column(name = "S_ONTOLOGY_IRI", unique = false, nullable = false, length = 255)
    protected String ontologyIri;

    /**
     * Service IRI.
     */
    @Column(name = "S_SERVICE_IRI", unique = false, nullable = false, length = 255)
    protected String serviceIri;

    /**
     * Service name.
     */
    @Column(name = "S_SERVICE_NAME", unique = false, nullable = false, length = 100)
    protected String serviceName;

    /**
     * List of service parameters.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "service", cascade = { CascadeType.ALL })
    @PrivateOwned
    protected List<ServiceParameter> parameters = new ArrayList<ServiceParameter>();

    /**
     * List of service outcomes.
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "service", cascade = { CascadeType.ALL })
    @PrivateOwned
    protected List<ServiceOutcome> outcomes = new ArrayList<ServiceOutcome>();


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getOntologyIri() {
        return ontologyIri;
    }


    public void setOntologyIri(String ontologyIri) {
        this.ontologyIri = ontologyIri;
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


    public List<ServiceParameter> getParameters() {
        return parameters;
    }


    public void setParameters(List<ServiceParameter> parameters) {
        this.parameters = parameters;
    }


    public List<ServiceOutcome> getOutcomes() {
        return outcomes;
    }


    public void setOutcomes(List<ServiceOutcome> outcomes) {
        this.outcomes = outcomes;
    }

}
