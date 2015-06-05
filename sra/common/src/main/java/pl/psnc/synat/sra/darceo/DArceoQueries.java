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
 * SPARQL queries required to infer some additional triples for describing dArceo services.
 */
public final class DArceoQueries {

    /**
     * Private constructor.
     */
    private DArceoQueries() {
    }


    /**
     * Gets the main SPARQL select query which returns input PUIDs and the class of output.
     * 
     * @param context
     *            context of triples
     * @param service
     *            name of a variable for service
     * @param inpuid
     *            name of a variable for input PUIDs
     * @param outclass
     *            name of a variable for output class
     * @param infilecond
     *            name of a variable for input file condition
     * @param inoutfmtcondition
     *            name of a variable for output format condition
     * @return SPARQL query
     */
    public static String getInOutPuidQuery(String context, String service, String inpuid, String outclass,
            String infilecond, String inoutfmtcondition) {
        StringBuilder sb = DArceoUtils.getSparqlDefaultPrefixes();
        sb.append(SemanticDataUtils.SELECT).append(" ?").append(service).append(" ?").append(inpuid).append(" ?")
                .append(outclass).append(" ?").append(infilecond).append(" ?").append(inoutfmtcondition).append(' ');
        sb.append(SemanticDataUtils.FROM).append(" <").append(context).append(">").append(' ');
        sb.append(SemanticDataUtils.WHERE).append(" { ");
        sb.append('?').append(service).append(' ').append(SemanticDataUtils.PREFIX_RDF).append(":type").append(' ')
                .append(DArceoUtils.PREFIX_SERVICE).append(":Service").append("; ");
        sb.append(DArceoUtils.PREFIX_SERVICE).append(":describedBy").append(' ').append("?process").append(". ");
        sb.append("?process").append(' ').append(SemanticDataUtils.PREFIX_RDF).append(":type").append(' ')
                .append(DArceoUtils.PREFIX_PROCESS).append(":AtomicProcess").append("; ");
        sb.append(DArceoUtils.PREFIX_PROCESS).append(":hasResult").append(' ').append("?result").append(". ");
        sb.append("?result").append(' ').append(DArceoUtils.PREFIX_PROCESS).append(":inCondition").append(' ')
                .append("?condition").append("; ");
        sb.append(DArceoUtils.PREFIX_PROCESS).append(":withOutput").append(' ').append("?binding").append(". ");
        sb.append("?condition").append(' ').append(DArceoUtils.PREFIX_DARCEO_PROCESS).append(":inputFileCondition")
                .append(' ').append('?').append(infilecond).append(". ");
        sb.append(" OPTIONAL { ").append("?condition").append(' ').append(DArceoUtils.PREFIX_DARCEO_PROCESS)
                .append(":inputOutputFormatCondition").append(' ').append('?').append(inoutfmtcondition)
                .append(" } . ");
        sb.append('?').append(infilecond).append(' ').append(DArceoUtils.PREFIX_DARCEO_PROCESS)
                .append(":inputFileFormat").append(' ').append('?').append(inpuid).append(". ");
        sb.append("?binding").append(' ').append(DArceoUtils.PREFIX_PROCESS).append(":valueType").append(' ')
                .append('?').append(outclass).append(". ");
        sb.append("}\n");
        return sb.toString();
    }


    /**
     * Gets a SPARQL select query which returns file format from the class of files (by the inferred property of an
     * instances of the class).
     * 
     * @param dArceoFileInstance
     *            singleton instance of class of files
     * @param dArceoFileClass
     *            class of file
     * @param outfmt
     *            name of a variable for the output format or variable of the output format
     * @return SPARQL query
     */
    public static String getFileFormatQuery(String dArceoFileInstance, String dArceoFileClass, String outfmt) {
        StringBuilder sb = DArceoUtils.getSparqlDefaultPrefixes();
        sb.append(SemanticDataUtils.SELECT).append(" ?").append(outfmt).append(' ');
        sb.append(SemanticDataUtils.WHERE).append(" { ");
        sb.append("<").append(dArceoFileInstance).append("> ").append(SemanticDataUtils.PREFIX_RDF).append(":type")
                .append(' ').append("<").append(dArceoFileClass).append(">; ");
        sb.append(DArceoUtils.PREFIX_DARCEO_FILE).append(":fileFormat").append(' ').append('?').append(outfmt)
                .append(". ");
        sb.append("}\n");
        return sb.toString();
    }


    /**
     * Gets a SPARQL select query which returns file format from the class of files (by the direct query of the class).
     * 
     * @param dArceoFileClass
     *            class of file
     * @param outfmt
     *            name of a variable for the output format or variable of the output format
     * @return SPARQL query
     */
    public static String getFileFormatQuery(String dArceoFileClass, String outfmt) {
        StringBuilder sb = DArceoUtils.getSparqlDefaultPrefixes();
        sb.append(SemanticDataUtils.SELECT).append(" ?").append(outfmt).append(' ');
        sb.append(SemanticDataUtils.WHERE).append(" { ");
        sb.append("<").append(dArceoFileClass).append("> ").append(SemanticDataUtils.PREFIX_RDFS).append(":subClassOf")
                .append(" [ ");
        sb.append(SemanticDataUtils.PREFIX_OWL).append(":onProperty").append(" ")
                .append(DArceoUtils.PREFIX_DARCEO_FILE).append(":fileFormat").append(" ; ")
                .append(SemanticDataUtils.PREFIX_OWL).append(":hasValue").append(" ?").append(outfmt).append("]. ");
        sb.append("}\n");
        return sb.toString();
    }


