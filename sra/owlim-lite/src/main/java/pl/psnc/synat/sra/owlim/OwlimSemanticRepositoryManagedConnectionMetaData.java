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
import javax.resource.spi.ManagedConnectionMetaData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Metadata of the OWLIM-style semantic repository resource adapter.
 * 
 */
public class OwlimSemanticRepositoryManagedConnectionMetaData implements ManagedConnectionMetaData {

    /** Logger. */
    private static final Logger logger = LoggerFactory
            .getLogger(OwlimSemanticRepositoryManagedConnectionMetaData.class);


    @Override
    public String getEISProductName()
            throws ResourceException {
        logger.debug("getting EIS product name");
        return null;
    }


    @Override
    public String getEISProductVersion()
            throws ResourceException {
        logger.debug("getting EIS product version");
        return null;
    }


    @Override
    public int getMaxConnections()
            throws ResourceException {
        logger.debug("getting max connection");
        return 0;
    }


    @Override
    public String getUserName()
            throws ResourceException {
        logger.debug("getting username");
        return null;
    }

}
