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

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.util.TransactionUtils;

/**
 * Resource manager of a managed connection which managed a transaction associated with the managed connection.
 * 
 */
public class DataStorageManagedConnectionResourceManager {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DataStorageManagedConnectionResourceManager.class);

    /** DSA Logger. */
    private static final Logger DSA_LOGGER = LoggerFactory.getLogger("DSA");

    /**
     * Clients of the file system protocol.
     */
    protected List<DataStorageClient> clients;

    /**
     * List of atomic write operations performed during transaction managed by this resource manager for each client.
     */
    protected Map<DataStorageClient, List<DataStorageWOOperation>> operations;

    /**
     * Marker indicating that the branch of the transaction managed by this resource manager was successfully prepared.
     */
    protected boolean success;

    /**
     * Transaction id - id of the only one transaction managed by this resource manager.
     */
    protected Xid xid;

    /**
     * Folder for transaction where all write operations are done during processing a transaction.
     */
    protected String folder;


    /**
     * Constructs new resource manager.
     * 
     * @param clients
     *            clients
     * @param operations
     *            map of list of operations for each client
     */
    public DataStorageManagedConnectionResourceManager(List<DataStorageClient> clients,
            Map<DataStorageClient, List<DataStorageWOOperation>> operations) {
        this.clients = clients;
        this.operations = operations;
        this.success = false;
        this.folder = null;
    }


    /**
     * Returns a folder name where all write operations are done during processing a transaction.
     * 
     * @return transaction folder name
     */
    public String getTransactionFolderName() {
        if (folder == null) {
            folder = TransactionUtils.getFolderNameForXid(this.xid) + new Date().getTime();
        }
        return folder;
    }


    /**
     * Starts or resumes a transaction branch specified by the transaction id. If TMJOIN is specified, transaction is
     * joining a transaction previously started.
     * 
     * @param xid
     *            transaction id
     * @param flags
     *            one of TMNOFLAGS, TMJOIN, or TMRESUME
     * @throws XAException
     *             if some error occurs
     **/
    public void start(Xid xid, int flags)
            throws XAException {
        switch (flags) {
            case XAResource.TMNOFLAGS:
                this.success = false;
                this.xid = xid;
                break;
            case XAResource.TMRESUME:
                if (!xid.equals(this.xid)) {
                    logger.error("This resource manager manages another transaction: "
                            + TransactionUtils.getXidAsString(this.xid));
                    throw new XAException(XAException.XAER_PROTO);
                } else {
                    logger.debug("resuming transaction: " + TransactionUtils.getXidAsString(xid));
                }
                break;
            case XAResource.TMJOIN:
                if (!xid.equals(this.xid)) {
                    logger.error("This resource manager manages another transaction: "
                            + TransactionUtils.getXidAsString(this.xid));
                    throw new XAException(XAException.XAER_PROTO);
                } else {
                    logger.debug("joining transaction: " + TransactionUtils.getXidAsString(xid));
                }
                break;
            default:
                logger.error("Unsupported transaction flag: ", TransactionUtils.flagsToString(flags));
                throw new XAException("Unsupported transaction flag: " + TransactionUtils.flagsToString(flags));
        }
    }


    /**
     * Ends or suspends a transaction branch specified by the transaction id. If TMFAIL is specified, transaction should
     * be marked as failed - to rolled it back. Parameters:
     * 
     * @param xid
     *            transaction id - the same as the identifier used previously in the start method
     * @param flags
     *            one of TMSUCCESS, TMFAIL, or TMSUSPEND
     * @throws XAException
     *             if some error occurs
     **/
    public void end(Xid xid, int flags)
            throws XAException {
        if (!xid.equals(this.xid)) {
            logger.error("This resource manager manages another transaction: "
                    + TransactionUtils.getXidAsString(this.xid));
            throw new XAException(XAException.XAER_PROTO);
        }
        switch (flags) {
            case XAResource.TMSUCCESS:
                logger.debug("Transaction : " + TransactionUtils.getXidAsString(xid) + " marked to commit.");
                this.success = true;
                break;
            case XAResource.TMFAIL:
                logger.debug("Transaction : " + TransactionUtils.getXidAsString(xid)
                        + " mreked to rollback. It is marked to rollback by default.");
                break;
            case XAResource.TMSUSPEND:
                logger.debug("Transaction : " + TransactionUtils.getXidAsString(xid) + " suspended.");
                break;
            default:
                logger.error("Unsupported transaction flag: ", TransactionUtils.flagsToString(flags));
                throw new XAException("Unsupported transaction flag: " + TransactionUtils.flagsToString(flags));
        }
    }


    /**
     * Ask the resource manager to prepare for a transaction commit of the transaction branch specified by the
     * transaction id.
     * 
     * @param xid
     *            transaction id - the same as the identifier used previously in the start and end methods
     * @return vote for a transaction commit: XA_RDONLY or XA_OK - if the resource manager wants to roll back the
     *         transaction, it should do so by raising an appropriate XAException
     * @throws XAException
     *             if some error occurs or the resource manager votes for roll back this transaction
     */
    public int prepare(Xid xid)
            throws XAException {

        if (!xid.equals(this.xid)) {
            logger.error("This resource manager manages another transaction: "
                    + TransactionUtils.getXidAsString(this.xid));
            throw new XAException(XAException.XAER_PROTO);
        }
        if (success) {
            int nos = 0;
            for (List<DataStorageWOOperation> os : operations.values()) {
                nos += os.size();
            }
            if (nos > 0) {
                logger.debug("There were some write operations. All of them ended successfully");
                return XAResource.XA_OK;
            } else {
                logger.debug("There were only read operations. All of them ended successfully");
                return XAResource.XA_RDONLY;
            }
        } else {
            logger.debug("Transaction was earlier marked to rollback."
                    + " So pass this information to the transaction manager.");
            cleanup();
            throw new XAException(XAException.XA_RBROLLBACK);
        }
    }


    /**
     * Commits the branch of the global transaction specified by the transaction id.
     * 
     * @param xid
     *            transaction id - the same as the identifier used previously in the start and end methods
     * @param onePhase
     *            if true, the resource manager should use a one-phase commit protocol, otherwise two-commit protocol
     * @throws XAException
     *             if some error occurs
     */
    public void commit(Xid xid, boolean onePhase)
            throws XAException {
        if (!xid.equals(this.xid)) {
            logger.error("This resource manager manages another transaction: "
                    + TransactionUtils.getXidAsString(this.xid));
            throw new XAException(XAException.XAER_PROTO);
        }
        for (DataStorageClient client : clients) {
            for (DataStorageWOOperation o : operations.get(client)) {
                logger.debug("operation: " + o);
                switch (o.getType()) {
                    case CREATE_DIRECTORY:
                        try {
                            client.createDirectory(o.getPath());
                            logger.debug("done: " + o);
                        } catch (IOException e) {
                            logger.error("There was a problem while creating a directory.", e);
                            DSA_LOGGER.error("TRANSACTION " + TransactionUtils.getXidAsString(this.xid)
                                    + " FROM THE FOLDER " + getTransactionFolderName() + " AT THE CLIENT " + client
                                    + " MUST BE COMMITED!!!");
                            throw new XAException(XAException.XA_HEURCOM);
                        }
                        break;
                    case DELETE_DIRECTORY:
                        try {
                            client.deleteDirectory(o.getPath());
                            logger.debug("done: " + o);
                        } catch (IOException e) {
                            logger.error("There was a problem while deleting a directory.", e);
                            DSA_LOGGER.error("TRANSACTION " + TransactionUtils.getXidAsString(this.xid)
                                    + " FROM THE FOLDER " + getTransactionFolderName() + " AT THE CLIENT " + client
                                    + " MUST BE COMMITED!!!");
                            throw new XAException(XAException.XA_HEURCOM);
                        }
                        break;
                    case PUT_FILE:
                        try {
                            client.moveFile(getTransactionFolderName() + "/" + o.getPath(), o.getPath());
                            logger.debug("done: " + o);
                        } catch (IOException e) {
                            logger.error("There was a problem while moving a file.", e);
                            DSA_LOGGER.error("TRANSACTION " + TransactionUtils.getXidAsString(this.xid)
                                    + " FROM THE FOLDER " + getTransactionFolderName() + " AT THE CLIENT " + client
                                    + " MUST BE COMMITED!!!");
                            throw new XAException(XAException.XA_HEURCOM);
                        }
                        break;
                    case DELETE_FILE:
                        try {
                            client.deleteFile(o.getPath());
                            logger.debug("done: " + o);
                        } catch (IOException e) {
                            logger.error("There was a problem while deleting a file.", e);
                            DSA_LOGGER.error("TRANSACTION " + TransactionUtils.getXidAsString(this.xid)
                                    + " FROM THE FOLDER " + getTransactionFolderName() + " AT THE CLIENT " + client
                                    + " MUST BE COMMITED!!!");
                            throw new XAException(XAException.XA_HEURCOM);
                        }
                        break;
                    default:
                        logger.error("unrecognized type of operation: " + o.getType() + " RELATED TO THE CLIENT "
                                + client);
                        throw new XAException(XAException.XA_HEURCOM);
                }
            }
        }
        cleanup();
    }


    /**
     * Informs the resource manager to rollback the branch of the global transaction specified by the transaction id.
     * 
     * @param xid
     *            transaction id - the same as the identifier used previously in the start and end methods
     * @throws XAException
     *             if some error occurs
     **/
    public void rollback(Xid xid)
            throws XAException {
        if (!xid.equals(this.xid)) {
            logger.error("This resource manager manages another transaction: "
                    + TransactionUtils.getXidAsString(this.xid));
            throw new XAException(XAException.XAER_PROTO);
        }
        cleanup();
    }


    /**
     * Cleans up the temporary directories for this transaction.
     */
    protected void cleanup() {
        for (DataStorageClient client : clients) {
            try {
                if (client.directoryExists(getTransactionFolderName())) {
                    client.deleteDirectory(getTransactionFolderName());
                }
            } catch (IOException e) {
                logger.error("There was a problem while deleting a directory.", e);
                DSA_LOGGER.error("TRANSACTION FOLDER " + getTransactionFolderName() + " COULDN'T BE REMOVED!!!");
            }
        }
    }

}
