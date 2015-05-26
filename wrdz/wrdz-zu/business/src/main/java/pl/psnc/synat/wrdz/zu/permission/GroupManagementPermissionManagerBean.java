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
package pl.psnc.synat.wrdz.zu.permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.zu.dao.permission.GroupManagementPermissionDao;
import pl.psnc.synat.wrdz.zu.dao.permission.GroupManagementPermissionFilterFactory;
import pl.psnc.synat.wrdz.zu.dao.user.GroupAuthenticationDao;
import pl.psnc.synat.wrdz.zu.dao.user.GroupAuthenticationFilterFactory;
import pl.psnc.synat.wrdz.zu.dao.user.UserDao;
import pl.psnc.synat.wrdz.zu.dao.user.UserFilterFactory;
import pl.psnc.synat.wrdz.zu.entity.permission.GroupManagementPermission;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;
import pl.psnc.synat.wrdz.zu.entity.user.User;
import pl.psnc.synat.wrdz.zu.exceptions.NotAuthorizedException;
import pl.psnc.synat.wrdz.zu.types.ManagementPermissionType;

/**
 * Permission manager for controlling access to groups management.
 */
@Stateless
public class GroupManagementPermissionManagerBean implements GroupManagementPermissionManager {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1810204486963092536L;
    /**
     * Groups management permissions DAO.
     */
    @EJB
    private GroupManagementPermissionDao groupManagementPermissionDao;

    /**
     * Groups DAO.
     */
    @EJB
    private GroupAuthenticationDao groupAuthenticationDao;

    /** User DAO. */
    @EJB
    private UserDao userDao;


    @Override
    public List<Long> fetchWithPermission(String username, ManagementPermissionType permission) {
        User user = getUser(username);
        if (user == null) {
            return Collections.emptyList();
        }

        List<GroupAuthentication> groups = user.getUserData().getGroups();
        List<Long> groupIds = new ArrayList<Long>();
        for (GroupAuthentication group : groups) {
            groupIds.add(group.getId());
        }
        return groupManagementPermissionDao.findGroupsWithPermission(groupIds, permission);
    }


    @SuppressWarnings("unchecked")
    @Override
    public boolean hasPermission(String username, Long resourceId, ManagementPermissionType permissionType) {
        if (username == null || permissionType == null) {
            throw new WrdzRuntimeException(
                    "Authorization check cannot be performed for null user or permissionType parameter.");
        }
        User user = getUser(username);
        if (user == null) {
            return false;
        } else if (user.isAdmin()) {
            return true;
        }
        List<GroupAuthentication> groups = user.getUserData().getGroups();
        List<Long> groupIds = new ArrayList<Long>();
        for (GroupAuthentication group : groups) {
            groupIds.add(group.getId());
        }
        GroupManagementPermissionFilterFactory filterFactory = groupManagementPermissionDao.createQueryModifier()
                .getQueryFilterFactory();
        QueryFilter<GroupManagementPermission> filter = filterFactory.and(filterFactory.byGroupIn(groupIds),
            filterFactory.byPermissionType(permissionType), filterFactory.byResourceId(resourceId));
        return groupManagementPermissionDao.findFirstResultBy(filter) != null;
    }


    @Override
    public void checkPermission(String username, Long resourceId, ManagementPermissionType permissionType)
            throws NotAuthorizedException {
        if (!hasPermission(username, resourceId, permissionType)) {
            throw new NotAuthorizedException("Permission check failed for [" + username + "; " + resourceId + "; "
                    + permissionType + "]");
        }
    }


    @Override
    public void setOwnerPermissions(String username, long resourceId) {
        setPermissions(username, true, resourceId,
            EnumSet.of(ManagementPermissionType.GRANT, ManagementPermissionType.READ, ManagementPermissionType.UPDATE));
    }


    @Override
    public Map<String, Set<ManagementPermissionType>> getUserPermissions(long resourceId) {
        return getPermissions(true, resourceId);
    }


    @Override
    public void setUserPermissions(String username, long resourceId, Set<ManagementPermissionType> permissions) {
        setPermissions(username, true, resourceId, permissions);
    }


