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
 * Detailed information about a particular transformation outcome.
 */
public class ServiceOutcomeInfo {

    /** Semantic name of outcome. */
    private String semanticName;

    /** Semantic type of outcome - IRI. */
    private String semanticType;

    /** Semantic type of items in case of a bundle type. */
    private String bundleType;

    /** Name of parameter (in WADL) - in case of outcome in a header. */
    private String name;

    /** Whether this parameter is required. */
    private boolean required;

    /** Whether the value of this parameter is a list. */
    private boolean repeating;

    /**
     * Map of names of outcomes in WADL files and set of URI of the technical types, for instance:
     * http://www.w3.org/2001/XMLSchema#int (only in case of value as string). Many values means options. The same names
     * can occur in different representations, so each name can have many alternative technical type.
     */
    private Map<String, Set<String>> technicalTypes;

    /**
     * Map of id of outcomes (representations)in WADL files and mimetypes for them (in case of files). It can be bundle
     * in this case but only as a ZIP file). Many items means options, like for instance "application/zip" or
     * "image/bmp".
     */
    private Map<String, String> mimetypes;

    /** Outcome style. */
    private OutcomeStyle style;

    /** Set of status codes of from WADL. */
    private Set<Integer> statuses;


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


    public Map<String, Set<String>> getTechnicalTypes() {
        return technicalTypes;
    }


    /**
     * Adds technical types to the list.
     * 
     * @param name
     *            name of outcome
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
     *            id of outcome (representation in WADL)
     * @param mimetype
     *            mimetype
     */
    public void addMimetype(String id, String mimetype) {
        if (this.mimetypes == null) {
            this.mimetypes = new HashMap<String, String>();
        }

        this.mimetypes.put(id, mimetype);
    }


    public OutcomeStyle getStyle() {
        return style;
    }


    public void setStyle(OutcomeStyle style) {
        this.style = style;
    }


    public Set<Integer> getStatuses() {
        return statuses;
    }


    public void setStatuses(Set<Integer> statuses) {
        this.statuses = statuses;
    }

}
