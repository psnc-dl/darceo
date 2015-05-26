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
package pl.psnc.synat.wrdz.zmd.object.async;

import java.io.IOException;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;
import pl.psnc.synat.wrdz.zmd.input.object.FileFetchingRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectDeletionRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectFetchingRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectVersionDeletionRequest;
import pl.psnc.synat.wrdz.zmd.object.FetchingException;
import pl.psnc.synat.wrdz.zmd.object.FileNotFoundException;
import pl.psnc.synat.wrdz.zmd.object.ObjectCreationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectDeletionException;
import pl.psnc.synat.wrdz.zmd.object.ObjectModificationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectNotFoundException;
import pl.psnc.synat.wrdz.zu.exceptions.NotAuthorizedException;

/**
 * Processes asynchronous requests related to object management.
 */
@Local
public interface ObjectRequestProcessor {

    /**
     * Creates a digital object (by the @ObjectManager interface) and prepares response for fetching services.
     * 
     * @param request
     *            request
     * @param requestId
     *            request identifier
     * @return request result
     * @throws ObjectCreationException
     *             when object creation failed
     * @throws NotAuthorizedException
     *             if the user does not have the create permission
     * @throws IOException
     *             if there was a problem saving the request result file
     */
    AsyncRequestResult createObject(ObjectCreationRequest request, String requestId)
            throws ObjectCreationException, NotAuthorizedException, IOException;


    /**
     * Modifies the digital object (by the @ObjectManager interface) and prepares response for fetching services.
     * 
     * @param request
     *            request
     * @param requestId
     *            request identifier
     * @return request result
     * @throws ObjectModificationException
     *             when object creation failed
     * @throws ObjectNotFoundException
     *             if the object does not exist
     * @throws NotAuthorizedException
     *             if the user does not have the update permission
     * @throws IOException
     *             if there was a problem saving the request result file
     */
    AsyncRequestResult modifyObject(ObjectModificationRequest request, String requestId)
            throws ObjectModificationException, ObjectNotFoundException, NotAuthorizedException, IOException;


    /**
     * Deletes a version of a digital object (by the @ObjectManager interface) and prepares response for fetching
     * services.
     * 
     * @param request
     *            request
     * @param requestId
     *            request identifier
     * @return request result
     * @throws ObjectDeletionException
     *             when object creation failed
     * @throws ObjectNotFoundException
     *             if the object does not exist
     * @throws NotAuthorizedException
     *             if the user does not have the delete permission
     * @throws IOException
     *             if there was a problem saving the request result file
     */
    AsyncRequestResult deleteVersion(ObjectVersionDeletionRequest request, String requestId)
            throws ObjectDeletionException, ObjectNotFoundException, NotAuthorizedException, IOException;


    /**
     * Deletes a digital object and its all versions (by the @ObjectManager interface) and prepares response for
     * fetching services.
     * 
     * @param request
     *            request
     * @param requestId
     *            request identifier
     * @return request result
     * @throws ObjectDeletionException
     *             when object creation failed
     * @throws ObjectNotFoundException
     *             if the object does not exist
     * @throws NotAuthorizedException
     *             if the user does not have the delete permission
     */
    AsyncRequestResult deleteObject(ObjectDeletionRequest request, String requestId)
            throws ObjectDeletionException, ObjectNotFoundException, NotAuthorizedException;


    /**
     * Fetches the digital object (by the @ObjectManager interface) and prepares response with fetched contents.
     * 
     * @param request
     *            request
     * @param requestId
     *            request identifier
     * @return request result
     * @throws ObjectNotFoundException
     *             - if object or it's version could not be found (does not exist).
     * @throws FetchingException
     *             - if any problems with data storage occur.
     * @throws NotAuthorizedException
     *             if the user does not have the read permission
     * @throws IOException
     *             if there was a problem saving the request result file
     */
    AsyncRequestResult fetchObject(ObjectFetchingRequest request, String requestId)
            throws ObjectNotFoundException, FetchingException, NotAuthorizedException, IOException;


    /**
     * Fetches the digital object's files (by the @ObjectManager interface) and prepares response with fetched contents.
     * 
     * @param request
     *            request
     * @param requestId
     *            request identifier
     * @return request result
     * @throws FileNotFoundException
     *             - if any of the specified files could not be found (does not exist).
     * @throws ObjectNotFoundException
     *             - if object or it's version could not be found (does not exist).
     * @throws FetchingException
     *             - if any problems with data storage occur.
     * @throws NotAuthorizedException
     *             if the user does not have the read permission
     * @throws IOException
     *             if there was a problem saving the request result file
     */
    AsyncRequestResult fetchFiles(FileFetchingRequest request, String requestId)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException, NotAuthorizedException,
            IOException;


    /**
     * Fetches the digital object's main file (by the @ObjectManager interface) and prepares response with fetched
     * contents.
     * 
     * @param request
     *            request
     * @param requestId
     *            request identifier
     * @return request result
     * @throws FileNotFoundException
     *             - if no main file not be found (object has no main file specified).
     * @throws ObjectNotFoundException
     *             - if object or it's version could not be found (does not exist).
     * @throws FetchingException
     *             - if any problems with data storage occur.
     * @throws NotAuthorizedException
     *             if the user does not have the read permission
     * @throws IOException
     *             if there was a problem saving the request result file
     */
    AsyncRequestResult fetchMainFile(FileFetchingRequest request, String requestId)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException, NotAuthorizedException,
            IOException;


    /**
     * Fetches the digital object's metadata (by the @ObjectManager interface) and prepares response with fetched
     * contents.
     * 
     * @param request
     *            request.
     * @param requestId
     *            request identifier
     * @return request result
     * @throws FileNotFoundException
     *             - if any no matching metadata files could not be found (do not exist).
     * @throws ObjectNotFoundException
     *             - if object or it's version could not be found (does not exist).
     * @throws FetchingException
     *             - if any problems with data storage occur.
     * @throws NotAuthorizedException
     *             if the user does not have the read permission
     * @throws IOException
     *             if there was a problem saving the request result file
     */
    AsyncRequestResult fetchMetadata(FileFetchingRequest request, String requestId)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException, NotAuthorizedException,
            IOException;
}
