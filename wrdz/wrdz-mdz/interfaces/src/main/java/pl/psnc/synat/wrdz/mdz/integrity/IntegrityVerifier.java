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
package pl.psnc.synat.wrdz.mdz.integrity;

import java.io.File;

import javax.ejb.Local;

/**
 * Checks whether the data integrity of digital objects has been compromised.
 */
@Local
public interface IntegrityVerifier {

    /**
     * Calculates hashes for the given object's files and checks them against the hash values stored in the ZMD
     * database.
     * 
     * @param objectIdentifier
     *            identifier of the digital object
     * @param objectFile
     *            zip archive containing the full digital object
     * @return <code>true</code> if one of the files is missing or its hash values do not match; <code>false</code>
     *         otherwise
     */
    boolean isCorrupted(String objectIdentifier, File objectFile);
}
