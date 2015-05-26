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
package pl.psnc.synat.wrdz.common.metadata.tech.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import pl.psnc.synat.wrdz.common.metadata.tech.ExtractedMetadata;

/**
 * Remote interface for calling the FITS tool.
 */
public interface FitsTechMetadataExtractor extends Remote {

    /**
     * Extracts technical metadata of the file specified by the path.
     * 
     * @param path
     *            path of the file on local file system.
     * @return extracted metadata
     * @throws RemoteException
     *             when something went wrong
     */
    ExtractedMetadata extractTechMetadata(String path)
            throws RemoteException;

}