    @Override
    public Map<String, Set<ManagementPermissionType>> getGroupPermissions(long resourceId) {
        return getPermissions(false, resourceId);
    }


    @Override
    public void setGroupPermissions(String groupname, long resourceId, Set<ManagementPermissionType> permissions) {
        setPermissions(groupname, false, resourceId, permissions);
    }


    /**
     * Retrieves a map of all group permissions to the given resource.
     * 
     * @param resourceId
     *            resource identifier
     * @param singleUser
     *            whether to check single-user or standard groups
     * @return all existing group permissions in the form of a <groupname, permissions> map
     */
    private Map<String, Set<ManagementPermissionType>> getPermissions(boolean singleUser, long resourceId) {

        GroupManagementPermissionFilterFactory permissionFilterFactory = groupManagementPermissionDao
                .createQueryModifier().getQueryFilterFactory();
        List<GroupManagementPermission> permissions = groupManagementPermissionDao.findBy(
            permissionFilterFactory.and(permissionFilterFactory.byGroupSingleUser(singleUser),
                permissionFilterFactory.byResourceId(resourceId)), true);

        Map<String, Set<ManagementPermissionType>> result = new HashMap<String, Set<ManagementPermissionType>>();
        for (GroupManagementPermission permission : permissions) {
            String groupname = permission.getGroup().getGroupname();
            if (!result.containsKey(groupname)) {
                result.put(groupname, EnumSet.noneOf(ManagementPermissionType.class));
            }
            result.get(groupname).add(permission.getPermission());
        }

        return result;
    }


    /**
     * Changes the permissions to the given resource for the group with the given groupname.
     * 
     * Permissions not present in the given set are be removed if they have been set before.
     * 
     * @param groupname
     *            name of the group
     * @param singleUser
     *            whether the group is a single user (user-specific) group or a normal one
     * @param resourceId
     *            resource identifier
     * @param permissions
     *            desired permissions
     */
    private void setPermissions(String groupname, boolean singleUser, Long resourceId,
            Set<ManagementPermissionType> permissions) {

        GroupAuthenticationFilterFactory filterFactory = groupAuthenticationDao.createQueryModifier()
                .getQueryFilterFactory();
        GroupAuthentication group = groupAuthenticationDao.findFirstResultBy(filterFactory.and(
            filterFactory.byGroupName(groupname), filterFactory.bySingleUser(singleUser)));
        if (group == null) {
            return;
        }

        GroupManagementPermissionFilterFactory permissionFilterFactory = groupManagementPermissionDao
                .createQueryModifier().getQueryFilterFactory();
        List<GroupManagementPermission> existing = groupManagementPermissionDao.findBy(permissionFilterFactory.and(
            permissionFilterFactory.byGroupIn(Collections.singletonList(group.getId())),
            permissionFilterFactory.byResourceId(resourceId)), true);

        for (GroupManagementPermission permission : existing) {
            if (!permissions.contains(permission.getPermission())) {
                groupManagementPermissionDao.delete(permission);
            } else {
                permissions.remove(permission.getPermission());
            }
        }

        for (ManagementPermissionType permission : permissions) {
            GroupManagementPermission added = new GroupManagementPermission();
            added.setGroup(group);
            added.setPermission(permission);
            added.setResourceId(resourceId);
            groupManagementPermissionDao.persist(added);
        }
    }


    @Override
    public boolean getUserCreatePermission(String username) {
        GroupAuthentication group = getGroup(username, true);
        if (group == null) {
            return false;
        }
        GroupManagementPermissionFilterFactory filterFactory = groupManagementPermissionDao.createQueryModifier()
                .getQueryFilterFactory();
        QueryFilter<GroupManagementPermission> filter = filterFactory.and(filterFactory.byGroupId(group.getId()),
            filterFactory.byPermissionType(ManagementPermissionType.CREATE), filterFactory.byResourceId(null));
        return groupManagementPermissionDao.findFirstResultBy(filter) != null;
    }


