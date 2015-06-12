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
package pl.psnc.synat.fits.tech;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.psnc.synat.wrdz.common.metadata.tech.FileStatus;
import pl.psnc.synat.wrdz.common.metadata.tech.FileValidationStatus;
import edu.harvard.hul.ois.fits.FitsMetadataElement;
import edu.harvard.hul.ois.fits.FitsOutput;

/**
 * Builder of a file status based upon a part of output returned by FITS.
 */
public class FileStatusBuilder {

    /**
     * FITS output. A seed for the construction of the file status.
     */
    private final FitsOutput fitsOutput;


    /**
     * Constructs a file format builder.
     * 
     * @param fitsOutput
     *            seed
     */
    public FileStatusBuilder(FitsOutput fitsOutput) {
        this.fitsOutput = fitsOutput;
    }


    /**
     * Creates an object <code>FileStatus</code> based upon output returned by FITS.
     * 
     * @return file format
     */
    public FileStatus build() {
        FileStatus fileStatus = new FileStatus();
        fileStatus.setStatus(FileValidationStatus.UNKNOWN);
        Set<String> warnings = new HashSet<String>();
        List<FitsMetadataElement> fileStatusElements = fitsOutput.getFileStatusElements();
        for (FitsMetadataElement fitsMetadataElement : fileStatusElements) {
            if (fitsMetadataElement.getName().equals("well-formed")) {
                if (fitsMetadataElement.getValue().equals("true")) {
                    if (fileStatus.getStatus().equals(FileValidationStatus.UNKNOWN)) {
                        fileStatus.setStatus(FileValidationStatus.WELL_FORMED);
                    }
                } else {
                    fileStatus.setStatus(FileValidationStatus.MALFORMED);
                }
            } else if (fitsMetadataElement.getName().equals("valid")) {
                if (fitsMetadataElement.getValue().equals("true")) {
                    if (fileStatus.getStatus().equals(FileValidationStatus.UNKNOWN)) {
                        fileStatus.setStatus(FileValidationStatus.WELL_FORMED);
                    }
                } else {
                    fileStatus.setStatus(FileValidationStatus.MALFORMED);
                }
            } else if (fitsMetadataElement.getName().equals("message")) {
                warnings.add(fitsMetadataElement.getValue());
            }
        }
        if (warnings.size() > 0) {
            fileStatus.setWarnings(warnings);
        }
        return fileStatus;
    }

}
