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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zu.dao.user.UserDao;
import pl.psnc.synat.wrdz.zu.dao.user.UserFilterFactory;
import pl.psnc.synat.wrdz.zu.dto.user.OrganizationDto;
import pl.psnc.synat.wrdz.zu.dto.user.UserDto;
import pl.psnc.synat.wrdz.zu.entity.user.Organization;
import pl.psnc.synat.wrdz.zu.entity.user.User;

/**
 * Default implementation of {@link UserBrowser}.
 */
@Stateless
public class UserBrowserBean implements UserBrowser {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(UserBrowserBean.class);

    /** User DAO. */
    @EJB
    private UserDao userDao;


    @Override
    public List<UserDto> getUsers() {
        List<UserDto> results = new ArrayList<UserDto>();
        for (User user : userDao.findAll()) {
            results.add(new UserDto(user.getId(), user.getUsername(), user.getHomeDir()));
        }
        return results;
    }


    @Override
    public UserDto getUser(String username) {
        UserFilterFactory filterFactory = userDao.createQueryModifier().getQueryFilterFactory();
        QueryFilter<User> filter = filterFactory.byUsername(username);
        User user = userDao.findFirstResultBy(filter);
        if (user != null) {
            return new UserDto(user.getId(), user.getUsername(), user.getHomeDir());
        }
        return null;
    }


    @Override
    public UserDto getUser(long id) {
        User user = userDao.findById(id);
        if (user != null) {
            return new UserDto(user.getId(), user.getUsername(), user.getHomeDir());
        }
        return null;
    }


    @Override
    public Long getUserId(String username) {
        UserDto user = getUser(username);
        if (user != null) {
            return user.getId();
        }
        return null;
    }


    @Override
    public OrganizationDto getOrganization(String username) {
        UserFilterFactory filterFactory = userDao.createQueryModifier().getQueryFilterFactory();
        QueryFilter<User> filter = filterFactory.byUsername(username);
        User user = userDao.findFirstResultBy(filter);
        if (user != null) {
            Organization org = user.getOrganization();
            return new OrganizationDto(org.getId(), org.getName(), org.getRootPath());
        }
        return null;
    }


    @Override
    public boolean isAdmin(String username) {
        UserFilterFactory filterFactory = userDao.createQueryModifier().getQueryFilterFactory();
        QueryFilter<User> filter = filterFactory.byUsername(username);
        User user = userDao.findFirstResultBy(filter);
        if (user != null) {
            return user.isAdmin();
        }
        return false;
    }


    @Override
    public String getEmail(String username) {
        UserFilterFactory filterFactory = userDao.createQueryModifier().getQueryFilterFactory();
        QueryFilter<User> filter = filterFactory.byUsername(username);
        User user = userDao.findFirstResultBy(filter);
        if (user != null && user.getUserData() != null) {
            return user.getUserData().getEmail();
        }
        return null;
    }
}
