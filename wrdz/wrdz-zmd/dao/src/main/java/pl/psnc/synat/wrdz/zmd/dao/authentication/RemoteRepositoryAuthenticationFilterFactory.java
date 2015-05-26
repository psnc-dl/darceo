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

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactory;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.entity.authentication.RemoteRepository;
import pl.psnc.synat.wrdz.zmd.entity.authentication.RemoteRepositoryAuthentication;

/**
 * Defines methods producing filters for queries concerning {@link RemoteRepositoryAuthentication} entities.
 */
public interface RemoteRepositoryAuthenticationFilterFactory extends
        GenericQueryFilterFactory<RemoteRepositoryAuthentication> {

    /**
     * Filters the entities by the {@link RemoteRepository} id. Uses exact match.
     * 
     * @param remoteRepositoryId
     *            identifier of the remote repository entity
     * @return current representations of filters set
     */
    QueryFilter<RemoteRepositoryAuthentication> byRemoteRepositoryId(Long remoteRepositoryId);


    /**
     * Filters the entities by the name of the protocol of the {@link RemoteRepository}. Can use exact string match or a
     * regexp pattern.
     * 
     * @param protocol
     *            the name of the protocol or a regexp pattern for that name to match
     * @return current representations of filters set
     */
    QueryFilter<RemoteRepositoryAuthentication> byRemoteRepositoryProtocol(String protocol);


    /**
     * Filters the entities by the name of the host of the {@link RemoteRepository}. Can use exact string match or a
     * regexp pattern.
     * 
     * @param host
     *            the name of the host or a regexp pattern for that name to match
     * @return current representations of filters set
     */
    QueryFilter<RemoteRepositoryAuthentication> byRemoteRepositoryHost(String host);


    /**
     * Filters the entities by the port number of the {@link RemoteRepository}.
     * 
     * @param port
     *            the number of the port the repository works on
     * @return current representations of filters set
     */
    QueryFilter<RemoteRepositoryAuthentication> byRemoteRepositoryPort(Integer port);


    /**
     * Filters the entities by the username. Can use exact string match or a regexp pattern.
     * 
     * @param username
     *            the username or a regexp pattern for that name to match
     * @return current representations of filters set
     */
    QueryFilter<RemoteRepositoryAuthentication> byUsername(String username);


    /**
     * Filters the entities by the value of the field indicating whether or not the authentication info will be used as
     * default access to the repository.
     * 
     * @param usedByDefault
     *            the value indicating whether or not the authentication info will be used as default access to the
     *            repository
     * @return current representations of filters set
     */
    QueryFilter<RemoteRepositoryAuthentication> byUsedByDefault(Boolean usedByDefault);

}
