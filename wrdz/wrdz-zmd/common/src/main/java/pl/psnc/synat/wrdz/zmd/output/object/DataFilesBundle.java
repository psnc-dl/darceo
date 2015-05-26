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

import pl.psnc.synat.wrdz.zmd.output.OutputFile;
import pl.psnc.synat.wrdz.zmd.output.OutputFileUpdate;

/**
 * Bundle of data files on the basis of which a digital object is created/modified.
 */
public final class DataFilesBundle {

    /**
     * List of data files to add to an object.
     */
    protected final List<OutputFile> addedData;

    /**
     * List of data files to modify in an object.
     */
    protected final List<OutputFileUpdate> modifiedData;

    /**
     * List of data files to delete from an object.
     */
    protected final Set<String> deletedData;


    /**
     * Constructs a bundle.
     * 
     * @param addedData
     *            files to add
     * @param modifiedData
     *            files to modify
     * @param deletedData
     *            files to delete
     */
    public DataFilesBundle(List<OutputFile> addedData, List<OutputFileUpdate> modifiedData, Set<String> deletedData) {
        this.addedData = addedData;
        this.modifiedData = modifiedData;
        this.deletedData = deletedData;
    }


    public List<OutputFile> getAddedData() {
        return addedData;
    }


    public List<OutputFileUpdate> getModifiedData() {
        return modifiedData;
    }


    public Set<String> getDeletedData() {
        return deletedData;
    }

}
