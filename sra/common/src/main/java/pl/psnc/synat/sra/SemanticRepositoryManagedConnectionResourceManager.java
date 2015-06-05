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
package pl.psnc.synat.sra;

import java.util.List;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.sra.exception.SemanticRepositoryCommitException;
import pl.psnc.synat.sra.exception.SemanticRepositoryRollbackException;
import pl.psnc.synat.sra.util.TransactionUtils;

/**
 * Resource manager of a managed connection which managed a transaction associated with the managed connection.
 * 
 */
public abstract class SemanticRepositoryManagedConnectionResourceManager {

    /** Logger. */
    private static final Logger logger = LoggerFactory
            .getLogger(SemanticRepositoryManagedConnectionResourceManager.class);

    /** SRA Logger. */
    private static final Logger SRA_LOGGER = LoggerFactory.getLogger("SRA");

    /**
     * List of atomic write operations performed during transaction managed by this resource manager.
     */
    protected List<SemanticRepositoryWOOperation> operations;

    /**
     * Client of the semantic repository.
     */
    protected SemanticRepositoryClient client;

    /**
     * Marker indicating that the branch of the transaction managed by this resource manager was successfully prepared.
     */
    protected boolean success;

    /**
     * Transaction id - id of the only one transaction managed by this resource manager.
     */
    protected Xid xid;


    /**
     * Constructs new resource manager.
     * 
     * @param operations
     *            list of operations
     * @param client
     *            client of the semantic repository
     */
    public SemanticRepositoryManagedConnectionResourceManager(List<SemanticRepositoryWOOperation> operations,
            SemanticRepositoryClient client) {
        this.success = false;
        this.operations = operations;
        this.client = client;
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
            if (operations.size() > 0) {
                logger.debug("There were some write operations. All of them ended successfully");
                return XAResource.XA_OK;
            } else {
                logger.debug("There were only read operations. All of them ended successfully");
                return XAResource.XA_RDONLY;
            }
        } else {
            logger.debug("Transaction was earlier marked to rollback."
                    + " So pass this information to the transaction manager.");

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
        try {
            client.commit();
        } catch (SemanticRepositoryCommitException e) {
            logger.error("There was a problem while committing the transaction", e);
            SRA_LOGGER.error("TRANSACTION " + TransactionUtils.getXidAsString(this.xid) + " HAS NOT BEEN COMMITTED! "
                    + " THE FOLLOWING OPERATIONS MUST BE ROLLBACKED!!!");
            SRA_LOGGER.error(operations.toString());
            throw new XAException(XAException.XAER_RMERR);
        }
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
        try {
            client.rollback();
        } catch (SemanticRepositoryRollbackException e) {
            logger.error("There was a problem while rollbacking the transaction", e);
            SRA_LOGGER.error("TRANSACTION " + TransactionUtils.getXidAsString(this.xid) + " HAS NOT BEEN ROLLBACKED! "
                    + " IT IS NOT A PROBLEM SINCE NOT COMMITTED OPERATIONS ARE NOT PERFORMED.");
            throw new XAException(XAException.XAER_RMERR);
        }
    }

}
