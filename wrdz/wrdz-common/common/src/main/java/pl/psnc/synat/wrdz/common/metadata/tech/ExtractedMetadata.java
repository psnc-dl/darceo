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
import java.util.List;

/**
 * The class represents extracted metadata of a file.
 */
public class ExtractedMetadata implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -4058376339086147942L;

    /**
     * File format.
     */
    private FileFormat fileFormat;

    /**
     * File status - info about file format validation.
     */
    private FileStatus fileStatus;

    /**
     * Technical metadata in some standard schema.
     */
    private List<TechMetadata> techMetadata;

    /**
     * Additional extracted metadata.
     */
    private AdditionalExtractedMetadata additionalMetadata;


    public FileFormat getFileFormat() {
        return fileFormat;
    }


    public void setFileFormat(FileFormat fileFormat) {
        this.fileFormat = fileFormat;
    }


    public FileStatus getFileStatus() {
        return fileStatus;
    }


    public void setFileStatus(FileStatus fileStatus) {
        this.fileStatus = fileStatus;
    }


    public List<TechMetadata> getTechMetadata() {
        return techMetadata;
    }


    public void setTechMetadata(List<TechMetadata> techMetadata) {
        this.techMetadata = techMetadata;
    }


    public AdditionalExtractedMetadata getAdditionlMetadata() {
        return additionalMetadata;
    }


    public void setAdditionalMetadata(AdditionalExtractedMetadata additionalMetadata) {
        this.additionalMetadata = additionalMetadata;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("ExtractedMetadata ");
        sb.append("[fileFormat = ").append(fileFormat);
        sb.append(", fileStatus = ").append(fileStatus);
        sb.append(", techMetadata = ").append(techMetadata);
        sb.append(", additionalMetadata = ").append(additionalMetadata);
        sb.append("]");
        return sb.toString();
    }

}
