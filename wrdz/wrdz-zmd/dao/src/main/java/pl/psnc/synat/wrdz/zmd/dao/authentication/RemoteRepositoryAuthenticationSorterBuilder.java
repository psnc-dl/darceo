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
package pl.psnc.synat.wrdz.zmd.dao.authentication;

import pl.psnc.synat.wrdz.common.dao.GenericQuerySorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.authentication.RemoteRepository;
import pl.psnc.synat.wrdz.zmd.entity.authentication.RemoteRepositoryAuthentication;

/**
 * Defines methods producing sorters for queries concerning {@link RemoteRepositoryAuthentication} entities.
 */
public interface RemoteRepositoryAuthenticationSorterBuilder extends
        GenericQuerySorterBuilder<RemoteRepositoryAuthentication> {

    /**
     * Enables sorting by the identifier (primary key) of the associated {@link RemoteRepository}.
     * 
     * @param ascendingly
     *            if <code>true</code> the sorting order will be ascending, else it will be descending.
     * @return modified instance of the builder with updated sorters list
     */
    RemoteRepositoryAuthenticationSorterBuilder byRemoteRepositoryId(boolean ascendingly);


    /**
     * Enables sorting by the name of the protocol name of associated {@link RemoteRepository}.
     * 
     * @param ascendingly
     *            if <code>true</code> the sorting order will be ascending, else it will be descending.
     * @return modified instance of the builder with updated sorters list
     */
    RemoteRepositoryAuthenticationSorterBuilder byRemoteRepositoryProtocol(boolean ascendingly);


    /**
     * Enables sorting by the name of the host name of associated {@link RemoteRepository}.
     * 
     * @param ascendingly
     *            if <code>true</code> the sorting order will be ascending, else it will be descending.
     * @return modified instance of the builder with updated sorters list
     */
    RemoteRepositoryAuthenticationSorterBuilder byRemoteRepositoryHost(boolean ascendingly);


    /**
     * Enables sorting by the name of the port number of associated {@link RemoteRepository}.
     * 
     * @param ascendingly
     *            if <code>true</code> the sorting order will be ascending, else it will be descending.
     * @return modified instance of the builder with updated sorters list
     */
    RemoteRepositoryAuthenticationSorterBuilder byRemoteRepositoryPort(boolean ascendingly);


    /**
     * Enables sorting by the username.
     * 
     * @param ascendingly
     *            if <code>true</code> the sorting order will be ascending, else it will be descending.
     * @return modified instance of the builder with updated sorters list
     */
    RemoteRepositoryAuthenticationSorterBuilder byUsername(boolean ascendingly);
}
