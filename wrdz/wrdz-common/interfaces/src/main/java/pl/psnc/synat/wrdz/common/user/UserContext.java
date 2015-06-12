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

import java.security.Principal;

import javax.ejb.Local;

/**
 * Bean's interface that provides basic information about current user.
 */
@Local
public interface UserContext {

    /**
     * Returns principal of the caller.
     * 
     * @return caller's principal.
     */
    Principal getCallerPrincipal();


    /**
     * Return name of the caller.
     * 
     * @return caller's name
     * 
     */
    String getCallerPrincipalName();

}
