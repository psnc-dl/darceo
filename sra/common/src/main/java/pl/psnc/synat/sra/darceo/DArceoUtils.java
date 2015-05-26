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
package pl.psnc.synat.sra.darceo;

import pl.psnc.synat.sra.util.SemanticDataUtils;

/**
 * Provides static utility methods for dArceo semantic data.
 * 
 */
public final class DArceoUtils {

    /**
     * Private constructor.
     */
    private DArceoUtils() {
    }


    /**
     * Prefix for OWL-S Process namespace.
     */
    public static final String PREFIX_PROCESS = "process";

    /**
     * Prefix for OWL-S Profile namespace.
     */
    public static final String PREFIX_PROFILE = "profile";

    /**
     * Prefix for OWL-S Service namespace.
     */
    public static final String PREFIX_SERVICE = "service";

    /**
     * Prefix for UDFR ontology namespace.
     */
    public static final String PREFIX_UDFRS = "udfrs";

    /**
     * Prefix for dArceo File namespace.
     */
    public static final String PREFIX_DARCEO_FILE = "dArceoFile";

    /**
     * Prefix for OWL-S dArceo Process namespace.
     */
    public static final String PREFIX_DARCEO_PROCESS = "dArceoProcess";

    /**
     * Prefix for OWL-S dArceo Service namespace.
     */
    public static final String PREFIX_DARCEO_SERVICE = "dArceoService";

    /**
     * OWL-S Process namespace.
     */
    public static final String NAMESPACE_PROCESS = "http://www.daml.org/services/owl-s/1.2/Process.owl#";

    /**
     * OWL-S Profile namespace.
     */
    public static final String NAMESPACE_PROFILE = "http://www.daml.org/services/owl-s/1.2/Profile.owl#";

    /**
     * OWL-S Service namespace.
     */
    public static final String NAMESPACE_SERVICE = "http://www.daml.org/services/owl-s/1.2/Service.owl#";

    /**
     * dArceo File namespace.
     */
    public static final String NAMESPACE_DARCEO_FILE = "http://darceo.psnc.pl/ontologies/dArceoFile.owl#";

    /**
     * UDFR ontology namespace.
     */
    public static final String NAMESPACE_UDFRS = "http://www.udfr.org/onto#";

    /**
     * OWL-S dArceo Process namespace.
     */
    public static final String NAMESPACE_DARCEO_PROCESS = "http://darceo.psnc.pl/ontologies/dArceoProcess.owl#";

    /**
     * OWL-S dArceo Service namespace.
     */
    public static final String NAMESPACE_DARCEO_SERVICE = "http://darceo.psnc.pl/ontologies/dArceoService.owl#";

    /**
     * URI of the OWL-S process input parameter.
     */
    public static final String PROCESS_INPUT_PARAMETER = NAMESPACE_PROCESS + "Input";

    /**
     * URI of the OWL-S process local parameter.
     */
    public static final String PROCESS_LOCAL_PARAMETER = NAMESPACE_PROCESS + "Local";

    /**
     * URI of the OWL-S process input parameter.
     */
    public static final String PROCESS_OUTPUT_PARAMETER = NAMESPACE_PROCESS + "Output";

    /**
     * URI of the dArceo service file transformation.
     */
    public static final String DARCEO_SERVICE_FILE_TRANSFORMATION = NAMESPACE_DARCEO_SERVICE + "FileTransformation";

    /**
     * URI of a input of the file transformation predicate.
     */
    public static final String PREDICATE_FILE_TRANSFORMATION_INPUT = NAMESPACE_DARCEO_SERVICE + "fileIn";

    /**
     * URI of a output of the file transformation predicate.
     */
    public static final String PREDICATE_FILE_TRANSFORMATION_OUTPUT = NAMESPACE_DARCEO_SERVICE + "fileOut";

    /**
     * URI of the has file transformation predicate.
     */
    public static final String PREDICATE_HAS_FILE_TRANSFORMATION = NAMESPACE_DARCEO_SERVICE + "hasFileTransformation";

    /**
     * URI of the performed by predicate.
     */
    public static final String PREDICATE_PERFORMED_BY_SERVICE = NAMESPACE_DARCEO_SERVICE + "performedBy";


    /**
     * Returns string builder with default prefixes for dArceo ontology for SPARQL query.
     * 
     * @return default prefixes for for dArceo ontology SPARQL query
     */
    public static StringBuilder getSparqlDefaultPrefixes() {
        StringBuilder sb = SemanticDataUtils.getSparqlDefaultPrefixes();
        sb.append(SemanticDataUtils.PREFIX).append(' ').append(PREFIX_UDFRS).append(": ").append('<')
                .append(NAMESPACE_UDFRS).append('>').append(' ');
        sb.append(SemanticDataUtils.PREFIX).append(' ').append(PREFIX_DARCEO_FILE).append(": ").append('<')
                .append(NAMESPACE_DARCEO_FILE).append('>').append(' ');
        sb.append(SemanticDataUtils.PREFIX).append(' ').append(PREFIX_PROCESS).append(": ").append('<')
                .append(NAMESPACE_PROCESS).append('>').append(' ');
        sb.append(SemanticDataUtils.PREFIX).append(' ').append(PREFIX_SERVICE).append(": ").append('<')
                .append(NAMESPACE_SERVICE).append('>').append(' ');
        sb.append(SemanticDataUtils.PREFIX).append(' ').append(PREFIX_DARCEO_PROCESS).append(": ").append('<')
                .append(NAMESPACE_DARCEO_PROCESS).append('>').append(' ');
        sb.append(SemanticDataUtils.PREFIX).append(' ').append(PREFIX_DARCEO_SERVICE).append(": ").append('<')
                .append(NAMESPACE_DARCEO_SERVICE).append('>').append(' ');
        return sb;
    }

}
