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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;
import pl.psnc.synat.wrdz.zu.types.ResourceType;

/**
 * Entity representing group's permissionType granted on the resource. Resources are expressed as a pair of attributes -
 * resource identifier and resource type. This allows for precise resource identification.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "GP_RESOURCE_TYPE", discriminatorType = DiscriminatorType.STRING)
@Table(name = "ZU_GROUP_PERMISSIONS", schema = "darceo")
public abstract class ResourcePermission implements Serializable {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = -5903173638293132235L;

    /**
     * Entity's identifier (primary key).
     */
    @Id
    @SequenceGenerator(name = "groupPermissionSequenceGenerator", sequenceName = "darceo.ZU_GP_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "groupPermissionSequenceGenerator")
    @Column(name = "GP_ID", unique = true, nullable = false)
    protected long id;

    /**
     * Group for which the permissionType is granted.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "GP_AG_ID", nullable = false)
    protected GroupAuthentication group;

    /**
     * Identifier of the resource for which the permissionType is granted.
     */
    @Column(name = "GP_RESOURCE_ID", nullable = false)
    protected Long resourceId;

    /**
     * Type of the resource for which the permissionType is granted.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "gp_resource_type", length = 10, nullable = false)
    protected ResourceType resourceType;

    /**
     * String expression representing granted permissionType type.
     */
    @Column(name = "GP_PERMISSION", length = 15, nullable = false, insertable = false, updatable = false)
    private String permissionType;


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public GroupAuthentication getGroup() {
        return group;
    }


    public void setGroup(GroupAuthentication group) {
        this.group = group;
    }


    public Long getResourceId() {
        return resourceId;
    }


    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }


    public ResourceType getResourceType() {
        return resourceType;
    }


    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }


    public String getPermissionType() {
        return permissionType;
    }


    public void setPermissionType(String permissionType) {
        this.permissionType = permissionType;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((group == null) ? 0 : group.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((permissionType == null) ? 0 : permissionType.hashCode());
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
        if (!(obj instanceof ResourcePermission)) {
            return false;
        }
        ResourcePermission other = (ResourcePermission) obj;
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
        if (permissionType == null) {
            if (other.getPermissionType() != null) {
                return false;
            }
        } else if (!permissionType.equals(other.getPermissionType())) {
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
                .append(resourceId).append(", resourceType=").append(resourceType).append(", permissionType=")
                .append(permissionType).append("]");
        return builder.toString();
    }

}
