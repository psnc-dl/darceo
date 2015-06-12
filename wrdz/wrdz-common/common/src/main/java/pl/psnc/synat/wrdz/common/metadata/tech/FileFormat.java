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
 * The class represents a file format.
 */
public class FileFormat implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -9123857337754936998L;

    /**
     * ID of the format from the PRONOM and UDRF databases.
     */
    private String puid;

    /**
     * Mime type of the format.
     */
    private String mimeType;

    /**
     * Format version.
     */
    private String formatVersion;

    /**
     * Names of file format.
     */
    private Set<String> names;

    /**
     * File type.
     */
    private FileType fileType;


    public String getPuid() {
        return puid;
    }


    public void setPuid(String puid) {
        this.puid = puid;
    }


    public String getMimeType() {
        return mimeType;
    }


    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }


    public String getFormatVersion() {
        return formatVersion;
    }


    public void setFormatVersion(String formatVersion) {
        this.formatVersion = formatVersion;
    }


    public Set<String> getNames() {
        return names;
    }


    public void setNames(Set<String> names) {
        this.names = names;
    }


    public FileType getFileType() {
        return fileType;
    }


    public void setFileType(FileType fileType) {
        this.fileType = fileType;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("FileFormat ");
        sb.append("[puid = ").append(puid);
        sb.append(", mimeType = ").append(mimeType);
        sb.append(", formatVersion = ").append(formatVersion);
        sb.append(", names = ").append(names);
        sb.append(", fileType = ").append(fileType);
        sb.append("]");
        return sb.toString();
    }

}
