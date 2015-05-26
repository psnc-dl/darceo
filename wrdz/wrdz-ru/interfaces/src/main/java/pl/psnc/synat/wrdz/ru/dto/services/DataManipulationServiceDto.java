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
package pl.psnc.synat.wrdz.ru.dto.services;

import java.io.Serializable;

import pl.psnc.synat.wrdz.ru.entity.types.ServiceType;

/**
 * Dto for transferring data manipulation service information.
 */
public class DataManipulationServiceDto implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 1713400911982679526L;

    /** Service IRI. */
    private String iri;

    /** Service name. */
    private String name;

    /** Service location URL. */
    private String locationUrl;

    /** Service description. */
    private String description;

    /** Service type. */
    private ServiceType type;


    /**
     * Default constructor.
     */
    public DataManipulationServiceDto() {
        // default constructor
    }


    /**
     * Convenience constructor.
     * 
     * @param iri
     *            service IRI
     * @param name
     *            service name
     * @param locationUrl
     *            service location URL
     * @param description
     *            service description
     * @param type
     *            service type
     */
    public DataManipulationServiceDto(String iri, String name, String locationUrl, String description, ServiceType type) {
        this.iri = iri;
        this.name = name;
        this.locationUrl = locationUrl;
        this.description = description;
        this.type = type;
    }


    public String getIri() {
        return iri;
    }


    public void setIri(String iri) {
        this.iri = iri;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getLocationUrl() {
        return locationUrl;
    }


    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public ServiceType getType() {
        return type;
    }


    public void setType(ServiceType type) {
        this.type = type;
    }
}
