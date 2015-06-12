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
package pl.psnc.synat.wrdz.zmd.object.validators;

import java.util.Set;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.input.InputFile;
import pl.psnc.synat.wrdz.zmd.input.InputFileUpdate;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.object.ObjectCreationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectModificationException;

/**
 * Interface for content modification request validator.
 */
@Local
public interface ContentModificationValidator {

    /**
     * Validates the object modification request.
     * 
     * @param modificationRequest
     *            modification request object.
     * @throws ObjectModificationException
     *             if request is invalid.
     */
    void validateContentModificationRequest(ObjectModificationRequest modificationRequest)
            throws ObjectModificationException;


    /**
     * Validates the object creation request.
     * 
     * @param creationRequest
     *            creation request object.
     * @throws ObjectCreationException
     *             if request is invalid.
     */
    void validateContentCreationRequest(ObjectCreationRequest creationRequest)
            throws ObjectCreationException;


    /**
     * Checks if operations reference correct resources, i.e. for adding operation the resource has to be unlisted in
     * latest version, for modification and deletion has to be a member of latest version.
     * 
     * @param objectsVersion
     *            object's version.
     * @param filesToAdd
     *            file adding operations.
     * @param filesToModify
     *            file modification operations.
     * @param filesToRemove
     *            file removal operations.
     * @return <code>true</code> if validated positively, otherwise <code>false</code>.
     * @throws ObjectModificationException
     *             if specified object does not exist.
     */
    boolean checkIfContentOperationsReferencesAreCorrect(ContentVersion objectsVersion, Set<InputFile> filesToAdd,
            Set<InputFileUpdate> filesToModify, Set<String> filesToRemove)
            throws ObjectModificationException;


    /**
     * Checks if operations on resources are exclusive, i.e. at most one operation is performed on single resource (data
     * file or metadata file).
     * 
     * @param filesToAdd
     *            file adding operations.
     * @param filesToModify
     *            file modification operations.
     * @param filesToRemove
     *            file removal operations.
     * @return <code>true</code> if validated positively, otherwise <code>false</code>.
     */
    boolean checkIfContentOperationsAreExclusive(Set<InputFile> filesToAdd, Set<InputFileUpdate> filesToModify,
            Set<String> filesToRemove);


    /**
     * Checks if operations on metadata are exclusive, i.e. at most one operation is performed on single metadata file.
     * 
     * @param inputFileUpdate
     *            file to check for collisions in paths.
     * @return <code>true</code> if validated positively, otherwise <code>false</code>.
     */
    boolean checkIfFileMetadataOperationsAreExclusive(InputFileUpdate inputFileUpdate);


    /**
     * Checks if operations reference correct metadata, i.e. for adding operation the metadata has to be unlisted in
     * latest version, for modification and deletion it has to be a member of latest version.
     * 
     * @param version
     *            latest object's version.
     * @param fileUpdate
     *            file being modified.
     * @return <code>true</code> if validated positively, otherwise <code>false</code>.
     * @throws ObjectModificationException
     *             if at least one metadata has incorrect path.
     */
    boolean checkIfFileMetadataOperationsReferencesAreCorrect(ContentVersion version, InputFileUpdate fileUpdate)
            throws ObjectModificationException;

}
