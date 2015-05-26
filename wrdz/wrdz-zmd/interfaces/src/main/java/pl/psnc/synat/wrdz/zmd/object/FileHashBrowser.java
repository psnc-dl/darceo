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
package pl.psnc.synat.wrdz.zmd.object;

import java.util.List;

import javax.ejb.Remote;

import pl.psnc.synat.wrdz.zmd.dto.object.FileHashDto;

/**
 * Provides means to obtain hashes calculated for stored digital objects' files.
 */
@Remote
public interface FileHashBrowser {

    /**
     * Returns a list of hashes for data and metadata files belonging to the digital object with the given identifier.
     * 
     * @param digitalObjectIdentifier
     *            identifier of the digital object to be searched for files
     * @return a list of hashes for data and metadata files belonging to the object
     * @throws ObjectNotFoundException
     *             if no object with the given identifier was found
     */
    List<FileHashDto> getFileHashes(String digitalObjectIdentifier)
            throws ObjectNotFoundException;
}
