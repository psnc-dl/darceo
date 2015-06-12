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
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.dao.QueryModifier;
import pl.psnc.synat.wrdz.zmd.dao.authentication.RemoteRepositoryAuthenticationDao;
import pl.psnc.synat.wrdz.zmd.dao.authentication.RemoteRepositoryAuthenticationFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.authentication.RemoteRepositoryAuthentication;
import pl.psnc.synat.wrdz.zmd.exception.AmbiguousResultException;

/**
 * A class providing user's authentication info management using both data provided in the connection URL and
 * information in the database.
 * 
 * <b>Note:</b> Due to limitations of {@link java.net.URL} class {@link URI} is used instead.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ConnectionHelperBean implements ConnectionHelper {

    /**
     * DAO object managing the remote repositories' authentication data.
     */
    @EJB
    private RemoteRepositoryAuthenticationDao remoteRepositoryAuthenticationDao;


    @Override
    public ConnectionInformation getConnectionInformation(URI uri)
            throws AmbiguousResultException, IllegalArgumentException {
        if (uri == null) {
            throw new IllegalArgumentException("Cannot download content - given URI is null");
        } else if (!uri.isAbsolute()) {
            throw new IllegalArgumentException("Cannot download content - given URI is not a URL");
        }
        String protocol = uri.getScheme();
        String host = getHost(uri);
        Integer port = getPort(uri);
        String username = getUsername(uri);
        RemoteRepositoryAuthentication result = getRemoteRepositoryAuthentication(protocol, host, port, username);
        return constructConnectionInformation(result);
    }


    /**
     * Fetches the remote repository authentication from the database using information provided in the parameters.
     * 
     * @param protocol
     *            repository's protocol type
     * @param host
     *            repository's host
     * @param port
     *            port on which repository listens
     * @param username
     *            name of the user of repository
     * @return remote repository authentication information retrieve from the database or null if none was found.
     * @throws AmbiguousResultException
     *             if multiple authentication info entries match arguments passed.
     */
    @SuppressWarnings({ "rawtypes" })
    private RemoteRepositoryAuthentication getRemoteRepositoryAuthentication(String protocol, String host,
            Integer port, String username)
            throws AmbiguousResultException {
        QueryModifier queryModifier = remoteRepositoryAuthenticationDao.createQueryModifier();
        RemoteRepositoryAuthenticationFilterFactory filterFactory = (RemoteRepositoryAuthenticationFilterFactory) queryModifier
                .getQueryFilterFactory();
        QueryFilter<RemoteRepositoryAuthentication> queryFilter = filterFactory.byRemoteRepositoryProtocol(protocol);
        QueryFilter<RemoteRepositoryAuthentication> queryFilterWithDefault = null;
        queryFilter = filterFactory.and(queryFilter, filterFactory.byRemoteRepositoryHost(host));
        if (port != null) {
            queryFilter = filterFactory.and(queryFilter, filterFactory.byRemoteRepositoryPort(port));
        }
        if (username != null && !username.equals("")) {
            queryFilter = filterFactory.and(queryFilter, filterFactory.byUsername(username));
        } else {
            queryFilterWithDefault = filterFactory.and(queryFilter, filterFactory.byUsedByDefault(Boolean.TRUE));
        }
        List<RemoteRepositoryAuthentication> results;
        if (queryFilterWithDefault != null) {
            results = remoteRepositoryAuthenticationDao.findBy(queryFilterWithDefault, false);
            switch (results.size()) {
                case 0:
                    break;
                case 1:
                    return results.get(0);
                default:
                    throw new AmbiguousResultException("Multiple matches found where single match was expected.");
            }
        }
        results = remoteRepositoryAuthenticationDao.findBy(queryFilter, false);
        switch (results.size()) {
            case 0:
                return null;
            case 1:
                return results.get(0);
            default:
                throw new AmbiguousResultException("Multiple matches found where single match was expected.");
        }
    }


    /**
     * Constructs new {@link ConnectionInformation} using information contained in passed
     * {@link RemoteRepositoryAuthentication} object.
     * 
     * @param source
     *            authentication information for the remote repository
     * @return constructed connection information
     */
    private ConnectionInformation constructConnectionInformation(RemoteRepositoryAuthentication source) {
        if (source == null) {
            return new ConnectionInformation();
        }
        ConnectionInformation connectionInfo = new ConnectionInformation(source.getRemoteRepository().getHost(), source
                .getRemoteRepository().getPort(), source.getUsername(), source.getPassword());
        if (source.getPrivateKey() != null) {
            CertificateInformation certificateInfo = new CertificateInformation(source.getPassphrase(),
                    source.getPrivateKey(), source.getPublicKey());
            connectionInfo.setCertificateInfo(certificateInfo);
        }
        return connectionInfo;
    }


    @Override
    public Integer getPort(URI uri) {
        Integer port = uri.getPort();
        if (port == null || port < 1) {
            String scheme = uri.getScheme();
            if ("http".equals(scheme)) {
                return 80;
            }
            if ("https".equals(scheme)) {
                return 443;
            }
            if ("ftp".equals(scheme)) {
                return 21;
            }
            if ("sftp".equals(scheme)) {
                return 22;
            }
            return null;
        } else {
            return port;
        }
    }


    @Override
    public String getHost(URI uri) {
        String host = uri.getHost();
        if (host == null || host.equals("")) {
            host = "localhost";
        }
        return host;
    }


    @Override
    public String getUsername(URI uri) {
        String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            return userInfo.split(":")[0];
        }
        return null;
    }


    @Override
    public String getPassword(URI uri) {
        String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            String[] temp = userInfo.split(":");
            return temp.length > 1 ? temp[1] : null;
        }
        return null;
    }

}
