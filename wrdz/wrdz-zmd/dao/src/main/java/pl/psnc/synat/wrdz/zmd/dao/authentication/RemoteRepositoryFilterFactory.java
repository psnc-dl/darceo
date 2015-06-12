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

/**
 * Defines methods producing filters for queries concerning {@link RemoteRepository} entities.
 */
public interface RemoteRepositoryFilterFactory extends GenericQueryFilterFactory<RemoteRepository> {

    /**
     * Filters the entities by the name of the protocol. Can use exact string match or a regexp pattern.
     * 
     * @param protocol
     *            the name of the protocol or a regexp pattern for that name to match
     * @return current representations of filters set
     */
    QueryFilter<RemoteRepository> byProtocol(String protocol);


    /**
     * Filters the entities by the name of the host. Can use exact string match or a regexp pattern.
     * 
     * @param host
     *            the name of the host or a regexp pattern for that name to match
     * @return current representations of filters set
     */
    QueryFilter<RemoteRepository> byHost(String host);


    /**
     * Filters the entities by the port number.
     * 
     * @param port
     *            the number of the port the repository works on
     * @return current representations of filters set
     */
    QueryFilter<RemoteRepository> byPort(Integer port);


    /**
     * Filters the entities by the description field. Can use exact string match or a regexp pattern.
     * 
     * @param description
     *            the repository description or a regexp pattern for that description to match
     * @return current representations of filters set
     */
    QueryFilter<RemoteRepository> byDescription(String description);

}
