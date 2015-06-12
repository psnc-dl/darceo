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
package pl.psnc.synat.wrdz.ru.session;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import pl.psnc.synat.wrdz.ru.i18n.RuMessageUtils;

/**
 * Login bean.
 */
@ManagedBean
@RequestScoped
public class LoginBean {

    /** Session Bean. */
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    /** Username. */
    private String username;

    /** Password. */
    private String password;


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public SessionBean getSessionBean() {
        return sessionBean;
    }


    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }


    /**
     * Logs the user in.
     * 
     * @return navigation directive.
     */
    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try {
            request.login(username, password);
            FacesContext.getCurrentInstance().getExternalContext().getSession(true);
            sessionBean.setLoggedInUser();
        } catch (ServletException e) {
            FacesMessage message = new FacesMessage(RuMessageUtils.getMessage("login.unknown_username_or_password"));
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage("login-form:username", message);
            return "";
        }
        return "services/services.xhtml?faces-redirect=true";
    }


    /**
     * Log the user out.
     * 
     * @return navigation directive.
     */
    public String logout() {
        sessionBean.clearLoggedInUser();
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "logout";
    }
}
