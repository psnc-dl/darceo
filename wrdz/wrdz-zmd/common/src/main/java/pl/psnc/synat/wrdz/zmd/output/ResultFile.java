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

import java.io.File;

/**
 * A class representing file handle to archive containing object's data fetched into cache for user to download.
 */
public class ResultFile {

    /**
     * Handle to result file.
     */
    private final File file;

    /**
     * Proposed human readable filename to be returned to client.
     */
    private final String proposedName;


    /**
     * Creates new instance of this class.
     * 
     * @param file
     *            handle to result file.
     * @param proposedName
     *            proposed human readable filename to be returned to client.
     */
    public ResultFile(File file, String proposedName) {
        this.file = file;
        this.proposedName = proposedName;
    }


    public File getFile() {
        return file;
    }


    public String getProposedName() {
        return proposedName;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((file == null) ? 0 : file.hashCode());
        result = prime * result + ((proposedName == null) ? 0 : proposedName.hashCode());
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
        if (!(obj instanceof ResultFile)) {
            return false;
        }
        ResultFile other = (ResultFile) obj;
        if (file == null) {
            if (other.file != null) {
                return false;
            }
        } else if (!file.equals(other.file)) {
            return false;
        }
        if (proposedName == null) {
            if (other.proposedName != null) {
                return false;
            }
        } else if (!proposedName.equals(other.proposedName)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("ResultFile ");
        sb.append("[file = ").append(file);
        sb.append(", proposedName = ").append(proposedName);
        sb.append("]");
        return sb.toString();
    }

}
