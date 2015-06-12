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

import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.hash.DataFileHash;

/**
 * Builder of data file.
 */
public class DataFileFactory {

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
     * If <code>true</code>, includes absolute path of files, otherwise they are skipped.
     */
    private boolean absolute;

    /**
     * Data file being the base of a new object created by the factory.
     */
    private DataFileVersion dataFile;


    /**
     * Creates new instance of the factory.
     * 
     * @param dataFile
     *            file being the base of a new object created by the factory.
     * @param provided
     *            if <code>true</code>, enables extraction of provided metadata, otherwise it is skipped.
     * @param extracted
     *            if <code>true</code>, enables extraction of extracted metadata, otherwise it is skipped.
     * @param hashes
     *            if <code>true</code>, includes hashes of files, otherwise they are skipped.
     * @param absolute
     *            if <code>true</code>, includes absolute paths of files, otherwise they are skipped.
     */
    public DataFileFactory(DataFileVersion dataFile, boolean provided, boolean extracted, boolean hashes,
            boolean absolute) {
        this.dataFile = dataFile;
        this.provided = provided;
        this.extracted = extracted;
        this.hashes = hashes;
        this.absolute = absolute;
    }


    public void setDataFile(DataFileVersion dataFile) {
        this.dataFile = dataFile;
    }


    /**
     * Extract's file's metadata information from the database entries.
     * 
     * @param metadataFiles
     *            metadata information from the database entries.
     * @return extracted metadata information.
     */
    private List<MetadataFile> extractMetadata(List<?> metadataFiles) {
        if (metadataFiles != null && metadataFiles.size() > 0) {
            List<MetadataFile> metadata = new ArrayList<MetadataFile>();
            MetadataFileFactory metadataFileFactory = null;
            for (Object metadataFile : metadataFiles) {
                if (metadataFileFactory == null) {
                    metadataFileFactory = new MetadataFileFactory(
                            (pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile) metadataFile, hashes, absolute);
                } else {
                    metadataFileFactory
                            .setMetadataFile((pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile) metadataFile);
                }
                MetadataFile mf = metadataFileFactory.produceMetadataFile();
                if (mf != null) {
                    metadata.add(mf);
                }
            }
            return metadata;
        }
        return null;
    }


    /**
     * Produces new data file.
     * 
     * @return constructed data file.
     */
    public DataFile produceDataFile() {
        if (dataFile != null) {
            DataFile file = new DataFile();
            file.setPath(dataFile.getDataFile().getFilename());
            file.setSize(dataFile.getDataFile().getSize());
            FileFormat format = new FileFormat();
            format.setMimeType(dataFile.getDataFile().getFormat().getMimeType());
            format.setPuid(dataFile.getDataFile().getFormat().getPuid());
            format.setType(FileType.valueOf(dataFile.getDataFile().getFormat().getType().name().toUpperCase()));
            format.setVersion(dataFile.getDataFile().getFormat().getVersion());
            file.setFormat(format);
            file.setSeq(dataFile.getSequence());
            if (absolute) {
                file.setFullpath(dataFile.getDataFile().getRepositoryFilepath());
            }
            if (hashes && !dataFile.getDataFile().getHashes().isEmpty()) {
                FileHashes fileHashes = new FileHashes();
                for (DataFileHash dataFileHash : dataFile.getDataFile().getHashes()) {
                    FileHash hash = new FileHash();
                    hash.setType(dataFileHash.getHashType().name());
                    hash.setValue(dataFileHash.getHashValue());
                    fileHashes.getHash().add(hash);
                }
                file.setHashes(fileHashes);
            }
            if (provided) {
                List<MetadataFile> provMetadata = extractMetadata(dataFile.getProvidedMetadata());
                if (provMetadata != null) {
                    MetadataFiles mfs = new MetadataFiles();
                    mfs.getMetadataFile().addAll(provMetadata);
                    file.setProvidedMetadata(mfs);
                }
            }
            if (extracted) {
                List<MetadataFile> extMetadata = extractMetadata(dataFile.getDataFile().getExtractedMetadata());
                if (extMetadata != null) {
                    MetadataFiles mfs = new MetadataFiles();
                    mfs.getMetadataFile().addAll(extMetadata);
                    file.setExtractedMetadata(mfs);
                }
            }
            return file;
        }
        return null;
    }
}
