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

import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectExtractedMetadata;

/**
 * Content version metadata construction strategy in the METS schema where metadata are referenced by href links.
 */
public class LinkMetsMetadataConstructionStrategy extends MetsMetadataConstructionStrategy {

    @Override
    protected void buildMetadataForObject(MetsMetadataBuilder metsBuilder,
            pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile metadataFile,
            ObjectExtractedMetadata previousMetsMetadata) {
        metsBuilder.addMetadataForObjectByLink(getMetadataStatus(metadataFile.getType()),
            getNonNullNamespaceType(metadataFile.getMainNamespace()), metadataFile.getObjectFilepath(),
            metadataFile.getFilename());
    }


    @Override
    protected void buildMetadataForFile(MetsMetadataBuilder metsBuilder, DataFile dataFile,
            pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile metadataFile,
            ObjectExtractedMetadata previousMetsMetadata) {
        metsBuilder.addMetadataForFileByLink(dataFile.getObjectFilepath(), getMetadataStatus(metadataFile.getType()),
            getNonNullNamespaceType(metadataFile.getMainNamespace()), metadataFile.getObjectFilepath(),
            metadataFile.getFilename());

    }

}
