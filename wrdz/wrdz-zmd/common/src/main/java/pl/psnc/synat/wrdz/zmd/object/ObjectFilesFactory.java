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
package pl.psnc.synat.wrdz.zmd.object;

import java.util.ArrayList;
import java.util.List;

import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.ObjectProvidedMetadata;

/**
 * Builder of files list of a digital object.
 */
public class ObjectFilesFactory {

    /**
     * If <code>true</code>, enables extraction of provided metadata, otherwise it is skipped.
     */
    private boolean provided;

    /**
     * If <code>true</code>, enables extraction of extracted metadata, otherwise it is skipped.
     */
    private boolean extracted;

    /**
     * If <code>true</code>, includes hashes of files, otherwise they are skipped.
     */
    private boolean hashes;

    /**
     * If <code>true</code>, includes absolute paths of files, otherwise they are skipped.
     */
    private boolean absolute;

    /**
     * Content version, for which the list of files will be constructed.
     */
    private ContentVersion contentVersion;


    /**
     * Creates new instance of the factory.
     * 
     * @param contentVersion
     *            content version, for which the list of files will be constructed.
     * @param provided
     *            if <code>true</code>, enables extraction of provided metadata, otherwise it is skipped.
     * @param extracted
     *            if <code>true</code>, enables extraction of extracted metadata, otherwise it is skipped.
     * @param hashes
     *            if <code>true</code>, includes hashes of files, otherwise they are skipped.
     * @param absolute
     *            if <code>true</code>, includes absolute paths of files, otherwise they are skipped.
     */
    public ObjectFilesFactory(ContentVersion contentVersion, Boolean provided, Boolean extracted, Boolean hashes,
            Boolean absolute) {
        this.contentVersion = contentVersion;
        this.provided = provided;
        this.extracted = extracted;
        this.hashes = hashes;
        this.absolute = absolute;
    }


    /**
     * Produces the output file list of object's data files.
     * 
     * @return output file list of object's data files.
     */
    private List<DataFile> produceDataFiles() {
        List<DataFileVersion> files = contentVersion.getFiles();
        if (!files.isEmpty()) {
            List<DataFile> results = new ArrayList<DataFile>();
            DataFileFactory dataFileFactory = null;
            for (DataFileVersion dataFile : files) {
                if (dataFileFactory == null) {
                    dataFileFactory = new DataFileFactory(dataFile, provided, extracted, hashes, absolute);
                } else {
                    dataFileFactory.setDataFile(dataFile);
                }
                DataFile df = dataFileFactory.produceDataFile();
                if (df != null) {
                    results.add(df);
                }
            }
            return results;
        }
        return null;
    }


    /**
     * Produces the list of object's provided metadata files.
     * 
     * @return list of object's provided metadata files.
     */
    private List<MetadataFile> produceProvidedMetadata() {
        List<ObjectProvidedMetadata> objectsProvidedMetadata = contentVersion.getProvidedMetadata();
        if (!objectsProvidedMetadata.isEmpty()) {
            List<MetadataFile> metadataFiles = new ArrayList<MetadataFile>();
            MetadataFileFactory metadataFileFactory = null;
            for (ObjectProvidedMetadata providedMetadataFile : objectsProvidedMetadata) {
                if (metadataFileFactory == null) {
                    metadataFileFactory = new MetadataFileFactory(providedMetadataFile, hashes, absolute);
                } else {
                    metadataFileFactory.setMetadataFile(providedMetadataFile);
                }
                MetadataFile mf = metadataFileFactory.produceMetadataFile();
                if (mf != null) {
                    metadataFiles.add(mf);
                }
            }
            return metadataFiles;
        }
        return null;
    }


    /**
     * Produces the information about object's extracted metadata.
     * 
     * @return information about object's extracted metadata.
     */
    private MetadataFile produceExtractedMetadata() {
        MetadataFileFactory metadataFileFactory = new MetadataFileFactory(contentVersion.getExtractedMetadata(),
                hashes, absolute);
        return metadataFileFactory.produceMetadataFile();
    }


    /**
     * Produces the information about the object's files.
     * 
     * @return information about the object's files.
     */
    public ObjectFiles produceObjectFiles() {
        ObjectFiles objectFiles = new ObjectFiles();
        objectFiles.setVersion(contentVersion.getVersion());
        DataFiles dfs = new DataFiles();
        dfs.getDataFile().addAll(produceDataFiles());
        objectFiles.setDataFiles(dfs);
        if (provided) {
            List<MetadataFile> providedMetadata = produceProvidedMetadata();
            if (providedMetadata != null && providedMetadata.size() > 0) {
                MetadataFiles mfs = new MetadataFiles();
                mfs.getMetadataFile().addAll(providedMetadata);
                objectFiles.setProvidedMetadata(mfs);
            }
        }
        if (extracted) {
            MetadataFile extractedMetadata = produceExtractedMetadata();
            if (extractedMetadata != null) {
                objectFiles.setExtractedMetadata(extractedMetadata);
            }
        }
        return objectFiles;
    }

}
