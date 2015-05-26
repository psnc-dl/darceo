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

import pl.psnc.synat.wrdz.common.metadata.tech.MixMetadata;
import pl.psnc.synat.wrdz.common.metadata.tech.TechMetadata;
import edu.harvard.hul.ois.fits.exceptions.FitsException;
import edu.harvard.hul.ois.ots.schemas.XmlContent.XmlContent;

/**
 * MIX metadata builder.
 * 
 */
public class MixMetadataBuilder extends TechMetadataBuilder {

    /**
     * Constructs MIX metadata builder.
     * 
     * @param xml
     *            seed
     */
    public MixMetadataBuilder(XmlContent xml) {
        super(xml);
    }


    @Override
    public TechMetadata build()
            throws FitsException {
        return new MixMetadata(constructStandardSchemaXml());
    }

}