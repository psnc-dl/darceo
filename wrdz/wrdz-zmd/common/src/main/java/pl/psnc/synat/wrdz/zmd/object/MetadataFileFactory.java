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

import pl.psnc.synat.wrdz.zmd.entity.object.hash.MetadataFileHash;

/**
 * Builder of metadata file.
 */
public class MetadataFileFactory {

    /**
     * If <code>true</code>, includes hashes of files, otherwise they are skipped.
     */
    private boolean hashes;

    /**
     * If <code>true</code>, includes full paths of files, otherwise they are skipped.
     */
    private boolean absolute;

    /**
     * Metadata file from which metada information's are extracted.
     */
    private pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile metadataFile;


    /**
     * Creates new instance of the factory.
     * 
     * @param metadataFile
     *            metadata file from which metada information's are extracted.
     * @param hashes
     *            if <code>true</code>, includes hashes of files, otherwise they are skipped.
     * @param absolute
     *            if <code>true</code>, includes full paths of files, otherwise they are skipped.
     */
    public MetadataFileFactory(pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile metadataFile, boolean hashes,
            boolean absolute) {
        this.metadataFile = metadataFile;
        this.hashes = hashes;
        this.absolute = absolute;
    }


    public void setMetadataFile(pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile metadataFile) {
        this.metadataFile = metadataFile;
    }


    /**
     * Produces metadata information.
     * 
     * @return metadata information.
     */
    public MetadataFile produceMetadataFile() {
        if (metadataFile == null) {
            return null;
        }
        MetadataFile metadata = new MetadataFile();
        metadata.setPath(metadataFile.getFilename());
        metadata.setSize(metadataFile.getSize());
        if (absolute) {
            metadata.setFullpath(metadataFile.getRepositoryFilepath());
        }
        if (hashes && !metadataFile.getHashes().isEmpty()) {
            FileHashes fileHashes = new FileHashes();
            for (MetadataFileHash dataFileHash : metadataFile.getHashes()) {
                FileHash hash = new FileHash();
                hash.setType(dataFileHash.getHashType().name());
                hash.setValue(dataFileHash.getHashValue());
                fileHashes.getHash().add(hash);
            }
            metadata.setHashes(fileHashes);
        }
        return metadata;
    }

}
