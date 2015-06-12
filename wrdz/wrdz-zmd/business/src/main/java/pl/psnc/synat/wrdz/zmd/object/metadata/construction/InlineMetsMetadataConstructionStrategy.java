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

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.metadata.mets.MetsMetadataBuilder;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectExtractedMetadata;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;

/**
 * Content version metadata construction strategy in the METS schema where metadata are included in METS.
 */
public class InlineMetsMetadataConstructionStrategy extends MetsMetadataConstructionStrategy {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(InlineMetsMetadataConstructionStrategy.class);


    @Override
    protected void buildMetadataForObject(MetsMetadataBuilder metsBuilder,
            pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile metadataFile,
            ObjectExtractedMetadata previousMetsMetadata) {
        logger.debug("metadataFile: " + metadataFile.getObjectFilepath());
        if (metadataFile.getCachePath() != null) {
            metsBuilder.addMetadataForObjectInline(getMetadataStatus(metadataFile.getType()),
                getNonNullNamespaceType(metadataFile.getMainNamespace()), new File(metadataFile.getCachePath()),
                metadataFile.getFilename());
        } else {
            metsBuilder.addMetadataForObjectInline(getMetadataStatus(metadataFile.getType()),
                getNonNullNamespaceType(metadataFile.getMainNamespace()),
                getPreviousMetadataContents(previousMetsMetadata.getMetadataContent()), metadataFile.getFilename());
        }
    }


    @Override
    protected void buildMetadataForFile(MetsMetadataBuilder metsBuilder, DataFile dataFile,
            pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile metadataFile,
            ObjectExtractedMetadata previousMetsMetadata) {
        logger.debug("metadataFile: " + metadataFile.getObjectFilepath());
        if (metadataFile.getCachePath() != null) {
            metsBuilder.addMetadataForFileInline(dataFile.getObjectFilepath(),
                getMetadataStatus(metadataFile.getType()), getNonNullNamespaceType(metadataFile.getMainNamespace()),
                new File(metadataFile.getCachePath()), metadataFile.getFilename());
        } else {
            metsBuilder.addMetadataForFileInline(dataFile.getObjectFilepath(),
                getMetadataStatus(metadataFile.getType()), getNonNullNamespaceType(metadataFile.getMainNamespace()),
                getPreviousMetadataContents(previousMetsMetadata.getMetadataContent()), metadataFile.getFilename());
        }

    }


    /**
     * Fetches the latest contents of object's metadata.
     * 
     * @param operations
     *            list of operations on object's metadata.
     * @return String containing previous metadata contents.
     */
    private String getPreviousMetadataContents(List<Operation> operations) {
        if (operations == null || operations.size() == 0) {
            throw new RuntimeException("No previous version of METS metadata!");
        }
        Collections.sort(operations, new OperationsDateComparator());
        Operation result = operations.get(operations.size() - 1);
        if (result.getMetadataType() != NamespaceType.METS) {
            throw new RuntimeException("No previous version of METS metadata!");
        }
        return result.getContents();
    }
}