    @Override
    public void setUserCreatePermission(String username, boolean value) {
        setPermissions(username, true, null,
            value ? EnumSet.of(ManagementPermissionType.CREATE) : EnumSet.noneOf(ManagementPermissionType.class));
    }


    @Override
    public boolean getGroupCreatePermission(String groupname) {
        GroupAuthentication group = getGroup(groupname, false);
        if (group == null) {
            return false;
        }
        GroupManagementPermissionFilterFactory filterFactory = groupManagementPermissionDao.createQueryModifier()
                .getQueryFilterFactory();
        QueryFilter<GroupManagementPermission> filter = filterFactory.and(filterFactory.byGroupId(group.getId()),
            filterFactory.byPermissionType(ManagementPermissionType.CREATE), filterFactory.byResourceId(null));
        return groupManagementPermissionDao.findFirstResultBy(filter) != null;
    }


    @Override
    public void setGroupCreatePermission(String groupname, boolean value) {
        setPermissions(groupname, false, null,
            value ? EnumSet.of(ManagementPermissionType.CREATE) : EnumSet.noneOf(ManagementPermissionType.class));
    }


    @Override
    public void removePermissions(long resourceId) {
        GroupManagementPermissionFilterFactory factory = groupManagementPermissionDao.createQueryModifier()
                .getQueryFilterFactory();
        List<GroupManagementPermission> permissions = groupManagementPermissionDao.findBy(
            factory.byResourceId(resourceId), true);
        for (GroupManagementPermission permission : permissions) {
            groupManagementPermissionDao.delete(permission);
        }
    }


    @Override
    public void removePermissionsForGroup(long groupId) {
        GroupManagementPermissionFilterFactory factory = groupManagementPermissionDao.createQueryModifier()
                .getQueryFilterFactory();
        List<GroupManagementPermission> permissions = groupManagementPermissionDao.findBy(factory.byGroupId(groupId),
            true);
        for (GroupManagementPermission permission : permissions) {
            groupManagementPermissionDao.delete(permission);
        }
    }


    /**
     * Removes the permission that allows the group or user to create group resources.
     * 
     * @param groupname
     *            name of the group
     * @param singleUser
     *            whether the group is a single user (user-specific) group or a normal one
     */
    private void removeCreatePermission(String groupname, boolean singleUser) {
        GroupAuthenticationFilterFactory filterFactory = groupAuthenticationDao.createQueryModifier()
                .getQueryFilterFactory();
        GroupAuthentication group = groupAuthenticationDao.findFirstResultBy(filterFactory.and(
            filterFactory.byGroupName(groupname), filterFactory.bySingleUser(singleUser)));
        if (group == null) {
            return;
        }
        GroupManagementPermissionFilterFactory factory = groupManagementPermissionDao.createQueryModifier()
                .getQueryFilterFactory();
        GroupManagementPermission permission = groupManagementPermissionDao.findFirstResultBy(factory.and(
            factory.byGroupId(group.getId()), factory.byPermissionType(ManagementPermissionType.CREATE)));
        if (permission != null) {
            groupManagementPermissionDao.delete(permission);
        }
    }


    /**
     * Finds the user with the given username.
     * 
     * @param username
     *            username
     * @return the user, or <code>null</code> if no such user exists
     */
    private User getUser(String username) {
        UserFilterFactory filterFactory = userDao.createQueryModifier().getQueryFilterFactory();
        QueryFilter<User> filter = filterFactory.byUsername(username);
        return userDao.findFirstResultBy(filter);
    }


    /**
     * Finds the group with the given groupname.
     * 
     * @param groupname
     *            groupname
     * @param singleUser
     *            whether the group is a single user (user-specific) group or a normal one
     * @return the group, or <code>null</code> if no such group exists
     */
    private GroupAuthentication getGroup(String groupname, boolean singleUser) {
        GroupAuthenticationFilterFactory filterFactory = groupAuthenticationDao.createQueryModifier()
                .getQueryFilterFactory();
        QueryFilter<GroupAuthentication> filter = filterFactory.and(filterFactory.byGroupName(groupname),
            filterFactory.bySingleUser(singleUser));
        return groupAuthenticationDao.findFirstResultBy(filter);
    }

}
