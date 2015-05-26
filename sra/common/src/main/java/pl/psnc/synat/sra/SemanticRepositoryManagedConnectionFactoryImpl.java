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

import java.io.PrintWriter;

import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ResourceAdapterAssociation;

/**
 * Factory of both managed connection and factory of connections to semantic repository.
 * 
 */
public abstract class SemanticRepositoryManagedConnectionFactoryImpl implements ManagedConnectionFactory,
        ResourceAdapterAssociation {

    /** Serial Version UID. */
    private static final long serialVersionUID = -6781197902980080653L;


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

}
