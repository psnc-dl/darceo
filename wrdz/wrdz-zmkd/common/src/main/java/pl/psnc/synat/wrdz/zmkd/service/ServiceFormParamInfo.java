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

import java.util.ArrayList;
import java.util.List;

/**
 * Detailed information about a particular form parameter of the hop.
 */
public class ServiceFormParamInfo {

    /** Semantic name of parameter. */
    private String semanticName;

    /** Semantic type of parameter - IRI. */
    private String semanticType;

    /** Semantic type of items in case of a bundle type. */
    private String bundleType;

    /** Name of parameter (in WADL). */
    private String name;

    /**
     * Values --- list of string representations of values of the technicalType type. For file it is not supported.
     */
    private List<String> values;

    /** Whether this parameter is required. */
    private boolean required;

    /** Whether the value of this parameter is a list. */
    private boolean repeating;

    /**
     * URI of the technical type, for instance: http://www.w3.org/2001/XMLSchema#int - for string values .
     */
    private String technicalType;

    /** Mimetype (in WADL) - for file parameters. */
    private String mimetype;


    public String getSemanticName() {
        return semanticName;
    }


    public void setSemanticName(String semanticName) {
        this.semanticName = semanticName;
    }


    public String getSemanticType() {
        return semanticType;
    }


    public void setSemanticType(String semanticType) {
        this.semanticType = semanticType;
    }


    public boolean isBundle() {
        return (bundleType != null);
    }


    public String getBundleType() {
        return bundleType;
    }


    public void setBundleType(String bundleType) {
        this.bundleType = bundleType;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public List<String> getValues() {
        return values;
    }


    /**
     * Adds value to the list.
     * 
     * @param value
     *            value
     */
    public void addValue(String value) {
        if (value != null) {
            if (this.values == null) {
                this.values = new ArrayList<String>();
            }
            this.values.add(value);
        }
    }


    public boolean isRequired() {
        return required;
    }


    public void setRequired(boolean required) {
        this.required = required;
    }


    public boolean isRepeating() {
        return repeating;
    }


    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }


    public String getTechnicalType() {
        return technicalType;
    }


    public void setTechnicalType(String technicalType) {
        this.technicalType = technicalType;
    }


    public String getMimetype() {
        return mimetype;
    }


    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }

}
