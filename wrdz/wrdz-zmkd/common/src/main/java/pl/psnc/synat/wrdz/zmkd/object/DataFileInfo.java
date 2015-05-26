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

/**
 * Describes a single data file.
 */
public class DataFileInfo {

    /** Relative file path. */
    private final String path;

    /** Format PRONOM identifier. */
    private final String puid;

    /** File sequence. */
    private Integer sequence;

    /** File. */
    private final File file;


    /**
     * Constructor.
     * 
     * @param path
     *            path to the file (relative to the object directory)
     * @param puid
     *            format PRONOM identifier
     * @param sequence
     *            file sequence value
     * @param file
     *            file
     */
    public DataFileInfo(String path, String puid, Integer sequence, File file) {
        this.path = path;
        this.puid = puid;
        this.sequence = sequence;
        this.file = file;
    }


    public String getPuid() {
        return puid;
    }


    public String getPath() {
        return path;
    }


    public Integer getSequence() {
        return sequence;
    }


    public File getFile() {
        return file;
    }


    /**
     * Adds a difference to the sequence number, if it exists.
     * 
     * @param difference
     *            difference to add
     */
    public void addToSequence(Integer difference) {
        if (sequence != null) {
            sequence += difference;
        }
    }


    /**
     * Subtracts a difference from the sequence number, if it exists.
     * 
     * @param difference
     *            difference to subtract
     */
    public void subtractFromSequence(Integer difference) {
        if (sequence != null) {
            sequence -= difference;
        }
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("DataFileInfo ");
        sb.append("[path = ").append(path);
        sb.append(", puid = ").append(puid);
        sb.append(", sequence = ").append(sequence);
        sb.append(", file = ").append(file);
        sb.append("]");
        return sb.toString();
    }

}
