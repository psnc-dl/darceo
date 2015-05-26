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
package pl.psnc.synat.wrdz.zu.entity.permission;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import pl.psnc.synat.wrdz.zu.types.ObjectPermissionType;

/**
 * Represents permissions on a specific digital object granted to specific user group.
 */
@Entity
@DiscriminatorValue("OBJECT")
public class ObjectPermission extends ResourcePermission {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 3751469197864650014L;

    /**
     * Permission granted.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "GP_PERMISSION", length = 15, nullable = false)
    private ObjectPermissionType permission;


    public ObjectPermissionType getPermission() {
        return permission;
    }


    public void setPermission(ObjectPermissionType permission) {
        this.permission = permission;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((group == null) ? 0 : group.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((permission == null) ? 0 : permission.hashCode());
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
        result = prime * result + ((resourceType == null) ? 0 : resourceType.hashCode());
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
        if (!(obj instanceof ObjectPermission)) {
            return false;
        }
        ObjectPermission other = (ObjectPermission) obj;
        if (group == null) {
            if (other.getGroup() != null) {
                return false;
            }
        } else if (!group.equals(other.getGroup())) {
            return false;
        }
        if (id != other.getId()) {
            return false;
        }
        if (permission == null) {
            if (other.getPermission() != null) {
                return false;
            }
        } else if (!permission.equals(other.getPermission())) {
            return false;
        }
        if (resourceId == null) {
            if (other.getResourceId() != null) {
                return false;
            }
        } else if (!resourceId.equals(other.getResourceId())) {
            return false;
        }
        if (resourceType != other.getResourceType()) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ResourcePermission [id=").append(id).append(", group=").append(group).append(", resourceId=")
                .append(resourceId).append(", resourceType=").append(resourceType).append(", permission=")
                .append(permission).append("]");
        return builder.toString();
    }

}
