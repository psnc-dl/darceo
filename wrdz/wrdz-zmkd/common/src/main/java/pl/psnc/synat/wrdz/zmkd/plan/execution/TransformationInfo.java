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
package pl.psnc.synat.wrdz.zmkd.plan.execution;

import pl.psnc.synat.wrdz.ru.composition.TransformationType;
import pl.psnc.synat.wrdz.zmkd.entity.format.FileFormat;
import pl.psnc.synat.wrdz.zmkd.service.ServiceInfo;

/**
 * Detailed information about a particular transformation.
 */
public class TransformationInfo extends ServiceInfo {

    /** Type of transformation. */
    private TransformationType type;

    /** Input file format. */
    private FileFormat inputFileFormat;

    /** Output file format. */
    private FileFormat outputFileFormat;


    public TransformationType getType() {
        return type;
    }


    public void setType(TransformationType type) {
        this.type = type;
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

}
