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
package pl.psnc.synat.wrdz.common.web.exception;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

/**
 * Bean responsible for exception view handling.
 */
@ManagedBean
@ViewScoped
public class ExceptionBean {

    /**
     * Key of the servlet exception entry.
     */
    private static final String SERVLET_EXCEPTION_KEY = "javax.servlet.error.exception";

    /**
     * Handled exception.
     */
    private Throwable exception;


    /**
     * Initializes exception by fetching it from the faces context.
     */
    public void initializeException() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        if (ctx.getExternalContext().getRequestMap().containsKey(SERVLET_EXCEPTION_KEY)) {
            this.exception = (Throwable) ctx.getExternalContext().getRequestMap().remove(SERVLET_EXCEPTION_KEY);
        } else if (ctx.getExternalContext().getSessionMap().containsKey(SERVLET_EXCEPTION_KEY)) {
            this.exception = (Throwable) ctx.getExternalContext().getSessionMap().remove(SERVLET_EXCEPTION_KEY);
        }
    }


    public String getMessage() {
        return exception.getLocalizedMessage();
    }

}
