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
package pl.psnc.synat.sra.owlim;

import java.util.List;

import javax.transaction.xa.XAException;
import javax.transaction.xa.Xid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.sra.SemanticRepositoryClient;
import pl.psnc.synat.sra.SemanticRepositoryManagedConnectionResourceManager;
import pl.psnc.synat.sra.SemanticRepositoryWOOperation;
import pl.psnc.synat.sra.SparqlSelectTuple;
import pl.psnc.synat.sra.darceo.DArceoQueries;
import pl.psnc.synat.sra.exception.SemanticRepositoryCommitException;
import pl.psnc.synat.sra.exception.SemanticRepositoryConnectionException;
import pl.psnc.synat.sra.owlim.exception.SemanticRepositoryFlushException;
import pl.psnc.synat.sra.util.TransactionUtils;

/**
 * Resource manager of a managed connection which managed a transaction associated with the managed connection to
 * OWLIM-style semantic repository.
 * 
 */
public class OwlimSemanticRepositoryManagedConnectionResourceManager extends
        SemanticRepositoryManagedConnectionResourceManager {

    /** Logger. */
    private static final Logger logger = LoggerFactory
            .getLogger(OwlimSemanticRepositoryManagedConnectionResourceManager.class);

    /** SRA Logger. */
    private static final Logger SRA_LOGGER = LoggerFactory.getLogger("SRA");


    /**
     * Constructs new resource manager.
     * 
     * @param operations
     *            list of operations
     * @param client
     *            client of the OWLIM repository
     */
    public OwlimSemanticRepositoryManagedConnectionResourceManager(List<SemanticRepositoryWOOperation> operations,
            SemanticRepositoryClient client) {
        super(operations, client);
    }


    @Override
    public void commit(Xid xid, boolean onePhase)
            throws XAException {
        if (!xid.equals(this.xid)) {
            logger.error("This resource manager manages another transaction: "
                    + TransactionUtils.getXidAsString(this.xid));
            throw new XAException(XAException.XAER_PROTO);
        }
        try {
            client.commit();
            if ((operations.size() > 0) && (logger.isDebugEnabled())) {
                List<SparqlSelectTuple> executeSparqlSelectQuery = client.executeSparqlSelectQuery(DArceoQueries
                        .getAllTransformationsQuery("service", "transformation", "inpuid", "outpuid", "previous",
                            "subsequent"));
                for (SparqlSelectTuple sparqlSelectTuple : executeSparqlSelectQuery) {
                    logger.debug(sparqlSelectTuple.toString());
                }
            }
            if (operations.size() > 0) {
                logger.debug("There were some write operations. OWLIM-Lite does not save its memory state into files.");
                logger.debug("    It do it only when repositoty shutdowns!");
                ((OwlimSemanticRepositoryClient) client).flush();
            }
        } catch (SemanticRepositoryCommitException e) {
            logger.error("There was a problem while committing the transaction", e);
            SRA_LOGGER.error("TRANSACTION " + TransactionUtils.getXidAsString(this.xid) + " HAS NOT BEEN COMMITTED! "
                    + " THE FOLLOWING OPERATIONS MUST BE ROLLBACKED!!!");
            SRA_LOGGER.error(operations.toString());
            throw new XAException(XAException.XAER_RMERR);
        } catch (SemanticRepositoryFlushException e) {
            logger.error("There was a problem while flushing data to disk", e);
            SRA_LOGGER.error("TRANSACTION " + TransactionUtils.getXidAsString(this.xid) + " HAS NOT BEEN COMMITTED! "
                    + " THE FOLLOWING OPERATIONS MUST BE ROLLBACKED!!!");
            SRA_LOGGER.error(operations.toString());
            throw new XAException(XAException.XAER_RMERR);
        } catch (SemanticRepositoryConnectionException e) {
            logger.error("There was a problem while initializing repository from the disk.", e);
        }

    }

}
