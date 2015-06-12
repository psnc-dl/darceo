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
package pl.psnc.synat.wrdz.zmkd.entity.plan;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import pl.psnc.synat.wrdz.ru.composition.TransformationClassification;
import pl.psnc.synat.wrdz.ru.composition.TransformationType;
import pl.psnc.synat.wrdz.zmkd.entity.format.FileFormat;

/**
 * Transformation (a service call that transforms from one file format to another).
 */
@Entity(name = "ZmkdTransformation")
@DiscriminatorValue("TRANSFORMATION")
public class Transformation extends Service {

    /** Serial version UID. */
    private static final long serialVersionUID = -1180556460710295054L;

    /**
     * Migration path for this transformation.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "T_TRANSFORMATION_PATH_ID", insertable = false, updatable = false, nullable = false)
    private TransformationPath transformationPath;

    /**
     * Input file format.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "T_INPUT_FILE_FORMAT_ID", nullable = true)
    private FileFormat inputFileFormat;

    /**
     * Output file format.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "T_OUTPUT_FILE_FORMAT_ID", nullable = true)
    private FileFormat outputFileFormat;

    /**
     * Type of transformation.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "T_TYPE", length = 20, nullable = false)
    private TransformationType type;

    /**
     * Service classification.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "T_CLASS", length = 20, nullable = false)
    private TransformationClassification classification;


    public TransformationPath getTransformationPath() {
        return transformationPath;
    }


    public void setTransformationPath(TransformationPath transformationPath) {
        this.transformationPath = transformationPath;
    }


    public FileFormat getInputFileFormat() {
        return inputFileFormat;
    }


    public void setInputFileFormat(FileFormat inputFileFormat) {
        this.inputFileFormat = inputFileFormat;
    }


    public FileFormat getOutputFileFormat() {
        return outputFileFormat;
    }


    public void setOutputFileFormat(FileFormat outputFileFormat) {
        this.outputFileFormat = outputFileFormat;
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
