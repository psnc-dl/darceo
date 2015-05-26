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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Detailed information about a particular body parameter of the transformation.
 */
public class ServiceBodyParamInfo {

    /** Semantic name of parameter. */
    private String semanticName;

    /** Semantic type of parameter - IRI. */
    private String semanticType;

    /** Semantic type of items in case of a bundle type. */
    private String bundleType;

    /**
     * Map of names of parameters in WADL files and set of URI of the technical types, for instance:
     * http://www.w3.org/2001/XMLSchema#int (only in case of value as string). Many values means options. The same names
     * can occur in different representations, so each name can have many alternative technical type.
     */
    private Map<String, Set<String>> technicalTypes;

    /**
     * Map of id of parameters (representations) in WADL files and mimetypes for them (in case of files). It can be
     * bundle in this case but only as a ZIP file). Many items means options, like for instance "application/zip" or
     * "image/bmp".
     */
    private Map<String, String> mimetypes;

    /**
     * Value of parameter - string representations of value of the technicalType type. For file it is not supported.
     */
    private String value;


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


    public Map<String, Set<String>> getTechnicalTypes() {
        return technicalTypes;
    }


    /**
     * Adds technical types to the list.
     * 
     * @param name
     *            name of parameter
     * @param technicalType
     *            technical type
     */
    public void addTechnicalType(String name, String technicalType) {
        if (this.technicalTypes == null) {
            this.technicalTypes = new HashMap<String, Set<String>>();
        }
        Set<String> types = this.technicalTypes.get(name);
        if (types != null) {
            types.add(technicalType);
        } else {
            types = new HashSet<String>();
            types.add(technicalType);
            this.technicalTypes.put(name, types);
        }
    }


    public Map<String, String> getMimetypes() {
        return mimetypes;
    }


    /**
     * Adds mimetype to the list.
     * 
     * @param id
     *            id of parameter (representation in WADL)
     * @param mimetype
     *            mimetype
     */
    public void addMimetype(String id, String mimetype) {
        if (this.mimetypes == null) {
            this.mimetypes = new HashMap<String, String>();
        }

        this.mimetypes.put(id, mimetype);
    }


    public String getValue() {
        return value;
    }


    public void setValue(String value) {
        this.value = value;
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
