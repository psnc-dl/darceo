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
import java.util.Set;

import pl.psnc.synat.wrdz.zmd.download.DownloadTask;

/**
 * Bundle of metadata files on the basis of which a digital object is created/modified.
 */
public class MetadataFilesBundle {

    /**
     * List of metadata files to add to an object.
     */
    protected final List<DownloadTask> addedMetadata;

    /**
     * List of metadata files to modify in an object.
     */
    protected final List<DownloadTask> modifiedMetadata;

    /**
     * List of metadata files to delete from an object.
     */
    protected final Set<String> deletedMetadata;


    /**
     * Constructs a bundle.
     * 
     * @param addedMetadata
     *            files to add
     * @param modifiedMetadata
     *            files to modify
     * @param deletedMetadata
     *            files to delete
     */
    public MetadataFilesBundle(List<DownloadTask> addedMetadata, List<DownloadTask> modifiedMetadata,
            Set<String> deletedMetadata) {
        this.addedMetadata = addedMetadata;
        this.modifiedMetadata = modifiedMetadata;
        this.deletedMetadata = deletedMetadata;
    }


    public List<DownloadTask> getAddedMetadata() {
        return addedMetadata;
    }


    public List<DownloadTask> getModifiedMetadata() {
        return modifiedMetadata;
    }


    public Set<String> getDeletedMetadata() {
        return deletedMetadata;
    }

}
