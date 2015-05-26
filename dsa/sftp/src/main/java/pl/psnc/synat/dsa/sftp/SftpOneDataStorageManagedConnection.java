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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.DataStorageClient;
import pl.psnc.synat.dsa.DataStorageManagedConnectionMetaData;
import pl.psnc.synat.dsa.DataStorageWOOperation;
import pl.psnc.synat.dsa.OneDataStorageManagedConnection;
import pl.psnc.synat.dsa.exception.DataStorageResourceException;
import pl.psnc.synat.dsa.sftp.config.Credential;
import pl.psnc.synat.dsa.sftp.util.ConfigUtil;

/**
 * Managed connection to data storages based on SFTP protocol. It handles only one data storage.
 * 
 */
public class SftpOneDataStorageManagedConnection extends OneDataStorageManagedConnection {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SftpOneDataStorageManagedConnection.class);


    /**
     * Creates a new managed connection.
     * 
     * @param host
     *            host of data storage
     * @param port
     *            port of data storage
     * @param publicKeyType
     *            public key type of data storage
     * @param publicKey
     *            public key of data storage
     * 
     * @param cxRequestInfo
     *            parameters of connection
     * @throws DataStorageResourceException
     *             if some IO exception occurred
     */
    public SftpOneDataStorageManagedConnection(String host, Integer port, String publicKeyType, String publicKey,
            SftpDataStorageConnectionRequestInfo cxRequestInfo)
            throws DataStorageResourceException {
        super();
        logger.debug("SftpDataStorageConnectionRequestInfo: " + cxRequestInfo);
        Credential credential = ConfigUtil.getCredentialForOrganization(cxRequestInfo.getOrganization());
        try {
            client = new SftpDataStorageClient(host, port, publicKeyType, publicKey, credential);
            client.connect();
        } catch (IOException e) {
            throw new DataStorageResourceException(e);
        }
        Map<DataStorageClient, List<DataStorageWOOperation>> operations = new HashMap<DataStorageClient, List<DataStorageWOOperation>>();
        operations.put(client, this.operations);
        xares = new SftpDataStorageXAResource(Arrays.asList(new DataStorageClient[] { client }), operations);
    }


    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        logger.debug("getting a connection");
        logger.debug("Subject: " + subject);
        logger.debug("ConnectionRequestInfo: " + cxRequestInfo);
        if (connection == null) {
            connection = new SftpDataStorageConnection(this);
        }
        return connection;
    }


    @Override
    public ManagedConnectionMetaData getMetaData()
            throws ResourceException {
        logger.debug("getting a metadata");
        return new DataStorageManagedConnectionMetaData();
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("SftpDataStorageManagedConnection ");
        sb.append("[hashCode = ").append(hashCode());
        sb.append(", connection = ").append(connection);
        sb.append(", xares = ").append(xares);
        sb.append("]");
        return sb.toString();
    }

}
