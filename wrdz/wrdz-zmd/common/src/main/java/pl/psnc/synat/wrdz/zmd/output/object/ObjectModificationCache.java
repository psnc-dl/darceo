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
package pl.psnc.synat.wrdz.zmd.output.object;

import java.util.List;

import pl.psnc.synat.wrdz.zmd.download.DownloadTask;
import pl.psnc.synat.wrdz.zmd.output.OutputFileUpdate;

/**
 * A class representing cached object's modification resources.
 */
public class ObjectModificationCache extends ObjectCreationCache {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -4220505628684468267L;

    /**
     * List of cached data files to modify.
     */
    protected List<OutputFileUpdate> modifiedFiles;

    /**
     * List of object's modified metadata.
     */
    protected List<DownloadTask> modifiedMetadata;


    /**
     * Constructor with the root of cache.
     * 
     * @param cachePath
     *            root folder for cached files
     */
    public ObjectModificationCache(String cachePath) {
        super(cachePath);
    }


    public List<OutputFileUpdate> getModifiedFiles() {
        return modifiedFiles;
    }


    public void setModifiedFiles(List<OutputFileUpdate> modifiedFiles) {
        this.modifiedFiles = modifiedFiles;
    }


    public List<DownloadTask> getModifiedMetadata() {
        return modifiedMetadata;
    }


    public void setModifiedMetadata(List<DownloadTask> modifiedMetadata) {
        this.modifiedMetadata = modifiedMetadata;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cachePath == null) ? 0 : cachePath.hashCode());
        result = prime * result + ((addedFiles == null) ? 0 : addedFiles.hashCode());
        result = prime * result + ((addedMetadata == null) ? 0 : addedMetadata.hashCode());
        result = prime * result + ((modifiedFiles == null) ? 0 : modifiedFiles.hashCode());
        result = prime * result + ((modifiedMetadata == null) ? 0 : modifiedMetadata.hashCode());
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
        if (!(obj instanceof ObjectModificationCache)) {
            return false;
        }
        ObjectModificationCache other = (ObjectModificationCache) obj;
        if (cachePath == null) {
            if (other.cachePath != null) {
                return false;
            }
        } else if (!cachePath.equals(other.cachePath)) {
            return false;
        }
        if (addedFiles == null) {
            if (other.addedFiles != null) {
                return false;
            }
        } else if (!addedFiles.equals(other.addedFiles)) {
            return false;
        }
        if (addedMetadata == null) {
            if (other.addedMetadata != null) {
                return false;
            }
        } else if (!addedMetadata.equals(other.addedMetadata)) {
            return false;
        }
        if (modifiedFiles == null) {
            if (other.modifiedFiles != null) {
                return false;
            }
        } else if (!modifiedFiles.equals(other.modifiedFiles)) {
            return false;
        }
        if (modifiedMetadata == null) {
            if (other.modifiedMetadata != null) {
                return false;
            }
        } else if (!modifiedMetadata.equals(other.modifiedMetadata)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("ObjectModificationCache ");
        sb.append("[cachePath = ").append(cachePath);
        sb.append(", addedFiles = ").append(addedFiles);
        sb.append(", modifiedFiles = ").append(modifiedFiles);
        sb.append(", addedMetadata = ").append(addedMetadata);
        sb.append(", modifiedMetadata = ").append(modifiedMetadata);
        sb.append("]");
        return sb.toString();
    }

}
