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
 * SPARQL updates concerning dArceo services.
 */
public final class DArceoUpdates {

    /**
     * Private constructor.
     */
    private DArceoUpdates() {
    }


    /**
     * Gets a SPARQL update which inserts triples describing one transformation of some service.
     * 
     * @param service
     *            service URI
     * @param transformation
     *            transformation URI
     * @param inpuid
     *            input PUID URI
     * @param outpuid
     *            output PUID URI
     * 
     * @return SPARQL update
     */
    public static String getFileTransformationUpdate(String service, String transformation, String inpuid,
            String outpuid) {
        StringBuilder sb = DArceoUtils.getSparqlDefaultPrefixes();
        sb.append(SemanticDataUtils.INSERT_DATA).append(" { ");
        sb.append("<").append(service).append("> ").append(DArceoUtils.PREFIX_DARCEO_SERVICE)
                .append(":hasFileTransformation").append(' ').append('<').append(transformation).append('>')
                .append(". ");
        sb.append("<").append(transformation).append("> ").append(DArceoUtils.PREFIX_DARCEO_SERVICE).append(":fileIn")
                .append(' ').append('<').append(inpuid).append('>').append("; ");
        sb.append(' ').append(DArceoUtils.PREFIX_DARCEO_SERVICE).append(":fileOut").append(' ').append('<')
                .append(outpuid).append('>').append("; ");
        sb.append("}\n");
        return sb.toString();
    }

}
