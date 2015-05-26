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
import pl.psnc.synat.wrdz.zu.entity.permission.GroupManagementPermission;
import pl.psnc.synat.wrdz.zu.types.ManagementPermissionType;

/**
 * Specified set of filters for queries concerning {@link GroupManagementPermission} entities.
 */
public interface GroupManagementPermissionFilterFactory extends GenericQueryFilterFactory<GroupManagementPermission> {

    /**
     * Filters group management permissions by the groups granted with that permission.
     * 
     * @param groups
     *            list of groups ids.
     * @return constructed query filter.
     */
    QueryFilter<GroupManagementPermission> byGroupIn(List<Long> groups);


    /**
     * Filters group management permissions by the group's singleUser property.
     * 
     * @param singleUser
     *            whether the group owning the permissions must be a single-user or a standard one
     * @return constructed query filter.
     */
    QueryFilter<GroupManagementPermission> byGroupSingleUser(boolean singleUser);


    /**
     * Filters group management permissions by the type of permission granted.
     * 
     * @param permissionType
     *            type of permission.
     * @return constructed query filter.
     */
    QueryFilter<GroupManagementPermission> byPermissionType(ManagementPermissionType permissionType);


    /**
     * Filters group management permissions by the resource to which permissions are granted.
     * 
     * @param id
     *            resource id (primary key).
     * @return constructed query filter.
     */
    QueryFilter<GroupManagementPermission> byResourceId(Long id);


    /**
     * Filters group management permissions by the group granted with that permission.
     * 
     * @param id
     *            group id (primary key).
     * @return constructed query filter.
     */
    QueryFilter<GroupManagementPermission> byGroupId(Long id);

}
