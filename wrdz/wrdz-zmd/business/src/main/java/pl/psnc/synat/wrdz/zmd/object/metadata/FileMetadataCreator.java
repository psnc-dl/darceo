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
package pl.psnc.synat.wrdz.zmd.object.metadata;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.FileProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.types.MetadataType;
import pl.psnc.synat.wrdz.zmd.output.OutputTask;

/**
 * Provides methods useful for file's metadata creation.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class FileMetadataCreator extends MetadataCreator {

    /**
     * Creates a {@link FileExtractedMetadata} entity.
     * 
     * @param source
     *            output task representing metadata
     * @return unpersisted metadata entity
     */
    public FileExtractedMetadata createFileExtractedMetadata(OutputTask source) {
        FileExtractedMetadata metadataFile = new FileExtractedMetadata();
        metadataFile.setType(MetadataType.FILES_EXTRACTED);
        fillWithData(metadataFile, source);
        return metadataFile;
    }


    /**
     * Creates a {@link FileProvidedMetadata} entity.
     * 
     * @param source
     *            output task representing metadata
     * @return unpersisted metadata entity
     */
    public FileProvidedMetadata createFileProvidedMetadata(OutputTask source) {
        FileProvidedMetadata metadataFile = new FileProvidedMetadata();
        metadataFile.setType(MetadataType.FILES_PROVIDED);
        fillWithData(metadataFile, source);
        return metadataFile;
    }
}
