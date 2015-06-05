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
import javax.transaction.xa.XAResource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.sra.SemanticRepositoryClient;
import pl.psnc.synat.sra.SemanticRepositoryWOOperation;
import pl.psnc.synat.sra.SemanticRepositoryXAResourceImpl;

/**
 * Transactional XA resource of OWLIM-style semantic repository.
 * 
 */
public class OwlimSemanticRepositoryXAResource extends SemanticRepositoryXAResourceImpl {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(OwlimSemanticRepositoryXAResource.class);


    /**
     * Constructs new XA resource.
     * 
     * @param operations
     *            list of operations
     * @param client
     *            client of the OWLIM repository
     */
    public OwlimSemanticRepositoryXAResource(List<SemanticRepositoryWOOperation> operations,
            SemanticRepositoryClient client) {
        resourceManager = new OwlimSemanticRepositoryManagedConnectionResourceManager(operations, client);
    }


    @Override
    public boolean isSameRM(XAResource xares)
            throws XAException {
        logger.debug("determining resource manager");
        logger.debug("XAResource: " + xares);
        // always recognize as new RM - separate branch of a transaction
        return false;
    }

}
