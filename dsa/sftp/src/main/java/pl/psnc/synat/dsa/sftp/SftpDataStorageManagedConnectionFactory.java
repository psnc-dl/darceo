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
package pl.psnc.synat.dsa.sftp;

import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.DataStorageManagedConnectionFactory;

/**
 * Factory of both managed connection and factory of connections to data storages based on SFTP protocol. Supports
 * connection pooling by providing methods for matching and creation of managed connection instance.
 * 
 * This factory produces managed connections which aren't sharable between connections.
 */
public class SftpDataStorageManagedConnectionFactory extends DataStorageManagedConnectionFactory {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SftpDataStorageManagedConnectionFactory.class);

    /** Serial Version UID. */
    private static final long serialVersionUID = -8661190867838457504L;

    /**
     * DNS hostname or IP addres of data storage.
     */
    private String host;

    /**
     * Port on which SFTP server listens.
     */
    private Integer port;

    /**
     * Type of the public key of the host.
     */
    private String publicKeyType;

    /**
     * Public key of the host.
     */
    private String publicKey;


    @Override
    public Object createConnectionFactory(ConnectionManager cxManager)
            throws ResourceException {
        logger.debug("creating a connection factory");
        return new SftpDataStorageConnectionFactory(this, cxManager);
    }


    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        logger.debug("creating a managed connection");
        logger.debug("Subject: " + subject);
        logger.debug("ConnectionRequestInfo: " + cxRequestInfo);
        SftpOneDataStorageManagedConnection managedConnection = new SftpOneDataStorageManagedConnection(host, port,
                publicKeyType, publicKey, (SftpDataStorageConnectionRequestInfo) cxRequestInfo);
        logger.debug("cerated: " + managedConnection);
        return managedConnection;
    }


    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject,
            ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        logger.debug("matching a managed connection");
        logger.debug("connectionSet.size: " + connectionSet.size());
        logger.debug("Subject: " + subject);
        for (ManagedConnection mc : (Set<ManagedConnection>) connectionSet) {
            logger.debug("trying to match: " + mc);
            if (mc instanceof SftpOneDataStorageManagedConnection) {
                logger.debug("match found: " + mc);
                return mc;
            }
        }
        logger.debug("match not found");
        return null;
    }


    public String getHost() {
        return host;
    }


    public void setHost(String host) {
        this.host = host;
    }


    public Integer getPort() {
        return port;
    }


    public void setPort(Integer port) {
        this.port = port;
    }


    public String getPublicKeyType() {
        return publicKeyType;
    }


    public void setPublicKeyType(String publicKeyType) {
        this.publicKeyType = publicKeyType;
    }


    public String getPublicKey() {
        return publicKey;
    }


    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("SftpDataStorageManagedConnectionFactory ");
        sb.append("[hashCode = ").append(hashCode());
        sb.append(", ra = ").append(ra);
        sb.append("]");
        return sb.toString();
    }

}
