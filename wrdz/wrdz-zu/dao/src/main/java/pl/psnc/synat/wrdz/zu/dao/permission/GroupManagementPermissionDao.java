﻿/**
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

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDao;
import pl.psnc.synat.wrdz.zu.entity.permission.GroupManagementPermission;
import pl.psnc.synat.wrdz.zu.types.ManagementPermissionType;

/**
 * An interface for a class managing the persistence of {@link GroupManagementPermission} class. It declares additional
 * operations available for {@link GroupManagementPermission} object apart from basic contract defined in
 * {@link ExtendedGenericDao}.
 */
@Local
public interface GroupManagementPermissionDao
        extends
        ExtendedGenericDao<GroupManagementPermissionFilterFactory, GroupManagementPermissionSorterBuilder, GroupManagementPermission, Long> {

    /**
     * Finds list of ids (primary keys) of groups that have specified permission granted to either of the groups passed
     * in the parameter.
     * 
     * @param groups
     *            list of user groups identifiers.
     * @param permissionType
     *            type of permission to be granted on given group.
     * @return list of ids (primary keys) of groups.
     */
    List<Long> findGroupsWithPermission(List<Long> groups, ManagementPermissionType permissionType);

}
