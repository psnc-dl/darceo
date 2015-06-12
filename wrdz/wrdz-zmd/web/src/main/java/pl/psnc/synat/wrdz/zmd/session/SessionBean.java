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
package pl.psnc.synat.wrdz.zmd.session;

import java.io.Serializable;
import java.security.Principal;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import pl.psnc.synat.wrdz.zu.dto.user.UserDto;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Session bean of ZMKD.
 */
@ManagedBean
@SessionScoped
public class SessionBean implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -4297179484406193778L;

    /**
     * User Browser bean.
     */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;

    /**
     * Logged in user.
     */
    private UserDto user;


    /**
     ** Sets a user field by username of logged in user.
     */
    public void setLoggedInUser() {
        Principal principal = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal();
        if (principal != null) {
            user = userBrowser.getUser(principal.getName());
        }
    }


    /**
     * Gets logged in user.
     * 
     * @return logged in user DTO
     */
    public UserDto getLoggedInUser() {
        return user;
    }


    /**
     * Returns true if some user is logged in.
     * 
     * @return whether some user is logged in.
     */
    public boolean isUserLoggedIn() {
        return user != null;
    }


    /**
     * Clears logged in user.
     */
    public void clearLoggedInUser() {
        user = null;
    }

}
