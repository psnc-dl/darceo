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

import java.util.List;

import pl.psnc.synat.wrdz.common.metadata.tech.AdditionalExtractedMetadata;
import edu.harvard.hul.ois.fits.FitsMetadataElement;
import edu.harvard.hul.ois.fits.FitsOutput;

/**
 * Builder of additional extracted metadata based upon a part of output returned by FITS.
 * 
 */
public class AdditionalExtractedMetadataBuilder {

    /**
     * FITS output. A seed for the construction of the file format.
     */
    private final FitsOutput fitsOutput;


    /**
     * Constructs a file format builder.
     * 
     * @param fitsOutput
     *            seed
     */
    public AdditionalExtractedMetadataBuilder(FitsOutput fitsOutput) {
        this.fitsOutput = fitsOutput;
    }


    /**
     * Creates an object <code>AdditionalExtractedMetadata</code> based upon output returned by FITS.
     * 
     * @return additional extracted metadata
     */
    public AdditionalExtractedMetadata build() {
        AdditionalExtractedMetadata additionalExtractedMetadata = new AdditionalExtractedMetadata();
        List<FitsMetadataElement> fileInfoElements = fitsOutput.getFileInfoElements();
        for (FitsMetadataElement fitsMetadataElement : fileInfoElements) {
            if (fitsMetadataElement.getName().equalsIgnoreCase("creatingApplicationName")) {
                additionalExtractedMetadata.setCreatingApplicationName(fitsMetadataElement.getValue());
            } else if (fitsMetadataElement.getName().equalsIgnoreCase("creatingApplicationVersion")) {
                additionalExtractedMetadata.setCreatingApplicationVersion(fitsMetadataElement.getValue());
            } else if (fitsMetadataElement.getName().equalsIgnoreCase("created")) {
                additionalExtractedMetadata.setDateCreatedByApplication(fitsMetadataElement.getValue());
            }
        }
        return additionalExtractedMetadata;
    }

}
