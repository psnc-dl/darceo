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

import pl.psnc.synat.wrdz.common.metadata.tech.FileType;
import edu.harvard.hul.ois.fits.FitsOutput;

/**
 * Builder of a file type based upon a part of output returned by FITS.
 * 
 */
public class FileTypeBuilder {

    /**
     * FITS output. A seed for the construction of the file status.
     */
    private final FitsOutput fitsOutput;


    /**
     * Constructs a file type builder.
     * 
     * @param fitsOutput
     *            seed
     */
    public FileTypeBuilder(FitsOutput fitsOutput) {
        this.fitsOutput = fitsOutput;
    }


    /**
     * Creates an object <code>FileType</code> based upon output returned by FITS.
     * 
     * @return file format
     */
    public FileType build() {
        try {
            return FileType.valueOf(fitsOutput.getTechMetadataType().toUpperCase());
        } catch (IllegalArgumentException e) {
            return FileType.UNKNOWN;
        } catch (NullPointerException e) {
            return FileType.UNKNOWN;
        }
    }

}
