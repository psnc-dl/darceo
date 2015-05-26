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
package pl.psnc.synat.wrdz.zmkd.object;

import java.io.File;
import java.util.List;

import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;

/**
 * Describes a digital object.
 */
public class DigitalObjectInfo {

    /** Work directory. */
    private final File workDir;

    /** Digital object type. */
    private ObjectType type;

    /** File and file formats informations. */
    private List<DataFileInfo> files;


    /**
     * Constructor.
     * 
     * @param workDir
     *            the directory in which the object's files reside
     * @param type
     *            object's type
     * @param files
     *            object's data files
     */
    public DigitalObjectInfo(File workDir, ObjectType type, List<DataFileInfo> files) {
        this.workDir = workDir;
        this.type = type;
        this.files = files;
    }


    public File getWorkDir() {
        return workDir;
    }


    public ObjectType getType() {
        return type;
    }


    public void setType(ObjectType type) {
        this.type = type;
    }


    public List<DataFileInfo> getFiles() {
        return this.files;
    }


    public void setFiles(List<DataFileInfo> files) {
        this.files = files;
    }

}
