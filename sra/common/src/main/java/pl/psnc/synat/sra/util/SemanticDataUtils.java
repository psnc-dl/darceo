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
package pl.psnc.synat.sra.util;

/**
 * Provides static utility methods for semantic data.
 * 
 */
public final class SemanticDataUtils {

    /**
     * Private constructor.
     */
    private SemanticDataUtils() {
    }


    /**
     * PREFIX constant.
     */
    public static final String PREFIX = "PREFIX";

    /**
     * SELECT constant.
     */
    public static final String SELECT = "SELECT";

    /**
     * ASK constant.
     */
    public static final String ASK = "ASK";

    /**
     * INSERT DATA constant.
     */
    public static final String INSERT_DATA = "INSERT DATA";

    /**
     * FROM constant.
     */
    public static final String FROM = "FROM";

    /**
     * GRAPH constant.
     */
    public static final String GRAPH = "GRAPH";

    /**
     * WHERE constant.
     */
    public static final String WHERE = "WHERE";

    /**
     * Prefix for XSD namespace.
     */
    public static final String PREFIX_XSD = "xsd";

    /**
     * Prefix for RDF namespace.
     */
    public static final String PREFIX_RDF = "rdf";

    /**
     * Prefix for RDFS namespace.
     */
    public static final String PREFIX_RDFS = "rdfs";

    /**
     * Prefix for OWL namespace.
     */
    public static final String PREFIX_OWL = "owl";

    /**
     * XSD namespace.
     */
    public static final String NAMESPACE_XSD = "http://www.w3.org/2001/XMLSchema#";

    /**
     * RDF namespace.
     */
    public static final String NAMESPACE_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    /**
     * RDFS namespace.
     */
    public static final String NAMESPACE_RDFS = "http://www.w3.org/2000/01/rdf-schema#";

    /**
     * OWL namespace.
     */
    public static final String NAMESPACE_OWL = "http://www.w3.org/2002/07/owl#";

    /**
     * URI of the RDF type predicate.
     */
    public static final String PREDICATE_RDF_TYPE = NAMESPACE_RDF + "type";


    /**
     * Returns string builder with default prefixes for SPARQL query.
     * 
     * @return default prefixes for SPARQL query
     */
    public static StringBuilder getSparqlDefaultPrefixes() {
        StringBuilder sb = new StringBuilder();
        sb.append(SemanticDataUtils.PREFIX).append(' ').append(SemanticDataUtils.PREFIX_XSD).append(": ").append('<')
                .append(SemanticDataUtils.NAMESPACE_XSD).append('>').append(' ');
        sb.append(SemanticDataUtils.PREFIX).append(' ').append(SemanticDataUtils.PREFIX_RDF).append(": ").append('<')
                .append(SemanticDataUtils.NAMESPACE_RDF).append('>').append(' ');
        sb.append(SemanticDataUtils.PREFIX).append(' ').append(SemanticDataUtils.PREFIX_RDFS).append(": ").append('<')
                .append(SemanticDataUtils.NAMESPACE_RDFS).append('>').append(' ');
        sb.append(SemanticDataUtils.PREFIX).append(' ').append(SemanticDataUtils.PREFIX_OWL).append(": ").append('<')
                .append(SemanticDataUtils.NAMESPACE_OWL).append('>').append(' ');
        return sb;
    }


    /**
     * Constructs the URI for instance by the URI of class.
     * 
     * @param uriClass
     *            URI of a class
     * @return URI for an instance
     */
    public static String classToInstance(String uriClass) {
        int sepidx = uriClass.indexOf('#');
        if (sepidx == -1) {
            sepidx = uriClass.lastIndexOf('/');
        }
        if (sepidx == -1) {
            sepidx = uriClass.lastIndexOf(':');
        }
        if (sepidx == -1) {
            throw new IllegalArgumentException("No separator character founds in URI: " + uriClass);
        }
        sepidx++;
        String className = uriClass.substring(sepidx);
        if (className.length() > 0) {
            className = className.substring(0, 1).toLowerCase().concat(className.substring(1));
        }
        return uriClass.substring(0, sepidx).concat(className).concat("Instance");
    }

}
