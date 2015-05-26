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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;

/**
 * An entity representing file containing metadata extracted automatically by the system.
 */
@Entity
@DiscriminatorValue("OBJECTS_EXTRACTED")
public class ObjectExtractedMetadata extends MetadataFile {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 7890352935343606940L;

    /**
     * Files being the source from which metadata was extracted - it must be relation many to many since metadata for
     * files are not under the version control.
     */
    @OneToOne(mappedBy = "extractedMetadata", fetch = FetchType.LAZY)
    private ContentVersion extractedFrom;


    public ContentVersion getExtractedFrom() {
        return extractedFrom;
    }


    public void setExtractedFrom(ContentVersion extractedFrom) {
        this.extractedFrom = extractedFrom;
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
        if (!(obj instanceof ObjectExtractedMetadata)) {
            return false;
        }
        ObjectExtractedMetadata other = (ObjectExtractedMetadata) obj;
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
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("ObjectExtractedMetadata ");
        sb.append("[id = ").append(id);
        sb.append(", filename = ").append(filename);
        sb.append(", repositoryFilepath = ").append(repositoryFilepath);
        sb.append(", objectFilepath = ").append(objectFilepath);
        sb.append(", usedNamespaces = ").append(usedNamespaces);
        sb.append("]");
        return sb.toString();
    }

}
