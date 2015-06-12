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
package pl.psnc.synat.wrdz.zmd.object.metadata.construction;

import java.util.Date;

import pl.psnc.synat.wrdz.common.metadata.adm.AdmMetadata;
import pl.psnc.synat.wrdz.common.metadata.adm.AdmMetadataProcessingException;
import pl.psnc.synat.wrdz.common.metadata.tech.ExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;

/**
 * Interface for administrative metadata construction strategy.
 */
public interface AdministrativeMetadataConstructionStrategy {

    /**
     * Constructs administrative metadata for the file with the usage of its extracted metadata.
     * 
     * @param file
     *            file for which administrative metadata are built
     * @param extractedMetadata
     *            metadata extracted from teh file
     * @param creationDate
     *            date of creation
     * @param maxValidationMessages
     *            max number of validation messages that can be save in metadata
     * @return administrative metadata
     * @throws AdmMetadataProcessingException
     *             when some problem with the construction of metadata occurs
     */
    AdmMetadata constructAdministrativeMetadata(DataFile file, ExtractedMetadata extractedMetadata, Date creationDate,
            int maxValidationMessages)
            throws AdmMetadataProcessingException;

}
