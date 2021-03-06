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
package pl.psnc.synat.dsa.sftp;

import javax.resource.spi.BootstrapContext;
import javax.resource.spi.ResourceAdapterInternalException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.DataStorageResourceAdapter;

/**
 * Data storage based on resource adapter to the SFTP protocol.
 * 
 */
public class SftpDataStorageResourceAdapter extends DataStorageResourceAdapter {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(SftpDataStorageResourceAdapter.class);


    @Override
    public void start(BootstrapContext ctx)
            throws ResourceAdapterInternalException {
        logger.debug("SftpDataStorageResourceAdapter started, bootstrap context: " + ctx);
    }


    @Override
    public void stop() {
        logger.debug("SftpDataStorageResourceAdapter stopped");
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("SftpDataStorageResourceAdapter ");
        sb.append("[hashCode = ").append(hashCode());
        sb.append("]");
        return sb.toString();
    }

}
