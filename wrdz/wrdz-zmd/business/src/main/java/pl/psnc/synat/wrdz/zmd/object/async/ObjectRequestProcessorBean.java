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

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.common.async.AsyncRequestResultManager;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResultConsts;
import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.input.object.FileFetchingRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectDeletionRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectFetchingRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectVersionDeletionRequest;
import pl.psnc.synat.wrdz.zmd.object.FetchingException;
import pl.psnc.synat.wrdz.zmd.object.FileNotFoundException;
import pl.psnc.synat.wrdz.zmd.object.ObjectBrowser;
import pl.psnc.synat.wrdz.zmd.object.ObjectCreationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectDeletionException;
import pl.psnc.synat.wrdz.zmd.object.ObjectManager;
import pl.psnc.synat.wrdz.zmd.object.ObjectModificationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectNotFoundException;
import pl.psnc.synat.wrdz.zmd.output.ResultFile;
import pl.psnc.synat.wrdz.zu.exceptions.NotAuthorizedException;
import pl.psnc.synat.wrdz.zu.permission.ObjectPermissionManager;
import pl.psnc.synat.wrdz.zu.types.ObjectPermissionType;

/**
 * Default implementation of {@link ObjectRequestProcessor}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ObjectRequestProcessorBean implements ObjectRequestProcessor {

    /** Object management bean. */
    @EJB
    ObjectManager objectManagerBean;

    /** Object browser bean. */
    @EJB
    ObjectBrowser objectBrowser;

    /** Object permission manager. */
    @EJB(name = "ObjectPermissionManager")
    ObjectPermissionManager permissionManager;

    /** User context. */
    @EJB
    UserContext userContext;

    /** AsyncRequestResultManager. */
    @EJB
    AsyncRequestResultManager resultManager;


    @Override
    public AsyncRequestResult createObject(ObjectCreationRequest request, String requestId)
            throws ObjectCreationException, NotAuthorizedException, IOException {

        permissionManager.checkPermission(userContext.getCallerPrincipalName(), null, ObjectPermissionType.CREATE);

        String objectId = objectManagerBean.createObject(request);
        AsyncRequestResult result = resultManager.prepareResult(requestId);
        resultManager.saveResultString(result.getId(), objectId);
        result.setCode(AsyncRequestResultConsts.HTTP_CODE_OK);
        result.setContentType(AsyncRequestResultConsts.CONTENT_TYPE_TEXT_PLAIN
                + AsyncRequestResultConsts.CONTENT_TYPE_CHARSET_UTF8);
        return result;
    }


    @Override
    public AsyncRequestResult modifyObject(ObjectModificationRequest request, String requestId)
            throws ObjectModificationException, ObjectNotFoundException, NotAuthorizedException, IOException {

        checkPermission(request.getIdentifier(), ObjectPermissionType.UPDATE);

        int version = objectManagerBean.modifyObject(request);
        AsyncRequestResult result = resultManager.prepareResult(requestId);
        resultManager.saveResultString(result.getId(), Integer.toString(version));
        result.setCode(AsyncRequestResultConsts.HTTP_CODE_OK);
        result.setContentType(AsyncRequestResultConsts.CONTENT_TYPE_TEXT_PLAIN
                + AsyncRequestResultConsts.CONTENT_TYPE_CHARSET_UTF8);
        return result;
    }


    @Override
    public AsyncRequestResult deleteVersion(ObjectVersionDeletionRequest request, String requestId)
            throws ObjectDeletionException, ObjectNotFoundException, NotAuthorizedException, IOException {

        checkPermission(request.getIdentifier(), ObjectPermissionType.DELETE);

        int version = objectManagerBean.deleteVersion(request);
        AsyncRequestResult result = resultManager.prepareResult(requestId);
        resultManager.saveResultString(result.getId(), Integer.toString(version));
        result.setCode(AsyncRequestResultConsts.HTTP_CODE_OK);
        result.setContentType(AsyncRequestResultConsts.CONTENT_TYPE_TEXT_PLAIN
                + AsyncRequestResultConsts.CONTENT_TYPE_CHARSET_UTF8);
        return result;
    }


    @Override
    public AsyncRequestResult deleteObject(ObjectDeletionRequest request, String requestId)
            throws ObjectDeletionException, ObjectNotFoundException, NotAuthorizedException {

        checkPermission(request.getIdentifier(), ObjectPermissionType.DELETE);

        objectManagerBean.deleteObject(request);
        AsyncRequestResult result = resultManager.prepareResult(requestId);
        result.setCode(AsyncRequestResultConsts.HTTP_CODE_OK);
        return result;
    }


    @Override
    public AsyncRequestResult fetchObject(ObjectFetchingRequest request, String requestId)
            throws ObjectNotFoundException, FetchingException, NotAuthorizedException, IOException {

        checkPermission(request.getIdentifier(), ObjectPermissionType.READ);

        ResultFile resultFile = objectManagerBean.getObject(request);
        AsyncRequestResult result = resultManager.prepareResult(requestId);
        processFetchingResult(resultFile, result);
        return result;
    }


    @Override
    public AsyncRequestResult fetchFiles(FileFetchingRequest request, String requestId)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException, NotAuthorizedException,
            IOException {

        checkPermission(request.getIdentifier(), ObjectPermissionType.READ);

        ResultFile resultFile = objectManagerBean.getContentFiles(request);
        AsyncRequestResult result = resultManager.prepareResult(requestId);
        processFetchingResult(resultFile, result);
        return result;
    }


    @Override
    public AsyncRequestResult fetchMainFile(FileFetchingRequest request, String requestId)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException, NotAuthorizedException,
            IOException {

        checkPermission(request.getIdentifier(), ObjectPermissionType.READ);

        ResultFile resultFile = objectManagerBean.getMainFile(request);
        AsyncRequestResult result = resultManager.prepareResult(requestId);
        processFetchingResult(resultFile, result);
        return result;
    }


    @Override
    public AsyncRequestResult fetchMetadata(FileFetchingRequest request, String requestId)
            throws FetchingException, ObjectNotFoundException, FileNotFoundException, NotAuthorizedException,
            IOException {

        checkPermission(request.getIdentifier(), ObjectPermissionType.READ);

        ResultFile resultFile = objectManagerBean.getMetadata(request.getIdentifier(), request.getVersion(),
            request.isProvided());
        AsyncRequestResult result = resultManager.prepareResult(requestId);
        processFetchingResult(resultFile, result);
        return result;
    }


    /**
     * Processes file containing the desired object's contents into asynchronous reply.
     * 
     * @param resultFile
     *            result file.
     * @param result
     *            result schema to fill with response metadata.
     * @throws IOException
     *             if there is a problem saving the result file
     */
    private void processFetchingResult(ResultFile resultFile, AsyncRequestResult result)
            throws IOException {
        resultManager.saveResultFile(result.getId(), resultFile.getFile().getAbsolutePath());
        result.setCode(AsyncRequestResultConsts.HTTP_CODE_OK);
        result.setContentType(AsyncRequestResultConsts.CONTENT_TYPE_APPLICATION_ZIP);
        result.setFilename(resultFile.getProposedName());
    }


    /**
     * Checks whether the current user has the required permission to the object with the given identifier.
     * 
     * @param identifier
     *            object identifier
     * @param permission
     *            required permission
     * @throws ObjectNotFoundException
     *             if the object does not exist
     * @throws NotAuthorizedException
     *             if the user does not have the permission
     */
    private void checkPermission(String identifier, ObjectPermissionType permission)
            throws ObjectNotFoundException, NotAuthorizedException {

        DigitalObject object = objectBrowser.getDigitalObject(identifier);
        if (object.getCurrentVersion() == null) {
            throw new ObjectNotFoundException("Object has been deleted.");
        }

        permissionManager.checkPermission(userContext.getCallerPrincipalName(), object.getId(), permission);
    }
}
