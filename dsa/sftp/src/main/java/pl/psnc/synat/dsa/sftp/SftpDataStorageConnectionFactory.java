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

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.DataStorageConnection;
import pl.psnc.synat.dsa.AbstractDataStorageConnectionFactory;
import pl.psnc.synat.dsa.DataStorageConnectionSpec;
import pl.psnc.synat.dsa.DataStorageManagedConnectionFactory;
import pl.psnc.synat.dsa.exception.DataStorageResourceException;

/**
 * Factory of connections to data storage based on SFTP protocol.
 * 
 */
public class SftpDataStorageConnectionFactory extends AbstractDataStorageConnectionFactory {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SftpDataStorageConnectionFactory.class);

    /** Serial Version UID. */
    private static final long serialVersionUID = 7352397074690252212L;


    /**
     * Creates a new connection factory associated with a managed connection factory (by application server through
     * connection manager).
     * 
     * @param mcf
     *            Managed connection factory
     * @param cxManager
     *            Connection manager
     */
    public SftpDataStorageConnectionFactory(DataStorageManagedConnectionFactory mcf, ConnectionManager cxManager) {
        super(mcf, cxManager);
    }


    @Override
    public DataStorageConnection getConnection(DataStorageConnectionSpec spec)
            throws DataStorageResourceException {
        logger.debug("getting a connection");
        SftpDataStorageConnection connection;
        try {
            connection = (SftpDataStorageConnection) cxManager.allocateConnection(mcf, getConnectionRequestInfo(spec));
        } catch (ResourceException e) {
            logger.error("Error in getting connection.", e);
            throw new DataStorageResourceException(e);
        }
        logger.debug("obtained connection: " + connection);
        return connection;
    }


    /**
     * Gets a connection request info with specific parameters of managed connection.
     * 
     * @param spec
     *            specific parameters of connection
     * @return connection request info
     */
    private SftpDataStorageConnectionRequestInfo getConnectionRequestInfo(DataStorageConnectionSpec spec) {
        SftpDataStorageConnectionRequestInfo cxRequestInfo = new SftpDataStorageConnectionRequestInfo(
                spec.getOrganization());
        return cxRequestInfo;
    }

}
