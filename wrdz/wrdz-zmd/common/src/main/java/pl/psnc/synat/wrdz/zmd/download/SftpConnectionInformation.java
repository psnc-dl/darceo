/**
 * 
 */
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
package pl.psnc.synat.wrdz.zmd.download;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.UserInfo;

/**
 * A class representing user's authentication information for accessing a sftp repository.
 */
public class SftpConnectionInformation extends ConnectionInformation implements UserInfo {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(SftpConnectionInformation.class);


    /**
     * Constructs new object from the given instance of {@link ConnectionInformation}.
     * 
     * @param connectionInfo
     *            object containing data about the connection
     * @throws NullPointerException
     *             if passed object is null
     */
    public SftpConnectionInformation(ConnectionInformation connectionInfo)
            throws NullPointerException {
        super(connectionInfo.getHost(), connectionInfo.getPort(), connectionInfo.getUsername(), connectionInfo
                .getPassword());
        this.certificateInfo = connectionInfo.certificateInfo;
    }


    @Override
    public boolean promptPassword(String message) {
        logger.debug("Prompting password: " + message);
        return true;
    }


    @Override
    public boolean promptPassphrase(String message) {
        logger.debug("Prompting passphrase + " + message);
        return false;
    }


    @Override
    public boolean promptYesNo(String message) {
        logger.debug("Prompting yes/no: " + message);
        return true;
    }


    @Override
    public void showMessage(String message) {
        logger.debug("Showing message: " + message);
    }


    @Override
    public String getPassphrase() {
        if (certificateInfo != null) {
            return certificateInfo.getPassphrase();
        }
        return null;
    }

}
