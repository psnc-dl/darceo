﻿/**
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

import pl.psnc.synat.dsa.AbstractDataStorageConnection;
import pl.psnc.synat.dsa.DataStorageManagedConnection;

/**
 * Connection to data storage based on SFTP protocol.
 *
 */
public class SftpDataStorageConnection extends AbstractDataStorageConnection {

    /**
     * Creates a new connection associated with managed connection.
     *
     * @param managedConnection managed connection
     */
    public SftpDataStorageConnection(DataStorageManagedConnection managedConnection) {
        super(managedConnection);
    }
}