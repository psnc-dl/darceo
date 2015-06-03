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

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.security.auth.Subject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.DataStorageManagedConnection;
import pl.psnc.synat.dsa.DataStorageManagedConnectionFactory;

/**
 * Factory of both managed connection and factory of connections to data storages based on file system. Supports
 * connection pooling by providing methods for matching and creation of managed connection instance.
 * 
 * This factory produces managed connections which aren't sharable between connections.
 */
public class FSDataStorageManagedConnectionFactory extends DataStorageManagedConnectionFactory {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FSDataStorageManagedConnectionFactory.class);

    /** Serial Version UID. */
    private static final long serialVersionUID = 3926060826806850460L;

    /** A set of paths to the root folders on a file system. */
    private Set<String> roots;

    /** Redundancy factor - it says how many copies should be distributed among archives. */
    private Integer redundancy;


    @Override
    public Object createConnectionFactory(ConnectionManager cxManager)
            throws ResourceException {
        logger.debug("creating a connection factory");
        return new FSDataStorageConnectionFactory(this, cxManager);
    }


    @Override
    public ManagedConnection createManagedConnection(Subject subject, ConnectionRequestInfo cxRequestInfo)
            throws ResourceException {
        logger.debug("creating a managed connection");
        logger.debug("Subject: " + subject);
        logger.debug("ConnectionRequestInfo: " + cxRequestInfo);
        DataStorageManagedConnection managedConnection = null;
        if (roots.size() > 1) {
            managedConnection = new FSMultiDataStorageManagedConnection(roots, redundancy,
                    (FSDataStorageConnectionRequestInfo) cxRequestInfo);

        } else {
            managedConnection = new FSOneDataStorageManagedConnection(roots.iterator().next(),
                    (FSDataStorageConnectionRequestInfo) cxRequestInfo);
        }
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
            if (mc instanceof FSOneDataStorageManagedConnection || mc instanceof FSMultiDataStorageManagedConnection) {
                logger.debug("match found: " + mc);
                return mc;
            }
        }
        logger.debug("match not found");
        return null;
    }


    /**
     * Returns the list of root folders from the field <code>roots</code>.
     * 
     * @return list of root folders
     */
    public String getRoots() {
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = this.roots.iterator();
        while (it.hasNext()) {
            sb.append(it.hasNext());
            if (it.hasNext()) {
                sb.append(';');
            }
        }
        return sb.toString();
    }


    /**
     * Reads the list of root folders from the settings of AS and sets them as a set of folders.
     * 
     * @param roots
     *            list of root folders from the settings in AS
     */
    public void setRoots(String roots) {
        logger.debug("set roots: " + roots);
        StringTokenizer tokenizer = new StringTokenizer(roots, ";", false);
        this.roots = new LinkedHashSet<String>();
        while (tokenizer.hasMoreTokens()) {
            String root = tokenizer.nextToken().replace('\\', '/');
            if (!root.endsWith("/")) {
                root = root + "/";
            }
            this.roots.add(root);
        }
    }


    public Integer getRedundancy() {
        return redundancy;
    }


    public void setRedundancy(Integer redundancy) {
        this.redundancy = redundancy;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("FSDataStorageManagedConnectionFactory ");
        sb.append("[hashCode = ").append(hashCode());
        sb.append(", ra = ").append(ra);
        sb.append(", roots = ").append(roots);
        sb.append("]");
        return sb.toString();
    }

}
