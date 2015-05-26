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

import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.types.MetadataType;
import pl.psnc.synat.wrdz.zmd.output.OutputTask;

/**
 * Provides methods useful for object's metadata creation.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ObjectMetadataCreator extends MetadataCreator {

    /**
     * Creates {@link ObjectProvidedMetadata} entity containing information about digital object's metadata provided by
     * user.
     * 
     * @param source
     *            output task representing metadata to be associated.
     * @return prepared, unpersisted metadata entity
     */
    public ObjectProvidedMetadata createProvidedMetadata(OutputTask source) {
        ObjectProvidedMetadata metadataFile = new ObjectProvidedMetadata();
        metadataFile.setType(MetadataType.OBJECTS_PROVIDED);
        fillWithData(metadataFile, source);
        return metadataFile;
    }


    /**
     * Creates {@link ObjectExtractedMetadata} entity containing information about digital object's extracted metadata.
     * 
     * @param source
     *            output task representing metadata to be associated.
     * @return prepared, unpersisted metadata entity
     */
    public ObjectExtractedMetadata createExtractedMetadata(OutputTask source) {
        ObjectExtractedMetadata metadataFile = new ObjectExtractedMetadata();
        metadataFile.setType(MetadataType.OBJECTS_EXTRACTED);
        fillWithData(metadataFile, source);
        return metadataFile;
    }

}
