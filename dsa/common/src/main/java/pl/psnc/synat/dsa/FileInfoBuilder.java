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
package pl.psnc.synat.dsa;

import java.io.File;
import java.util.Date;

/**
 * Builder of a file info object.
 * 
 */
public class FileInfoBuilder {

    /**
     * File object. A seed for the construction of a file info object.
     */
    private File file;


    /**
     * Constructs a file info builder object.
     * 
     * @param file
     *            seed
     */
    public FileInfoBuilder(File file) {
        this.file = file;
    }


    /**
     * Build a file info object on the basis of the seed.
     * 
     * @return file info object
     */
    public FileInfo build() {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setPath(file.getParent());
        fileInfo.setName(file.getName());
        fileInfo.setLastModified(new Date(file.lastModified()));
        if (file.isDirectory()) {
            fileInfo.setType(FileInfo.Type.DIR);
            fileInfo.setSize(null);
        } else {
            fileInfo.setType(FileInfo.Type.FILE);
            fileInfo.setSize(file.length());
        }
        return fileInfo;
    }

}
