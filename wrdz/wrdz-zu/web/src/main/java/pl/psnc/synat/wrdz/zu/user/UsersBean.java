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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.zu.entity.user.User;

/**
 * Manages user listing.
 */
@ManagedBean
@ViewScoped
public class UsersBean {

    /** User manager. */
    @EJB
    private UserManager userManager;

    /** User browser. */
    @EJB
    private UserBrowser userBrowser;

    /** User context. */
    @EJB
    private UserContext userContext;

    /** User list. */
    private List<User> users;


    /**
     * Checks if the current user has admin access rights.
     */
    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (!userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
                HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance()
                        .getExternalContext().getResponse();
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                FacesContext.getCurrentInstance().responseComplete();
            }
        }
    }


    /**
     * Returns the cached user list.
     * 
     * @return the user list
     */
    public List<User> getUsers() {
        if (users == null) {
            refresh();
        }
        return users;
    }


    /**
     * Refreshes the cached user list.
     */
    public void refresh() {
        users = userManager.getUsers();
    }
}
