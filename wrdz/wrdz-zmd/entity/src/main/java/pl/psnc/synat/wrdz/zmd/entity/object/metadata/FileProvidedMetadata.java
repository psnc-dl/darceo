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
package pl.psnc.synat.wrdz.zmd.entity.object.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;

/**
 * An entity representing file containing metadata provided by the user.
 */
@Entity
@DiscriminatorValue("FILES_PROVIDED")
public class FileProvidedMetadata extends MetadataFile {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 6493242071705481692L;

    /**
     * File for which metadata was provided. - it must be relation many to many since metadata for files are not under
     * the version control.
     */
    @ManyToMany(mappedBy = "providedMetadata", fetch = FetchType.LAZY)
    private List<DataFileVersion> providedFor = new ArrayList<DataFileVersion>();


    public List<DataFileVersion> getProvidedFor() {
        return providedFor;
    }


    public void setProvidedFor(List<DataFileVersion> providedFor) {
        this.providedFor = providedFor;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((filename == null) ? 0 : filename.hashCode());
        result = prime * result + ((repositoryFilepath == null) ? 0 : repositoryFilepath.hashCode());
        result = prime * result + ((objectFilepath == null) ? 0 : objectFilepath.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + (int) (size ^ (size >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((providedFor == null) ? 0 : providedFor.hashCode());
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
        if (!(obj instanceof FileProvidedMetadata)) {
            return false;
        }
        FileProvidedMetadata other = (FileProvidedMetadata) obj;
        if (id != other.id) {
            return false;
        }
        if (size != other.size) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        if (filename == null) {
            if (other.filename != null) {
                return false;
            }
        } else if (!filename.equals(other.filename)) {
            return false;
        }
        if (repositoryFilepath == null) {
            if (other.repositoryFilepath != null) {
                return false;
            }
        } else if (!repositoryFilepath.equals(other.repositoryFilepath)) {
            return false;
        }
        if (objectFilepath == null) {
            if (other.objectFilepath != null) {
                return false;
            }
        } else if (!objectFilepath.equals(other.objectFilepath)) {
            return false;
        }
        if (providedFor == null) {
            if (other.providedFor != null) {
                return false;
            }
        } else if (!providedFor.equals(other.providedFor)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("FileProvidedMetadata ");
        sb.append("[id = ").append(id);
        sb.append(", filename = ").append(filename);
        sb.append(", repositoryFilepath = ").append(repositoryFilepath);
        sb.append(", objectFilepath = ").append(objectFilepath);
        sb.append(", usedNamespaces = ").append(usedNamespaces);
        sb.append(", providedFor = ").append(providedFor);
        sb.append("]");
        return sb.toString();
    }

}
