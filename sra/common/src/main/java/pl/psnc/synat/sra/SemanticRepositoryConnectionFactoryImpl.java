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

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.spi.ConnectionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory of connections to semantic repository.
 * 
 */
public abstract class SemanticRepositoryConnectionFactoryImpl implements SemanticRepositoryConnectionFactory {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SemanticRepositoryConnectionFactoryImpl.class);

    /** Serial Version UID. */
    private static final long serialVersionUID = 6607945015443632132L;

    /**
     * Managed connection factory.
     */
    protected SemanticRepositoryManagedConnectionFactoryImpl mcf;

    /**
     * Connection manager.
     */
    protected ConnectionManager cxManager;

    /**
     * Reference.
     */
    private Reference reference;


    /**
     * Constructor for new connections factory associated with a managed connection factory (by application server
     * through connection manager).
     * 
     * @param mcf
     *            managed connection factory
     * @param cxManager
     *            connection manager
     */
    public SemanticRepositoryConnectionFactoryImpl(SemanticRepositoryManagedConnectionFactoryImpl mcf,
            ConnectionManager cxManager) {
        logger.debug("SemanticRepositoryManagedConnectionFactory: " + mcf);
        logger.debug("ConnectionManager: " + cxManager);
        this.mcf = mcf;
        this.cxManager = cxManager;
    }


    @Override
    public void setReference(Reference reference) {
        this.reference = reference;
    }


    @Override
    public Reference getReference()
            throws NamingException {
        return reference;
    }

}
