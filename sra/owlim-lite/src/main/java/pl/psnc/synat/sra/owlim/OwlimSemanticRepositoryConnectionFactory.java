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

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.sra.SemanticRepositoryConnection;
import pl.psnc.synat.sra.SemanticRepositoryConnectionFactoryImpl;
import pl.psnc.synat.sra.SemanticRepositoryConnectionSpec;
import pl.psnc.synat.sra.exception.SemanticRepositoryConnectionException;

/**
 * Factory of connections to OWLIM-style semantic repository.
 * 
 */
public class OwlimSemanticRepositoryConnectionFactory extends SemanticRepositoryConnectionFactoryImpl {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(OwlimSemanticRepositoryConnectionFactory.class);

    /** Serial Version UID. */
    private static final long serialVersionUID = -4203432750850184922L;


    /**
     * Creates a new connection factory associated with a managed connection factory (by application server through
     * connection manager).
     * 
     * @param mcf
     *            Managed connection factory
     * @param cxManager
     *            Connection manager
     */
    public OwlimSemanticRepositoryConnectionFactory(OwlimSemanticRepositoryManagedConnectionFactory mcf,
            ConnectionManager cxManager) {
        super(mcf, cxManager);
    }


    @Override
    public SemanticRepositoryConnection getConnection()
            throws SemanticRepositoryConnectionException {
        return getConnection(new SemanticRepositoryConnectionSpec());
    }


    @Override
    public SemanticRepositoryConnection getConnection(SemanticRepositoryConnectionSpec spec)
            throws SemanticRepositoryConnectionException {
        logger.debug("getting a connection");
        OwlimSemanticRepositoryConnection connection;
        try {
            connection = (OwlimSemanticRepositoryConnection) cxManager.allocateConnection(mcf,
                getConnectionRequestInfo(spec));
        } catch (ResourceException e) {
            logger.error("Error in getting connection.", e);
            throw new SemanticRepositoryConnectionException(e);
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
    private OwlimSemanticRepositoryConnectionRequestInfo getConnectionRequestInfo(SemanticRepositoryConnectionSpec spec) {
        return new OwlimSemanticRepositoryConnectionRequestInfo(spec.getDArceo());
    }

}
