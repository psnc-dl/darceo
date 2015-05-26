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
package pl.psnc.synat.wrdz.zmd.download;

import java.net.URI;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.zmd.exception.AmbiguousResultException;

/**
 * Specifies the interface of the classes that will provide connection info, including user's authentication information
 * management.
 */
@Local
public interface ConnectionHelper {

    /**
     * Extracts information contained in the URI and system configuration to provide authentication information for
     * repository targeted by URI.
     * 
     * @param uri
     *            URI that is URL compliant
     * @return connection information including authentication information for given repository
     * @throws AmbiguousResultException
     *             if more than one authentication information item was found
     * @throws IllegalArgumentException
     *             if passed URI was not URL compliant
     */
    ConnectionInformation getConnectionInformation(URI uri)
            throws AmbiguousResultException, IllegalArgumentException;


    /**
     * Extract the port from the authority part of the URI or, if not present searches for default port for the protocol
     * specified in URI scheme part in the application configuration.
     * 
     * @param uri
     *            URI from which to extract port number
     * @return port number extracted from URI or, if no port present in URI the default port for the protocol from the
     *         application configuration. If both not found returns <code>null</code>
     */
    Integer getPort(URI uri);


    /**
     * Extracts the name of the host from the URI, if URI does not contain explicitly stated host name then
     * <code>localhost</code> is assumed.
     * 
     * @param uri
     *            URI from which to extract host name
     * @return name of the host from the URI or <code>localhost</code>
     */
    String getHost(URI uri);


    /**
     * Extracts the username from the URI.
     * 
     * @param uri
     *            URI from which to extract username
     * @return username extracted from the URI or <code>null</code> if no username was found
     */
    String getUsername(URI uri);


    /**
     * Extracts the password from the URI.
     * 
     * @param uri
     *            URI from which to extract password
     * @return password extracted from the URI or <code>null</code> if no password was found
     */
    String getPassword(URI uri);

}
