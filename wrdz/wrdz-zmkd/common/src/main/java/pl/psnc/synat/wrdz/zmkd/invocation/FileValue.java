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
package pl.psnc.synat.wrdz.zmkd.invocation;

import java.io.File;

/**
 * File value in case of files as parameters.
 */
public class FileValue {

    /** File. */
    private File file;

    /** Mimetype of the file. */
    private String mimetype;

    /** Filename of the file. */
    private String filename;


    /**
     * Default constructor.
     */
    public FileValue() {
    }


    /**
     * Constructor.
     * 
     * @param file
     *            file
     * @param mimetype
     *            mimetype
     * @param filename
     *            filename
     */
    public FileValue(File file, String mimetype, String filename) {
        this.file = file;
        this.mimetype = mimetype;
        this.filename = filename;
    }


    public File getFile() {
        return file;
    }


    public void setFile(File file) {
        this.file = file;
    }


    public String getMimetype() {
        return mimetype;
    }


    public void setMimetype(String mimetype) {
        this.mimetype = mimetype;
    }


    public String getFilename() {
        return filename;
    }


    public void setFilename(String filename) {
        this.filename = filename;
    }

}
