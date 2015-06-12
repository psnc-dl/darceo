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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Service parameter of a single service call (transformation or delivery).
 */
@Entity
@Table(name = "ZMKD_SERVICE_PARAMETERS", schema = "darceo")
public class ServiceParameter implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -8354197601832528210L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "zmkdServiceParameterSequenceGenerator", sequenceName = "darceo.ZMKD_SP_ID_SEQ",
            allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "zmkdServiceParameterSequenceGenerator")
    @Column(name = "SP_ID", unique = true, nullable = false)
    private long id;

    /**
     * Service for this parameter.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SP_SERVICE_ID", nullable = false)
    private Service service;

    /**
     * Parameter name.
     */
    @Column(name = "SP_NAME", unique = false, nullable = false, length = 255)
    private String name;

    /**
     * Parameter type.
     */
    @Column(name = "SP_TYPE", unique = false, nullable = false, length = 255)
    private String type;

    /**
     * Parameter type.
     */
    @Column(name = "SP_BUNDLE_TYPE", unique = false, nullable = true, length = 255)
    private String bundleType;

    /**
     * Parameter value.
     */
    @Column(name = "SP_VALUE", unique = false, nullable = true, length = 255)
    private String value;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public Service getService() {
        return service;
    }


    public void setService(Service service) {
        this.service = service;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }


    public String getBundleType() {
        return bundleType;
    }


    public void setBundleType(String bundleType) {
        this.bundleType = bundleType;
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }

}
