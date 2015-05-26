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
 * Service outcome.
 */
public class ServiceOutcome implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -5478655317367860812L;

    /** Name of outcome - IRI. */
    private String name;

    /** Type of outcome - IRI. */
    private String type;

    /** In case if bundle - type of items or null otherwise - IRI. */
    private String bundleType;


    /**
     * Default constructor.
     */
    public ServiceOutcome() {
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
    public ServiceOutcome(String name, String type, String bundleType) {
        this.name = name;
        this.type = type;
        this.bundleType = bundleType;
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

}
