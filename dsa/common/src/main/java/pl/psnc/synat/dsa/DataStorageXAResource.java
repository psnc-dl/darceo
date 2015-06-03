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
package pl.psnc.synat.dsa;

import java.util.List;
import java.util.Map;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.util.TransactionUtils;

/**
 * Transactional XA resource of data storage.
 * 
 */
public abstract class DataStorageXAResource implements XAResource {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DataStorageXAResource.class);

    /**
     * Resource manager for managed connection associated with this XA resource. This XA resource is actually only an
     * interface to this resource manager.
     */
    protected DataStorageManagedConnectionResourceManager resourceManager;


    /**
     * Constructs for XA resource.
     * 
     * @param clients
     *            clients
     * @param operations
     *            list of operations for each client
     */
    public DataStorageXAResource(List<DataStorageClient> clients,
            Map<DataStorageClient, List<DataStorageWOOperation>> operations) {
        resourceManager = new DataStorageManagedConnectionResourceManager(clients, operations);
    }


    /**
     * Returns a folder name where all write operations are done during processing a transaction.
     * 
     * @return transaction folder name
     */
    public String getTransactionFolderName() {
        return resourceManager.getTransactionFolderName();
    }


    @Override
    public void start(Xid xid, int flags)
            throws XAException {
        logger.debug("starting transaction xid " + TransactionUtils.getXidAsString(xid) + " flags "
                + TransactionUtils.flagsToString(flags));
        resourceManager.start(xid, flags);
    }


    @Override
    public void end(Xid xid, int flags)
            throws XAException {
        logger.debug("ending transaction xid " + TransactionUtils.getXidAsString(xid) + " flags "
                + TransactionUtils.flagsToString(flags));
        resourceManager.end(xid, flags);
    }


    @Override
    public int prepare(Xid xid)
            throws XAException {
        logger.debug("preparing transaction xid " + TransactionUtils.getXidAsString(xid));
        return resourceManager.prepare(xid);
    }


    @Override
    public void commit(Xid xid, boolean onePhase)
            throws XAException {
        logger.debug("committing transaction xid " + TransactionUtils.getXidAsString(xid) + " onePhase " + onePhase);
        resourceManager.commit(xid, onePhase);
    }


    @Override
    public void rollback(Xid xid)
            throws XAException {
        logger.debug("rollbacking transaction xid " + TransactionUtils.getXidAsString(xid));
        resourceManager.rollback(xid);
    }


    @Override
    public void forget(Xid xid)
            throws XAException {
        logger.debug("forgetting transaction xid " + TransactionUtils.getXidAsString(xid));
    }


    @Override
    public Xid[] recover(int flag)
            throws XAException {
        logger.debug("recovering flag " + TransactionUtils.flagsToString(flag));
        return null;
    }


    @Override
    public int getTransactionTimeout()
            throws XAException {
        logger.debug("getting timeout");
        return 0;
    }


    @Override
    public boolean setTransactionTimeout(int seconds)
            throws XAException {
        logger.debug("setting timeout seconds " + seconds);
        return false;
    }

}
