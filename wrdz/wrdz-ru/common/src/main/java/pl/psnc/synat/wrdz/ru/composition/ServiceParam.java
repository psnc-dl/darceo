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

/**
 * Service parameter.
 */
public class ServiceParam implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -3466300322822099452L;

    /** Name of parameter - IRI. */
    private String name;

    /** Type of parameter - IRI. */
    private String type;

    /** In case if bundle - type of items or null otherwise - IRI. */
    private String bundleType;

    /** Value of parameter? . */
    private String value;


    /**
     * Default constructor.
     */
    public ServiceParam() {
    }


    /**
     * Constructor.
     * 
     * @param name
     *            name
     * @param type
     *            type
     * @param bundleType
     *            bundle type
     */
    public ServiceParam(String name, String type, String bundleType) {
        this.name = name;
        this.type = type;
        this.bundleType = bundleType;
    }


    /**
     * Constructor.
     * 
     * @param name
     *            name
     * @param type
     *            type
     * @param bundleType
     *            bundle type
     * @param value
     *            value
     */
    public ServiceParam(String name, String type, String bundleType, String value) {
        this.name = name;
        this.type = type;
        this.bundleType = bundleType;
        this.value = value;
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


    public boolean isBundle() {
        return (this.bundleType != null);
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }

}
