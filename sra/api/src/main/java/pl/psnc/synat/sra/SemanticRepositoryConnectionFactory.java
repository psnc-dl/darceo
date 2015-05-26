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

import java.io.Serializable;

import javax.resource.Referenceable;

import pl.psnc.synat.sra.exception.SemanticRepositoryResourceException;

/**
 * Common interface of connections' factories to semantic repositories.
 * 
 */
public interface SemanticRepositoryConnectionFactory extends Serializable, Referenceable {

    /**
     * Gets a connection to a semantic repository instance.
     * 
     * @return connection to semantic repository
     * @throws SemanticRepositoryResourceException
     *             if getting a connection fails
     */
    SemanticRepositoryConnection getConnection()
            throws SemanticRepositoryResourceException;


    /**
     * Gets a connection to a semantic repository instance.
     * 
     * @param spec
     *            specific parameters of connection
     * 
     * @return connection to semantic repository
     * @throws SemanticRepositoryResourceException
     *             if getting a connection fails
     */
    SemanticRepositoryConnection getConnection(SemanticRepositoryConnectionSpec spec)
            throws SemanticRepositoryResourceException;

}
