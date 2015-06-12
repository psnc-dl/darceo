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
package pl.psnc.synat.wrdz.common.metadata.tech;

import java.io.Serializable;
import java.util.Set;

/**
 * Info about file format validation.
 */
public class FileStatus implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 8800259753964240024L;

    /**
     * Status of a file validation process.
     */
    private FileValidationStatus status;

    /**
     * Messages from tools used to validating.
     */
    private Set<String> warnings;


    public FileValidationStatus getStatus() {
        return status;
    }


    public void setStatus(FileValidationStatus status) {
        this.status = status;
    }


    public Set<String> getWarnings() {
        return warnings;
    }


    public void setWarnings(Set<String> warnings) {
        this.warnings = warnings;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("FileStatus ");
        sb.append("[status = ").append(status);
        sb.append(", warnings = ").append(warnings);
        sb.append("]");
        return sb.toString();
    }

}