    /**
     * Gets a SPARQL select query which returns PUID assigned to local parameter of a process.
     * 
     * @param infilecondIndividual
     *            dArceo InputFileCondition individual
     * @param localparamIndividual
     *            Local Param individual of the output format
     * @param outpuid
     *            name of a variable for the output PUID format
     * @return SPARQL query
     */
    public static String getOutputPuidFromLocalQuery(String infilecondIndividual, String localparamIndividual,
            String outpuid) {
        StringBuilder sb = DArceoUtils.getSparqlDefaultPrefixes();
        sb.append(SemanticDataUtils.SELECT).append(" ?").append(outpuid).append(' ');
        sb.append(SemanticDataUtils.WHERE).append(" { ");
        sb.append("<").append(infilecondIndividual).append("> ").append(DArceoUtils.PREFIX_DARCEO_PROCESS)
                .append(":toLocalParam").append(' ').append('<').append(localparamIndividual).append("> ").append("; ");
        sb.append(DArceoUtils.PREFIX_DARCEO_PROCESS).append(":inputFileFormat").append(' ').append('?').append(outpuid)
                .append(". ");
        sb.append("}\n");
        return sb.toString();
    }


    /**
     * Gets a SPARQL select query which returns PUID assigned to input parameter of a process.
     * 
     * @param inoutfmtcondIndividual
     *            dArceo InputOutputFormatCondition individual
     * @param inputparamIndividual
     *            Input Param individual of the output format
     * @param outpuid
     *            name of a variable for the output PUID format
     * @return SPARQL query
     */
    public static String getOutputPuidFromInputQuery(String inoutfmtcondIndividual, String inputparamIndividual,
            String outpuid) {
        StringBuilder sb = DArceoUtils.getSparqlDefaultPrefixes();
        sb.append(SemanticDataUtils.SELECT).append(" ?").append(outpuid).append(' ');
        sb.append(SemanticDataUtils.WHERE).append(" { ");
        sb.append("<").append(inoutfmtcondIndividual).append("> ").append(DArceoUtils.PREFIX_DARCEO_PROCESS)
                .append(":inputParam").append(' ').append('<').append(inputparamIndividual).append("> ").append("; ");
        sb.append(DArceoUtils.PREFIX_DARCEO_PROCESS).append(":outputFormat").append(' ').append('?').append(outpuid)
                .append(". ");
        sb.append("}\n");
        return sb.toString();
    }


    /**
     * Gets a SPARQL select query which returns all transformations performed by services - also complex ones.
     * 
     * @param service
     *            name of a variable for service
     * @param transformation
     *            name of a variable for transformation
     * @param inpuid
     *            name of a variable for input PUID
     * @param outpuid
     *            name of a variable for output PUID
     * @param previous
     *            name of a variable for previous transformation
     * @param subsequent
     *            name of a variable for subsequent transformation
     * @return SPARQL query
     */
    public static String getAllTransformationsQuery(String service, String transformation, String inpuid,
            String outpuid, String previous, String subsequent) {
        StringBuilder sb = DArceoUtils.getSparqlDefaultPrefixes();
        sb.append(SemanticDataUtils.SELECT).append(" ?").append(service).append(" ?").append(transformation)
                .append(" ?").append(inpuid).append(" ?").append(outpuid).append(" ?").append(previous).append(" ?")
                .append(subsequent).append(' ');
        sb.append(SemanticDataUtils.WHERE).append(" { ");
        sb.append(" ?").append(transformation).append(' ').append(SemanticDataUtils.PREFIX_RDF).append(":type")
                .append(' ').append('<').append(DArceoUtils.DARCEO_SERVICE_FILE_TRANSFORMATION).append('>')
                .append(". ");
        sb.append("OPTIONAL { ?").append(transformation).append(' ').append(DArceoUtils.PREFIX_DARCEO_SERVICE)
                .append(":fileIn").append(' ').append('?').append(inpuid).append(" } . ");
        sb.append("OPTIONAL { ?").append(transformation).append(' ').append(DArceoUtils.PREFIX_DARCEO_SERVICE)
                .append(":fileOut").append(' ').append('?').append(outpuid).append(" } . ");
        sb.append("OPTIONAL { ?").append(transformation).append(' ').append(DArceoUtils.PREFIX_DARCEO_SERVICE)
                .append(":performedBy").append(' ').append('?').append(service).append(" } . ");
        sb.append("OPTIONAL { ?").append(transformation).append(' ').append(DArceoUtils.PREFIX_DARCEO_SERVICE)
                .append(":previousTransformation").append(' ').append('?').append(previous).append(" } . ");
        sb.append("OPTIONAL { ?").append(transformation).append(' ').append(DArceoUtils.PREFIX_DARCEO_SERVICE)
                .append(":subsequentTransformation").append(' ').append('?').append(subsequent).append(" } . ");
        sb.append("}\n");
        return sb.toString();
    }

}
