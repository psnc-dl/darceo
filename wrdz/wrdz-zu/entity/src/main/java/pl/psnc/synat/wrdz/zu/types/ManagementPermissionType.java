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
package pl.psnc.synat.wrdz.zu.types;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * Types of permissions granted on user management, i.e. users and groups.
 */
public enum ManagementPermissionType implements PermissionType {

    /**
     * Permission to add new group.
     */
    CREATE,

    /**
     * Permission to read data and metadata about certain group.
     */
    READ,

    /**
     * Permission to update and delete the group.
     */
    UPDATE(READ),

    /**
     * Permission to grant permissions to manage groups.
     */
    GRANT(READ, UPDATE);

    /** Implied permissions. */
    private final Set<ManagementPermissionType> impliedPermissions;


    /**
     * Constructor.
     * 
     * @param impliedPermissions
     *            permissions implied by the permission
     */
    private ManagementPermissionType(ManagementPermissionType... impliedPermissions) {
        Set<ManagementPermissionType> implied = new HashSet<ManagementPermissionType>();
        for (ManagementPermissionType type : impliedPermissions) {
            implied.add(type);
        }
        this.impliedPermissions = Collections.unmodifiableSet(implied);
    }


    public Set<ManagementPermissionType> getImpliedPermissions() {
        return impliedPermissions;
    }


    @Override
    public String getPermissionType() {
        return name();
    }


    /**
     * Adds missing implied permissions to the given permission set.
     * 
     * @param permissions
     *            set to be updated
     */
    public static void addImpliedPermissions(Set<ManagementPermissionType> permissions) {
        EnumSet<ManagementPermissionType> implied = EnumSet.noneOf(ManagementPermissionType.class);
        for (ManagementPermissionType type : permissions) {
            implied.addAll(type.getImpliedPermissions());
        }
        permissions.addAll(implied);
    }
}
