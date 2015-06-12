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

/**
 * Additional extracted metadata which are mainly used to build some sections of administrative metadata.
 */
public class AdditionalExtractedMetadata implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -795221583430132742L;

    /**
     * Name of creating application.
     */
    private String creatingApplicationName;

    /**
     * Version of creating application.
     */
    private String creatingApplicationVersion;

    /**
     * Creation date (by application).
     */
    private String dateCreatedByApplication;


    public String getCreatingApplicationName() {
        return creatingApplicationName;
    }


    public void setCreatingApplicationName(String creatingApplicationName) {
        this.creatingApplicationName = creatingApplicationName;
    }


    public String getCreatingApplicationVersion() {
        return creatingApplicationVersion;
    }


    public void setCreatingApplicationVersion(String creatingApplicationVersion) {
        this.creatingApplicationVersion = creatingApplicationVersion;
    }


    public String getDateCreatedByApplication() {
        return dateCreatedByApplication;
    }


    public void setDateCreatedByApplication(String dateCreatedByApplication) {
        this.dateCreatedByApplication = dateCreatedByApplication;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("AdditionalExtractedMetadata ");
        sb.append("[creatingApplicationName = ").append(creatingApplicationName);
        sb.append(", creatingApplicationVersion = ").append(creatingApplicationVersion);
        sb.append(", dateCreatedByApplication = ").append(dateCreatedByApplication);
        sb.append("]");
        return sb.toString();
    }

}
