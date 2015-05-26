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

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import pl.psnc.synat.wrdz.zu.dao.user.GroupAuthenticationDao;
import pl.psnc.synat.wrdz.zu.dao.user.UserAuthenticationDao;
import pl.psnc.synat.wrdz.zu.dao.user.UserAuthenticationFilterFactory;
import pl.psnc.synat.wrdz.zu.dao.user.UserDao;
import pl.psnc.synat.wrdz.zu.dao.user.UserFilterFactory;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;
import pl.psnc.synat.wrdz.zu.entity.user.User;
import pl.psnc.synat.wrdz.zu.entity.user.UserAuthentication;
import pl.psnc.synat.wrdz.zu.exceptions.NoSuchUserException;

/**
 * Default implementation of group assignment manager.
 */
@Stateless
public class GroupAssignmentManagerBean implements GroupAssignmentManager {

    /** User DAO. */
    @EJB
    private UserDao userDao;

    /** User authentication DAO. */
    @EJB
    private UserAuthenticationDao userAuthDao;

    /** Group authentication DAO. */
    @EJB
    private GroupAuthenticationDao groupAuthDao;


    @Override
    public List<UserAuthentication> getUsers(long groupId) {

        GroupAuthentication group = groupAuthDao.findById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("No such group: " + groupId);
        }
        if (group.isSingleUser()) {
            throw new IllegalArgumentException("Cannot list users for single-user groups.");
        }

        UserAuthenticationFilterFactory factory = userAuthDao.createQueryModifier().getQueryFilterFactory();
        return userAuthDao.findBy(factory.byGroup(group), true);
    }


    @Override
    public void addUser(String username, long groupId)
            throws NoSuchUserException {

        GroupAuthentication group = groupAuthDao.findById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("No such group: " + groupId);
        }
        if (group.isSingleUser()) {
            throw new IllegalArgumentException("Cannot add users to single-user groups.");
        }

        UserFilterFactory factory = userDao.createQueryModifier().getQueryFilterFactory();
        User user = userDao.findFirstResultBy(factory.byUsername(username));
        if (user != null) {
            if (!user.getUserData().getGroups().contains(group)) {
                user.getUserData().getGroups().add(group);
            }
        } else {
            throw new NoSuchUserException("No such user: " + username);
        }
    }


    @Override
    public void removeUser(String username, long groupId)
            throws NoSuchUserException {

        GroupAuthentication group = groupAuthDao.findById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("No such group: " + groupId);
        }
        if (group.isSingleUser()) {
            throw new IllegalArgumentException("Cannot remove users from single-user groups.");
        }

        UserFilterFactory factory = userDao.createQueryModifier().getQueryFilterFactory();
        User user = userDao.findFirstResultBy(factory.byUsername(username));
        if (user != null) {
            if (user.getUserData().getGroups().contains(group)) {
                user.getUserData().getGroups().remove(group);
            }
        } else {
            throw new NoSuchUserException("No such user: " + username);
        }
    }
}
