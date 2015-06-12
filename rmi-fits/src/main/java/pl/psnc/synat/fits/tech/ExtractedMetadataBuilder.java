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

import pl.psnc.synat.wrdz.common.metadata.tech.ExtractedMetadata;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsException;

/**
 * Builder of an extracted metadata object based upon an output returned by FITS.
 * 
 */
public class ExtractedMetadataBuilder {

    /**
     * FITS output. A seed for the construction of extracted metadata.
     */
    private final FitsOutput fitsOutput;


    /**
     * Constructs extracted metadata builder.
     * 
     * @param fitsOutput
     *            seed
     */
    public ExtractedMetadataBuilder(FitsOutput fitsOutput) {
        this.fitsOutput = fitsOutput;
    }


    /**
     * Creates an object <code>ExtractedMetadata</code> based upon output returned by FITS.
     * 
     * @return extracted metadata object
     * @throws FitsException
     *             when construction of an extracted metadata object failed
     */
    public ExtractedMetadata build()
            throws FitsException {
        ExtractedMetadata extractedMetadata = new ExtractedMetadata();
        extractedMetadata.setFileFormat(new FileFormatBuilder(fitsOutput).build());
        extractedMetadata.setFileStatus(new FileStatusBuilder(fitsOutput).build());
        extractedMetadata.setTechMetadata(new TechMetadataDirector(fitsOutput).construct());
        extractedMetadata.setAdditionalMetadata(new AdditionalExtractedMetadataBuilder(fitsOutput).build());
        return extractedMetadata;
    }

}
