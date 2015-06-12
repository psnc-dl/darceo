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
 * Types of permissions granted on digital objects.
 */
public enum ObjectPermissionType implements PermissionType {

    /**
     * Permission to create new digital object.
     */
    CREATE,

    /**
     * Permission to read certain digital object through OAI-PMH interface.
     */
    OAI_PMH_READ,

    /**
     * Permission to read certain digital object.
     */
    READ(OAI_PMH_READ),

    /**
     * Permission to modify certain digital object's metadata (migrations, identifiers etc.).
     */
    METADATA_UPDATE(OAI_PMH_READ, READ),

    /**
     * Permission to update certain digital object.
     */
    UPDATE(OAI_PMH_READ, READ, METADATA_UPDATE),

    /**
     * Permission to delete certain digital object.
     */
    DELETE(OAI_PMH_READ, READ, METADATA_UPDATE, UPDATE),

    /**
     * Permission to grant permissions to certain digital object.
     */
    GRANT(OAI_PMH_READ, READ, METADATA_UPDATE, UPDATE, DELETE);

    /** Implied permissions. */
    private final Set<ObjectPermissionType> impliedPermissions;


    /**
     * Constructor.
     * 
     * @param impliedPermissions
     *            permissions implied by the permission
     */
    private ObjectPermissionType(ObjectPermissionType... impliedPermissions) {
        Set<ObjectPermissionType> implied = new HashSet<ObjectPermissionType>();
        for (ObjectPermissionType type : impliedPermissions) {
            implied.add(type);
        }
        this.impliedPermissions = Collections.unmodifiableSet(implied);
    }


    public Set<ObjectPermissionType> getImpliedPermissions() {
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
    public static void addImpliedPermissions(Set<ObjectPermissionType> permissions) {
        EnumSet<ObjectPermissionType> implied = EnumSet.noneOf(ObjectPermissionType.class);
        for (ObjectPermissionType type : permissions) {
            implied.addAll(type.getImpliedPermissions());
        }
        permissions.addAll(implied);
    }
}
