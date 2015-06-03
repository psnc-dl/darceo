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
package pl.psnc.synat.dsa.fs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.DataStorageClient;
import pl.psnc.synat.dsa.DataStorageClientComparator;
import pl.psnc.synat.dsa.DataStorageManagedConnectionMetaData;
import pl.psnc.synat.dsa.DataStorageWOOperation;
import pl.psnc.synat.dsa.MultiDataStorageManagedConnection;
import pl.psnc.synat.dsa.exception.DataStorageResourceException;

/**
 * Managed connection to data storages based on file system. It handles many data storages.
 * 
 */
public class FSMultiDataStorageManagedConnection extends MultiDataStorageManagedConnection {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FSMultiDataStorageManagedConnection.class);


    /**
     * Creates a new managed connection.
     * 
     * @param roots
     *            set of root folders
     * @param redundancy
     *            redundancy factor
     * @param cxRequestInfo
     *            parameters of connection
     * @throws DataStorageResourceException
     *             if some IO exception occurred
     */
    public FSMultiDataStorageManagedConnection(Set<String> roots, int redundancy,
            FSDataStorageConnectionRequestInfo cxRequestInfo)
            throws DataStorageResourceException {
        super(redundancy);
        logger.debug("FSDataStorageConnectionRequestInfo: " + cxRequestInfo);
        if (roots.size() < redundancy) {
            logger.error("The redundancy factor cannot be greater than the number of archives!");
            throw new DataStorageResourceException(
                    "The redundancy factor cannot be greater than the number of archives!");
        }
        clients = new ArrayList<DataStorageClient>();
        for (String root : roots) {
            FSDataStorageClient client = new FSDataStorageClient(root);
            clients.add(client);
            operations.put(client, new ArrayList<DataStorageWOOperation>());
        }
        for (DataStorageClient client : clients) {
            try {
                client.connect();
            } catch (IOException e) {
                logger.error("Error while connecting to data storage by the client " + client, e);
                throw new DataStorageResourceException(e);
            }
        }
        Collections.sort(clients, new DataStorageClientComparator());
        xares = new FSDataStorageXAResource(clients.subList(0, redundancy), operations);
    }


    @Override
    public Object getConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        logger.debug("getting a connection");
        logger.debug("Subject: " + subject);
        logger.debug("ConnectionRequestInfo: " + cxRequestInfo);
        if (connection == null) {
            connection = new FSDataStorageConnection(this);
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
        StringBuffer sb = new StringBuffer("FSMultiDataStorageManagedConnection ");
        sb.append("[hashCode = ").append(hashCode());
        sb.append(", connection = ").append(connection);
        sb.append(", xares = ").append(xares);
        sb.append("]");
        return sb.toString();
    }

}
