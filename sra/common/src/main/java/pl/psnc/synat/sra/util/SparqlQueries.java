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
 * Predefined SPARQL queries.
 */
public final class SparqlQueries {

    /**
     * Private constructor.
     */
    private SparqlQueries() {
    }


    /**
     * Gets a SPARQL select query which can return RDF type for a given individual.
     * 
     * @param individual
     *            individual
     * @param type
     *            name of a variable for RDF type
     * @return SPARQL query
     */
    public static String getRdfTypeQuery(String individual, String type) {
        StringBuilder sb = SemanticDataUtils.getSparqlDefaultPrefixes();
        sb.append(SemanticDataUtils.SELECT).append(" ?").append(type).append(' ');
        sb.append(SemanticDataUtils.WHERE).append(" { ");
        sb.append("<").append(individual).append("> ").append(SemanticDataUtils.PREFIX_RDF).append(":type").append(' ')
                .append('?').append(type).append(". ");
        sb.append("}\n");
        return sb.toString();
    }


    /**
     * Gets a SPARQL ask query which can confirm whether a given individual is a given type.
     * 
     * @param individual
     *            individual
     * @param type
     *            RDF type
     * @return SPARQL query ask
     */
    public static String getRdfTypeQueryAsk(String individual, String type) {
        StringBuilder sb = SemanticDataUtils.getSparqlDefaultPrefixes();
        sb.append(SemanticDataUtils.ASK).append(" { ");
        sb.append("<").append(individual).append("> ").append(SemanticDataUtils.PREFIX_RDF).append(":type").append(' ')
                .append('<').append(type).append(">. ");
        sb.append("}\n");
        return sb.toString();
    }

}
