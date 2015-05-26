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

import java.io.PrintWriter;

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapter;
import javax.resource.spi.ResourceAdapterAssociation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory of both managed connection and factory of connections to data storage.
 * 
 */
public abstract class DataStorageManagedConnectionFactory implements ManagedConnectionFactory,
        ResourceAdapterAssociation {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DataStorageManagedConnectionFactory.class);

    /** Serial Version UID. */
    private static final long serialVersionUID = -9151787295992539403L;

    /**
     * Resource Adapter.
     */
    protected DataStorageResourceAdapter ra;


    @Override
    public Object createConnectionFactory()
            throws ResourceException {
        throw new UnsupportedOperationException("It cannot be used in unmanaged environment.");
    }


    @Override
    public PrintWriter getLogWriter()
            throws ResourceException {
        return null;
    }


    @Override
    public void setLogWriter(PrintWriter out)
            throws ResourceException {
    }


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
        this.ra = (DataStorageResourceAdapter) ra;
    }

}
