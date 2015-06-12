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

import javax.ejb.Remote;

import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile;

/**
 * Describes interface of {@link ObjectManager} helper bean.
 */
@Remote
public interface ObjectChecker {

    /**
     * Checks if object with specified identifier exists.
     * 
     * @param identifier
     *            public identifier of the object.
     * @return whether the digital object exists
     */
    boolean checkIfDigitalObjectExists(String identifier);


    /**
     * Tests whether the digital object at the public identifier exists. If version is passed, tests whether this
     * version exists.
     * 
     * @param identifier
     *            public identifier of the object
     * @param versionNo
     *            number of version of the object or <code>null</code> if existence of current version is checked.
     * @return whether the digital object version exists
     */
    boolean checkIfObjectsVersionExists(String identifier, Integer versionNo);


    /**
     * Checks whether or not the specified content version contains specified file.
     * 
     * @param path
     *            object-relative path to file.
     * @param version
     *            content version to search in.
     * @return whether the content version contains specified file.
     */
    boolean checkIfDataFileExistsInVersion(String path, ContentVersion version);


    /**
     * Checks whether or not the specified content version contains specified file's metadata.
     * 
     * @param name
     *            object-relative path to metadata.
     * @param version
     *            content version to search in.
     * @param dataFile
     *            data file for which metadata was specified.
     * @return whether the content version contains specified file.
     */
    boolean checkIfFileProvidedMetadataExistsInVersion(String name, ContentVersion version, DataFile dataFile);


    /**
     * Checks whether or not the specified content version contains specified metadata.
     * 
     * @param name
     *            object-relative path to metadata.
     * @param version
     *            content version to search in.
     * @return whether the content version contains specified metadata.
     */
    boolean checkIfObjectProvidedMetadataExistsInVersion(String name, ContentVersion version);


    /**
     * Checks whether the current version of the object with the given identifier contains data files in the format with
     * the given PUID.
     * 
     * @param identifier
     *            object identifier
     * @param formatPuid
     *            data file format PUID
     * @return <code>true</code> if at least one matching data file exists; <code>false</code> otherwise
     */
    boolean containsDataFiles(String identifier, String formatPuid);
}
