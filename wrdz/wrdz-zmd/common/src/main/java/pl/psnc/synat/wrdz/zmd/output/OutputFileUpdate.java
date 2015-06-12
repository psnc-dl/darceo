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

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import pl.psnc.synat.wrdz.zmd.download.DownloadTask;

/**
 * A class representing file update handle to cached resource.
 */
public class OutputFileUpdate extends OutputFile implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 2788671159438503663L;

    /**
     * List of files modified metadata resources.
     */
    private final List<DownloadTask> modifiedMetadataFiles;

    /**
     * Set of files deleted metadata resources.
     */
    private final Set<String> deletedMetadataFiles;


    /**
     * Creates new file update handle.
     * 
     * @param file
     *            file resource.
     * @param sequence
     *            sequence value, used for file ordering purposes
     * @param addedMetadataFiles
     *            list of files added metadata resources.
     * @param modifiedMetadataFiles
     *            list of files modified metadata resources.
     * @param deletedMetadataFiles
     *            list of files deleted metadata resources.
     */
    public OutputFileUpdate(DownloadTask file, Integer sequence, List<DownloadTask> addedMetadataFiles,
            List<DownloadTask> modifiedMetadataFiles, Set<String> deletedMetadataFiles) {
        super(file, sequence, addedMetadataFiles);
        this.modifiedMetadataFiles = modifiedMetadataFiles;
        this.deletedMetadataFiles = deletedMetadataFiles;
    }


    public List<DownloadTask> getModifiedMetadataFiles() {
        return modifiedMetadataFiles;
    }


    public Set<String> getDeletedMetadataFiles() {
        return deletedMetadataFiles;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((file == null) ? 0 : file.hashCode());
        result = prime * result + ((metadataFiles == null) ? 0 : metadataFiles.hashCode());
        result = prime * result + ((deletedMetadataFiles == null) ? 0 : deletedMetadataFiles.hashCode());
        result = prime * result + ((modifiedMetadataFiles == null) ? 0 : modifiedMetadataFiles.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof OutputFileUpdate)) {
            return false;
        }
        OutputFileUpdate other = (OutputFileUpdate) obj;
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
        if (deletedMetadataFiles == null) {
            if (other.deletedMetadataFiles != null) {
                return false;
            }
        } else if (!deletedMetadataFiles.equals(other.deletedMetadataFiles)) {
            return false;
        }
        if (modifiedMetadataFiles == null) {
            if (other.modifiedMetadataFiles != null) {
                return false;
            }
        } else if (!modifiedMetadataFiles.equals(other.modifiedMetadataFiles)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("OutputFileUpdate ");
        sb.append("[file = ").append(file);
        sb.append(", metadataFiles = ").append(metadataFiles);
        sb.append(", modifiedMetadataFiles = ").append(modifiedMetadataFiles);
        sb.append(", deletedMetadataFiles = ").append(deletedMetadataFiles);
        sb.append(", techMetadataFiles = ").append(techMetadataFiles);
        sb.append(", admMetadataFile = ").append(admMetadataFile);
        sb.append("]");
        return sb.toString();
    }

}
