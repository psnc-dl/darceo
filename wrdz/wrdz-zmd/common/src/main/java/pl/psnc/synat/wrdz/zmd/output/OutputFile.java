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
package pl.psnc.synat.wrdz.zmd.output;

import java.util.List;

import pl.psnc.synat.wrdz.zmd.download.DownloadTask;

/**
 * A class representing file handle to cached resource.
 */
public class OutputFile {

    /**
     * Location of file in the cache.
     */
    protected final DownloadTask file;

    /**
     * List of locations of downloaded metadata files.
     */
    protected final List<DownloadTask> metadataFiles;

    /**
     * Location of the file with extracted technical metadata.
     */
    protected List<OutputTask> techMetadataFiles;

    /**
     * Location of the file with constructed administrative metadata.
     */
    protected OutputTask admMetadataFile;

    /**
     * Sequence property. Used for file ordering.
     */
    private final Integer sequence;


    /**
     * Constructs new instance out of passed parameters.
     * 
     * @param file
     *            downloaded file handle.
     * @param sequence
     *            sequence value, used for file ordering purposes
     * @param metadataFiles
     *            metadata files of the downloaded file.
     */
    public OutputFile(DownloadTask file, Integer sequence, List<DownloadTask> metadataFiles) {
        this.file = file;
        this.sequence = sequence;
        this.metadataFiles = metadataFiles;
    }


    public DownloadTask getFile() {
        return file;
    }


    public Integer getSequence() {
        return sequence;
    }


    public List<DownloadTask> getMetadataFiles() {
        return metadataFiles;
    }


    /**
     * Adds a collection of metadata resources to the existing list.
     * 
     * @param metadata
     *            colection of metadata resources.
     */
    public void addToMetadataFiles(List<DownloadTask> metadata) {
        metadataFiles.addAll(metadata);
    }


    public List<OutputTask> getTechMetadataFiles() {
        return techMetadataFiles;
    }


    public void setTechMetadataFiles(List<OutputTask> techMetadataFiles) {
        this.techMetadataFiles = techMetadataFiles;
    }


    public OutputTask getAdmMetadataFile() {
        return admMetadataFile;
    }


    public void setAdmMetadataFile(OutputTask admMetadataFile) {
        this.admMetadataFile = admMetadataFile;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((file == null) ? 0 : file.hashCode());
        result = prime * result + ((metadataFiles == null) ? 0 : metadataFiles.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof OutputFile)) {
            return false;
        }
        OutputFile other = (OutputFile) obj;
        if (file == null) {
            if (other.file != null) {
                return false;
            }
        } else if (!file.equals(other.file)) {
            return false;
        }
        if (metadataFiles == null) {
            if (other.metadataFiles != null) {
                return false;
            }
        } else if (!metadataFiles.equals(other.metadataFiles)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("OutputFile ");
        sb.append("[file = ").append(file);
        sb.append(", metadataFiles = ").append(metadataFiles);
        sb.append(", techMetadataFiles = ").append(techMetadataFiles);
        sb.append(", admMetadataFile = ").append(admMetadataFile);
        sb.append("]");
        return sb.toString();
    }

}
