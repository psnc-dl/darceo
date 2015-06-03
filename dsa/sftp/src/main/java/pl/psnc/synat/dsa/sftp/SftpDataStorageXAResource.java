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

import java.util.List;
import java.util.Map;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.DataStorageClient;
import pl.psnc.synat.dsa.DataStorageWOOperation;
import pl.psnc.synat.dsa.DataStorageXAResource;

/**
 * Transactional XA resource of data storage based on SFTP protocol.
 * 
 */
public class SftpDataStorageXAResource extends DataStorageXAResource {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SftpDataStorageXAResource.class);


    /**
     * Constructs new XA resource.
     * 
     * @param clients
     *            clients
     * @param operations
     *            list of operations for each client
     */
    public SftpDataStorageXAResource(List<DataStorageClient> clients,
            Map<DataStorageClient, List<DataStorageWOOperation>> operations) {
        super(clients, operations);
    }


    @Override
    public boolean isSameRM(XAResource xares)
            throws XAException {
        logger.debug("determining resource manager XAResource: " + xares);
        // always recognize as new RM - separate branch of a transaction
        return false;
    }

}
