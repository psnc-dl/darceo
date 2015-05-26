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

import java.util.Set;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ResourceAdapter;
import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.sra.SemanticRepositoryManagedConnectionFactoryImpl;

/**
 * Factory of both managed connection and factory of connections to OWLIM-style semantic repositories. Supports
 * connection pooling by providing methods for matching and creation of managed connection instance.
 * 
 * This factory produces only managed connections which aren't sharable between connections - it's based upon
 * connections returned from OWLIM repositories initialized in Resource Adapter.
 * 
 */
public class OwlimSemanticRepositoryManagedConnectionFactory extends SemanticRepositoryManagedConnectionFactoryImpl {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(OwlimSemanticRepositoryManagedConnectionFactory.class);

    /** Serial Version UID. */
    private static final long serialVersionUID = 3761562489248364929L;

    /**
     * Resource Adapter.
     */
    protected OwlimSemanticRepositoryResourceAdapter ra;


    @Override
    public ResourceAdapter getResourceAdapter() {
        logger.debug("getting resource adapter");
        return ra;
    }


    @Override
    public void setResourceAdapter(ResourceAdapter ra)
            throws ResourceException {
        logger.debug("setting resource adapter");
        logger.debug("ResourceAdapter: " + ra);
        this.ra = (OwlimSemanticRepositoryResourceAdapter) ra;
    }


    @Override
    public Object createConnectionFactory(ConnectionManager cxManager)
            throws ResourceException {
        logger.debug("creating a connection factory");
        return new OwlimSemanticRepositoryConnectionFactory(this, cxManager);
    }


    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        logger.debug("creating a managed connection");
        logger.debug("Subject: " + subject);
        logger.debug("ConnectionRequestInfo: " + cxRequestInfo);
        OwlimSemanticRepositoryManagedConnection managedConnection = new OwlimSemanticRepositoryManagedConnection(
                ra.getRepositoryManager(), ra.getRepository(),
                (OwlimSemanticRepositoryConnectionRequestInfo) cxRequestInfo);
        logger.debug("cerated: " + managedConnection);
        return managedConnection;
    }


    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ManagedConnection matchManagedConnections(Set connectionSet, Subject subject,
            ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        logger.debug("matching a managed connection");
        logger.debug("connectionSet.size: " + connectionSet.size());
        logger.debug("Subject: " + subject);
        for (ManagedConnection mc : (Set<ManagedConnection>) connectionSet) {
            logger.debug("trying to match: " + mc);
            if (mc instanceof OwlimSemanticRepositoryManagedConnection) {
                logger.debug("match found: " + mc);
                return mc;
            }
        }
        logger.debug("match not found");
        return null;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("OwlimSemanticRepositoryManagedConnectionFactory ");
        sb.append("[hashCode = ").append(hashCode());
        sb.append(", ra = ").append(ra);
        sb.append("]");
        return sb.toString();
    }

}
