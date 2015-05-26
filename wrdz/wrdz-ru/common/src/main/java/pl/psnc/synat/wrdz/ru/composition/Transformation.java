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
package pl.psnc.synat.wrdz.ru.composition;


/**
 * DTO for transferring an atomic transformation and a service that performs this transformation, also in user friendly
 * names.
 */
public class Transformation extends AbstractService {

    /** Serial Version UID. */
    private static final long serialVersionUID = 125561819652013125L;

    /**
     * Input format (IRI) of a transformation.
     */
    private String inputFormatIri;

    /**
     * Output format (IRI) of a transformation.
     */
    private String outputFormatIri;

    /**
     * Transformation type.
     */
    private TransformationType type;

    /**
     * Transformation classification.
     */
    private TransformationClassification classification;


    public String getInputFormatIri() {
        return inputFormatIri;
    }


    public void setInputFormatIri(String inputFormatIri) {
        this.inputFormatIri = inputFormatIri;
    }


    public String getOutputFormatIri() {
        return outputFormatIri;
    }


    public void setOutputFormatIri(String outputFormatIri) {
        this.outputFormatIri = outputFormatIri;
    }


    public TransformationType getType() {
        return type;
    }


    public void setType(TransformationType type) {
        this.type = type;
    }


    public TransformationClassification getClassification() {
        return classification;
    }


    public void setClassification(TransformationClassification classification) {
        this.classification = classification;
    }

}
