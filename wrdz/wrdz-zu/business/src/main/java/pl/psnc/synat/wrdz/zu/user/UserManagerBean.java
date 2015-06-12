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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import pl.psnc.synat.wrdz.zu.dao.user.GroupAuthenticationDao;
import pl.psnc.synat.wrdz.zu.dao.user.GroupAuthenticationFilterFactory;
import pl.psnc.synat.wrdz.zu.dao.user.OrganizationDao;
import pl.psnc.synat.wrdz.zu.dao.user.UserDao;
import pl.psnc.synat.wrdz.zu.dao.user.UserFilterFactory;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;
import pl.psnc.synat.wrdz.zu.entity.user.Organization;
import pl.psnc.synat.wrdz.zu.entity.user.User;
import pl.psnc.synat.wrdz.zu.entity.user.UserAuthentication;
import pl.psnc.synat.wrdz.zu.exceptions.NameExistsException;
import pl.psnc.synat.wrdz.zu.permission.GroupManagementPermissionManager;
import pl.psnc.synat.wrdz.zu.permission.ObjectPermissionManager;

/**
 * Default implementation of user manager.
 */
@Stateless
public class UserManagerBean implements UserManager {

    /** The name of the auth-users group. */
    private static final String AUTH_USERS = "auth-users";

    /** OrganizationDAO. */
    @EJB
    private OrganizationDao organizationDao;

    /** User DAO. */
    @EJB
    private UserDao userDao;

    /** Group DAO. */
    @EJB
    private GroupAuthenticationDao groupDao;

    /** Group permission manager. */
    @EJB
    private GroupManagementPermissionManager groupPermissionManager;

    /** Object permission manager. */
    @EJB
    private ObjectPermissionManager objectPermissionManager;


    @Override
    public User getUser(long id) {
        return userDao.findById(id);
    }


    @Override
    public List<User> getUsers() {
        return userDao.findAll();
    }


    @Override
    public User createUser(User user, boolean groupsCreatable, boolean objectsCreatable)
            throws NameExistsException {

        Organization organization = organizationDao.findById(user.getOrganization().getId());
        if (organization == null) {
            throw new IllegalArgumentException("No such organization: " + user.getOrganization().getId());
        }

        UserFilterFactory factory = userDao.createQueryModifier().getQueryFilterFactory();
        if (userDao.countBy(factory.byUsername(user.getUsername())) > 0) {
            throw new NameExistsException("User with the name " + user.getUsername() + " already exists");
        }

        user.setOrganization(organization);

        List<GroupAuthentication> groups = new ArrayList<GroupAuthentication>();

        GroupAuthentication groupAuth = new GroupAuthentication();
        groupAuth.setGroupname(user.getUsername());
        groupAuth.setSingleUser(true);
        groups.add(groupAuth);

        GroupAuthenticationFilterFactory groupFilterFactory = groupDao.createQueryModifier().getQueryFilterFactory();
        @SuppressWarnings("unchecked")
        GroupAuthentication authUsers = groupDao.findSingleResultBy(groupFilterFactory.and(
            groupFilterFactory.byGroupName(AUTH_USERS), groupFilterFactory.bySingleUser(false)));
        groups.add(authUsers);

        user.getUserData().setPrimaryGroup(groupAuth);
        user.getUserData().setGroups(groups);

        userDao.persist(user);

        if (groupsCreatable) {
            groupPermissionManager.setUserCreatePermission(user.getUsername(), true);
        }
        if (objectsCreatable) {
            objectPermissionManager.setUserCreatePermission(user.getUsername(), true);
        }

        userDao.flush();

        return user;
    }


    @Override
    public User modifyUser(User modified, boolean groupsCreatable, boolean objectsCreatable)
            throws NameExistsException {

        User current = userDao.findById(modified.getId());
        if (current == null) {
            throw new IllegalArgumentException("No such user: " + modified.getId());
        }

        Organization organization = organizationDao.findById(modified.getOrganization().getId());
        if (organization == null) {
            throw new IllegalArgumentException("No such organization: " + modified.getOrganization().getId());
        }

        if (!current.getUsername().equals(modified.getUsername())) {

            UserFilterFactory factory = userDao.createQueryModifier().getQueryFilterFactory();
            if (userDao.countBy(factory.byUsername(modified.getUsername())) > 0) {
                throw new NameExistsException("User with the name " + modified.getUsername() + " already exists");
            }

            current.setUsername(modified.getUsername());
            current.getUserData().getPrimaryGroup().setGroupname(current.getUsername());
        }

        current.setAdmin(modified.isAdmin());
        current.setHomeDir(modified.getHomeDir());
        current.setOrganization(organization);

        userDao.flush();

        groupPermissionManager.setUserCreatePermission(modified.getUsername(), groupsCreatable);
        objectPermissionManager.setUserCreatePermission(modified.getUsername(), objectsCreatable);

        UserAuthentication currentAuth = current.getUserData();
        UserAuthentication modifiedAuth = modified.getUserData();

        currentAuth.setActive(modifiedAuth.getActive());
        currentAuth.setCertificate(modifiedAuth.getCertificate());
        currentAuth.setDisplayName(modifiedAuth.getDisplayName());
        currentAuth.setEmail(modifiedAuth.getEmail());
        currentAuth.setFirstName(modifiedAuth.getFirstName());
        currentAuth.setLastName(modifiedAuth.getLastName());
        currentAuth.setMiddleInitial(modifiedAuth.getMiddleInitial());
        currentAuth.setPasswordHash(modifiedAuth.getPasswordHash());
        currentAuth.setPasswordSalt(modifiedAuth.getPasswordSalt());

        userDao.flush();

        return current;
    }

}
