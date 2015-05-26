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
import pl.psnc.synat.wrdz.common.metadata.adm.PremisMetadataBuilder;
import pl.psnc.synat.wrdz.common.metadata.adm.PremisMetadataBuilderFactory;
import pl.psnc.synat.wrdz.common.metadata.tech.ExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;

/**
 * Strategy which constructs administrative metadata in the PREMIS schema.
 */
public class PremisMetadataConstructionStrategy implements AdministrativeMetadataConstructionStrategy {

    /**
     * Pronom registry name.
     */
    private static final String PRONOM_REGISTRY_NAME = "PRONOM";


    @Override
    public AdmMetadata constructAdministrativeMetadata(DataFile file, ExtractedMetadata extractedMetadata,
            Date creationDate, int maxValidationMessages)
            throws AdmMetadataProcessingException {
        PremisMetadataBuilder premisBuilder = PremisMetadataBuilderFactory.getInstance().getPremisMetadataBuilder();
        premisBuilder.setFileRelativePath(file.getFilename());
        if (!file.getHashes().isEmpty()) {
            premisBuilder.setFileHash(file.getHashes().get(0).getHashType().name(), file.getHashes().get(0)
                    .getHashValue());
        }
        premisBuilder.setFileSize(file.getSize());
        premisBuilder.setFileCreationEvent(creationDate);
        if (extractedMetadata != null && extractedMetadata.getFileFormat() != null) {
            if (extractedMetadata.getFileFormat().getMimeType() != null) {
                premisBuilder.setFileFormatDesignation(extractedMetadata.getFileFormat().getMimeType(),
                    extractedMetadata.getFileFormat().getFormatVersion());
            }
            if (extractedMetadata.getFileFormat().getPuid() != null) {
                premisBuilder.setFileFormatRegistry(PRONOM_REGISTRY_NAME, extractedMetadata.getFileFormat().getPuid());
            }
        }
        if (extractedMetadata != null
                && extractedMetadata.getAdditionlMetadata() != null
                && (extractedMetadata.getAdditionlMetadata().getCreatingApplicationName() != null || extractedMetadata
                        .getAdditionlMetadata().getDateCreatedByApplication() != null)) {
            premisBuilder.setCreativeApplication(extractedMetadata.getAdditionlMetadata().getCreatingApplicationName(),
                extractedMetadata.getAdditionlMetadata().getCreatingApplicationVersion(), extractedMetadata
                        .getAdditionlMetadata().getDateCreatedByApplication());

        }
        if (extractedMetadata != null && extractedMetadata.getFileStatus() != null) {
            premisBuilder.setFileFormatValidationEvent(creationDate, extractedMetadata.getFileStatus().getStatus()
                    .name().replace("_", "-").toLowerCase(), extractedMetadata.getFileStatus().getWarnings(),
                maxValidationMessages);
        }
        return premisBuilder.build();
    }

}
