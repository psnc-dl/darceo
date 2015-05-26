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
package pl.psnc.synat.wrdz.zu.dao.permission;

import java.util.List;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zu.entity.permission.ObjectPermission;
import pl.psnc.synat.wrdz.zu.types.ObjectPermissionType;

/**
 * Specified set of filters for queries concerning {@link ObjectPermission} entities.
 */
public interface ObjectPermissionFilterFactory extends GenericQueryFilterFactory<ObjectPermission> {

    /**
     * Filters object permissions by the groups granted with that permission.
     * 
     * @param groups
     *            list of groups ids.
     * @return constructed query filter.
     */
    QueryFilter<ObjectPermission> byGroupIn(List<Long> groups);


    /**
     * Filters object permissions by their group's singleUser property.
     * 
     * @param singleUser
     *            whether the group must be a single-user or a standard one
     * @return constructed query filter.
     */
    QueryFilter<ObjectPermission> byGroupSingleUser(boolean singleUser);


    /**
     * Filters object permissions by the type of permission granted.
     * 
     * @param permissionType
     *            type of permission.
     * @return constructed query filter.
     */
    QueryFilter<ObjectPermission> byPermissionType(ObjectPermissionType permissionType);


    /**
     * Filters object permissions by the resource to which permissions are granted.
     * 
     * @param id
     *            resource id (primary key).
     * @return constructed query filter.
     */
    QueryFilter<ObjectPermission> byResourceId(Long id);


    /**
     * Filters object permissions by the group granted with that permission.
     * 
     * @param id
     *            group id (primary key).
     * @return constructed query filter.
     */
    QueryFilter<ObjectPermission> byGroupId(Long id);

}
