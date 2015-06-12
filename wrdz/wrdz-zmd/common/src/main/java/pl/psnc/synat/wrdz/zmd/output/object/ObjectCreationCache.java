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

import java.io.Serializable;
import java.util.List;

import pl.psnc.synat.wrdz.zmd.download.DownloadTask;
import pl.psnc.synat.wrdz.zmd.output.OutputFile;

/**
 * A class representing cached object's creation resources.
 */
public class ObjectCreationCache implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -5715281728234830905L;

    /**
     * Path to the root folder for cached files.
     */
    protected final String cachePath;

    /**
     * List of cached data files to add.
     */
    protected List<OutputFile> addedFiles;

    /**
     * List of object's added metadata.
     */
    protected List<DownloadTask> addedMetadata;


    /**
     * Constructor with the root of cache.
     * 
     * @param cachePath
     *            root folder for cached files
     */
    public ObjectCreationCache(String cachePath) {
        this.cachePath = cachePath;
    }


    public String getCachePath() {
        return cachePath;
    }


    public List<OutputFile> getAddedFiles() {
        return addedFiles;
    }


    public void setAddedFiles(List<OutputFile> addedFiles) {
        this.addedFiles = addedFiles;
    }


    public List<DownloadTask> getAddedMetadata() {
        return addedMetadata;
    }


    public void setAddedMetadata(List<DownloadTask> addedMetadata) {
        this.addedMetadata = addedMetadata;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cachePath == null) ? 0 : cachePath.hashCode());
        result = prime * result + ((addedFiles == null) ? 0 : addedFiles.hashCode());
        result = prime * result + ((addedMetadata == null) ? 0 : addedMetadata.hashCode());
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
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("ObjectModificationCache ");
        sb.append("[cachePath = ").append(cachePath);
        sb.append(", addedFiles = ").append(addedFiles);
        sb.append(", addedMetadata = ").append(addedMetadata);
        sb.append("]");
        return sb.toString();
    }

}
