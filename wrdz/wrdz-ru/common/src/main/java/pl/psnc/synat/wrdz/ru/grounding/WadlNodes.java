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
package pl.psnc.synat.wrdz.ru.grounding;

import java.io.Serializable;
import java.util.Map;

/**
 * DTO for identifiers of nodes in WADL description of the service.
 */
public class WadlNodes implements Serializable {

    /** Serial Version UID. */
    private static final long serialVersionUID = -882185036566438286L;

    /** URI of the WADL document. */
    private String wadlUri;

    /** Id of a node in WADL file with the resource of the service (address). */
    private String resource;

    /** Id of a node in WADL file with the method of the service. */
    private String method;

    /** Map of pairs: node ID in WADL file and name of parameter in OWL-S. */
    private Map<String, String> parameters;

    /** Map of pairs: node ID in WADL file and name of outcome in OWL-S. */
    private Map<String, String> outcomes;


    public String getWadlUri() {
        return wadlUri;
    }


    public void setWadlUri(String wadlUri) {
        this.wadlUri = wadlUri;
    }


    public String getResource() {
        return resource;
    }


    public void setResource(String resource) {
        this.resource = resource;
    }


    public String getMethod() {
        return method;
    }


    public void setMethod(String method) {
        this.method = method;
    }


    public Map<String, String> getParameters() {
        return parameters;
    }


    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }


    public Map<String, String> getOutcomes() {
        return outcomes;
    }


    public void setOutcomes(Map<String, String> outcomes) {
        this.outcomes = outcomes;
    }

}
