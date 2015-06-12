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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pl.psnc.synat.wrdz.common.metadata.tech.FileType;
import pl.psnc.synat.wrdz.common.metadata.tech.TechMetadata;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;

/**
 * Director that manages the technical metadata creation.
 * 
 */
public class TechMetadataDirector {

    /**
     * FITS output. A seed for the construction of technical metadata.
     */
    private final FitsOutput fitsOutput;


    /**
     * Creates director for building technical metadata.
     * 
     * @param fitsOutput
     *            fits output
     */
    public TechMetadataDirector(FitsOutput fitsOutput) {
        this.fitsOutput = fitsOutput;
    }


    /**
     * Constructs a proper technical metadata object.
     * 
     * @return technical metadata object
     * @throws FitsException
     *             when construction of a XML with technical metadata failed
     */
    public List<TechMetadata> construct()
            throws FitsException {
        Map<String, XmlContent> xmlContents = fitsOutput.getStandardXmlContents();
        if (xmlContents.size() == 0) {
            return null;
        }
        List<TechMetadata> result = new ArrayList<TechMetadata>();
        FileType fileType = new FileTypeBuilder(fitsOutput).build();
        switch (fileType) {
            case TEXT:
                result.add(new TextMdMetadataBuilder(xmlContents.get("text")).build());
                break;
            case DOCUMENT:
                result.add(new DocumentMdMetadataBuilder(xmlContents.get("document")).build());
                break;
            case IMAGE:
                result.add(new MixMetadataBuilder(xmlContents.get("image")).build());
                break;
            case AUDIO:
                result.add(new AesMetadataBuilder(xmlContents.get("audio")).build());
                break;
            case VIDEO:
                result.add(new VideoMdMetadataBuilder(xmlContents.get("video")).build());
                if (xmlContents.size() > 1) {
                    result.add(new AesMetadataBuilder(xmlContents.get("audio")).build());
                }
                break;
            default:
                return null;
        }
        return result;
    }

}
