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
package pl.psnc.synat.wrdz.common.user;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.DomainCombiner;
import java.security.Principal;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.security.auth.SubjectDomainCombiner;

/**
 * Provides basic information about current user.
 */
@Stateless
public class UserContextBean implements UserContext {

    /**
     * Name of the anonymous principal.
     */
    private static final String ANONYMOUS = "anonymous";

    /**
     * Session context.
     */
    @Resource
    SessionContext sessionContext;


    @Override
    public Principal getCallerPrincipal() {
        Principal principal = sessionContext.getCallerPrincipal();
        if (principal.getName().equalsIgnoreCase(ANONYMOUS)) {
            AccessControlContext accessControlContext = AccessController.getContext();
            DomainCombiner dc = accessControlContext.getDomainCombiner();
            if (dc instanceof SubjectDomainCombiner) {
                SubjectDomainCombiner sdc = (SubjectDomainCombiner) dc;
                Set<Principal> principals = sdc.getSubject().getPrincipals();
                if (principals != null && principals.size() > 0) {
                    return principals.iterator().next();
                }
            }
        }
        return principal;
    }


    @Override
    public String getCallerPrincipalName() {
        return getCallerPrincipal().getName().toLowerCase();
    }
}
