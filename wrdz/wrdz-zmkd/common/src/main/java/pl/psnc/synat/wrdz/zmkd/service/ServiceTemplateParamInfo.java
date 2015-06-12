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

/**
 * Detailed information about a particular template parameter of the transformation.
 */
public class ServiceTemplateParamInfo {

    /** Semantic name of parameter. */
    private String semanticName;

    /** Semantic type of parameter - IRI - bundle is not allowed. */
    private String semanticType;

    /** Name of parameter (in WADL). */
    private String name;

    /** Value - string representations of value of the technicalType type. */
    private String value;

    /**
     * URI of the technical type, for instance: http://www.w3.org/2001/XMLSchema#int.
     */
    private String technicalType;


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


    /**
     * Template parameter cannot be bundle.
     * 
     * @return false
     */
    public boolean isBundle() {
        return false;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
    }


    public String getTechnicalType() {
        return technicalType;
    }


    public void setTechnicalType(String technicalType) {
        this.technicalType = technicalType;
    }


    /**
     * Parameter is always required.
     * 
     * @return true
     */
    public boolean isRequired() {
        return true;
    }

}
