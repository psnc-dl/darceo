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
package pl.psnc.synat.wrdz.zu.user;

import java.util.EnumSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.zu.dao.user.GroupAuthenticationDao;
import pl.psnc.synat.wrdz.zu.dao.user.GroupAuthenticationFilterFactory;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;
import pl.psnc.synat.wrdz.zu.entity.user.UserAuthentication;
import pl.psnc.synat.wrdz.zu.exceptions.NameExistsException;
import pl.psnc.synat.wrdz.zu.permission.GroupManagementPermissionManager;
import pl.psnc.synat.wrdz.zu.permission.ObjectPermissionManager;
import pl.psnc.synat.wrdz.zu.types.ManagementPermissionType;

/**
 * Default implementation of group manager.
 */
@Stateless
public class GroupManagerBean implements GroupManager {

    /** Group authentication DAO. */
    @EJB
    private GroupAuthenticationDao groupAuthDao;

    /** Group assignment manager. */
    @EJB
    private GroupAssignmentManager groupAssignmentManager;

    /** Group permission manager. */
    @EJB
    private GroupManagementPermissionManager groupPermissionManager;

    /** Object permission manager. */
    @EJB
    private ObjectPermissionManager objectPermissionManager;

    /** User context. */
    @EJB
    private UserContext userContext;


    @Override
    public GroupAuthentication getGroup(long id) {
        return groupAuthDao.findById(id);
    }


    @Override
    public List<GroupAuthentication> getGroups(List<Long> ids) {
        GroupAuthenticationFilterFactory factory = groupAuthDao.createQueryModifier().getQueryFilterFactory();
        return groupAuthDao.findBy(factory.byIdentifiers(ids), true);
    }


    @Override
    public List<GroupAuthentication> getPublicGroups() {
        GroupAuthenticationFilterFactory factory = groupAuthDao.createQueryModifier().getQueryFilterFactory();
        return groupAuthDao.findBy(factory.bySingleUser(false), true);
    }


    @Override
    public List<GroupAuthentication> getPrivateGroups() {
        GroupAuthenticationFilterFactory factory = groupAuthDao.createQueryModifier().getQueryFilterFactory();
        return groupAuthDao.findBy(factory.bySingleUser(true), true);
    }


    @Override
    public GroupAuthentication createGroup(GroupAuthentication group, boolean groupsCreatable, boolean objectsCreatable)
            throws NameExistsException {

        GroupAuthenticationFilterFactory factory = groupAuthDao.createQueryModifier().getQueryFilterFactory();
        if (groupAuthDao.countBy(factory.and(factory.byGroupName(group.getGroupname()), factory.bySingleUser(false))) > 0) {
            throw new NameExistsException("Group with the name " + group.getGroupname() + " already exists");
        }

        group.setSingleUser(false);
        groupAuthDao.persist(group);

        groupPermissionManager.setUserPermissions(userContext.getCallerPrincipalName(), group.getId(),
            EnumSet.of(ManagementPermissionType.GRANT, ManagementPermissionType.READ, ManagementPermissionType.UPDATE));

        if (groupsCreatable) {
            groupPermissionManager.setGroupCreatePermission(group.getGroupname(), true);
        }
        if (objectsCreatable) {
            objectPermissionManager.setGroupCreatePermission(group.getGroupname(), true);
        }
        return group;
    }


    @Override
    public GroupAuthentication modifyGroup(GroupAuthentication modified, boolean groupsCreatable,
            boolean objectsCreatable)
            throws NameExistsException {

        GroupAuthentication current = groupAuthDao.findById(modified.getId());
        if (current == null) {
            throw new IllegalArgumentException("No such group: " + modified.getId());
        }
        if (current.isSingleUser()) {
            throw new IllegalArgumentException("Cannot modify single-user groups.");
        }

        if (!modified.getGroupname().equals(current.getGroupname())) {
            GroupAuthenticationFilterFactory factory = groupAuthDao.createQueryModifier().getQueryFilterFactory();
            if (groupAuthDao.countBy(factory.and(factory.byGroupName(modified.getGroupname()),
                factory.bySingleUser(false))) > 0) {
                throw new NameExistsException("Group with the name " + modified.getGroupname() + " already exists");
            }

            current.setGroupname(modified.getGroupname());
        }

        groupAuthDao.flush();

        groupPermissionManager.setGroupCreatePermission(modified.getGroupname(), groupsCreatable);
        objectPermissionManager.setGroupCreatePermission(modified.getGroupname(), objectsCreatable);

        groupAuthDao.flush();

        return current;
    }


    @Override
    public void deleteGroup(long groupId) {

        GroupAuthentication group = groupAuthDao.findById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("No such group: " + groupId);
        }
        if (group.isSingleUser()) {
            throw new IllegalArgumentException("Cannot delete single-user groups.");
        }

        for (UserAuthentication user : groupAssignmentManager.getUsers(groupId)) {
            user.getGroups().remove(group);
        }

        groupPermissionManager.removePermissions(group.getId());

        groupPermissionManager.removePermissionsForGroup(group.getId());
        objectPermissionManager.removePermissionsForGroup(group.getId());

        groupAuthDao.delete(group);
    }
}
