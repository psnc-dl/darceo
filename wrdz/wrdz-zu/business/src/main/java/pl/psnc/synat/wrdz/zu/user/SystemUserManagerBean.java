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

import javax.ejb.EJB;
import javax.ejb.Stateless;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.zu.dao.user.OrganizationDao;
import pl.psnc.synat.wrdz.zu.dao.user.OrganizationFilterFactory;
import pl.psnc.synat.wrdz.zu.dao.user.UserAuthenticationDao;
import pl.psnc.synat.wrdz.zu.dao.user.UserDao;
import pl.psnc.synat.wrdz.zu.dao.user.UserFilterFactory;
import pl.psnc.synat.wrdz.zu.entity.user.GroupAuthentication;
import pl.psnc.synat.wrdz.zu.entity.user.Organization;
import pl.psnc.synat.wrdz.zu.entity.user.User;
import pl.psnc.synat.wrdz.zu.entity.user.UserAuthentication;

/**
 * Default implementation of system user manager.
 */
@Stateless
public class SystemUserManagerBean implements SystemUserManager {

    /** OrganizationDAO. */
    @EJB
    private OrganizationDao organizationDao;

    /** User DAO. */
    @EJB
    private UserDao userDao;

    /** User authentication DAO. */
    @EJB
    private UserAuthenticationDao userAuthDao;


    @Override
    public long createSystemUser(String name, String certificate, String displayName, String organizationName) {

        OrganizationFilterFactory filterFactory = organizationDao.createQueryModifier().getQueryFilterFactory();
        Organization organization = organizationDao.findFirstResultBy(filterFactory.byName(organizationName));
        if (organization == null) {
            organization = new Organization();
            organization.setName(organizationName);
            organization.setRootPath("/");
            organizationDao.persist(organization);
        }

        User user = new User();
        user.setHomeDir("/");
        user.setUsername(name);
        user.setOrganization(organization);
        userDao.persist(user);

        organization.getUsers().add(user);

        GroupAuthentication groupAuth = new GroupAuthentication();
        groupAuth.setGroupname(user.getUsername());
        groupAuth.setSingleUser(true);

        UserAuthentication userAuth = new UserAuthentication();
        userAuth.setPrimaryGroup(groupAuth);
        userAuth.getGroups().add(groupAuth);
        userAuth.setActive(true);
        userAuth.setDisplayName(displayName);
        userAuth.setCertificate(certificate);
        userAuth.setUser(user);
        user.setUserData(userAuth);
        userAuthDao.persist(userAuth);

        userDao.flush();

        return user.getId();
    }


    @Override
    public void updateSystemUser(String name, String certificate) {

        UserFilterFactory filterFactory = userDao.createQueryModifier().getQueryFilterFactory();
        User user = userDao.findFirstResultBy(filterFactory.byUsername(name));
        if (user != null) {
            user.getUserData().setCertificate(certificate);
        } else {
            throw new WrdzRuntimeException("User not found: " + name);
        }
    }


    @Override
    public void deleteSystemUser(String name) {

        UserFilterFactory filterFactory = userDao.createQueryModifier().getQueryFilterFactory();
        User user = userDao.findFirstResultBy(filterFactory.byUsername(name));
        if (user != null) {
            userDao.delete(user);
        } else {
            throw new WrdzRuntimeException("User not found: " + name);
        }
    }


    @Override
    public String getCertificate(String name) {

        UserFilterFactory filterFactory = userDao.createQueryModifier().getQueryFilterFactory();
        User user = userDao.findFirstResultBy(filterFactory.byUsername(name));
        if (user != null) {
            return user.getUserData().getCertificate();
        }

        return null;
    }

}
