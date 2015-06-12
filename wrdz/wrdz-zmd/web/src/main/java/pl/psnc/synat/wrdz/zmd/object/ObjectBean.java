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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipInputStream;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.richfaces.event.FileUploadEvent;

import pl.psnc.synat.wrdz.common.async.AsyncRequestFetcher;
import pl.psnc.synat.wrdz.common.async.AsyncRequestNotFoundException;
import pl.psnc.synat.wrdz.common.async.AsyncRequestProcessor;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequest;
import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.input.IncompleteDataException;
import pl.psnc.synat.wrdz.zmd.input.InvalidDataException;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.object.async.ObjectAsyncRequestEnum;
import pl.psnc.synat.wrdz.zmd.object.parser.ObjectCreationParser;
import pl.psnc.synat.wrdz.zmd.object.parser.ObjectModificationParser;
import pl.psnc.synat.wrdz.zmd.object.validators.ObjectCreationValidator;
import pl.psnc.synat.wrdz.zmd.object.validators.ObjectModificationValidator;
import pl.psnc.synat.wrdz.zu.permission.ObjectPermissionManager;
import pl.psnc.synat.wrdz.zu.types.ObjectPermissionType;

/**
 * Saves objects.
 */
@ManagedBean
@ViewScoped
public class ObjectBean {

    /** Fetches asynchronous request responses. Duh. */
    @EJB
    protected AsyncRequestFetcher asyncRequestFetcherBean;

    /** Executes requests. */
    @EJB(beanName = "ObjectAsyncRequestProcessorBean")
    private AsyncRequestProcessor<ObjectAsyncRequestEnum> asyncRequestProcessor;

    /** Creation validator. */
    @EJB
    private ObjectCreationValidator objectCreationValidator;

    /** Modification validator. */
    @EJB
    private ObjectModificationValidator objectModificationValidator;

    /** Module configuration. */
    @Inject
    private ZmdConfiguration zmdConfig;

    /** Object identifier. */
    private String identifier;

    /** Object name. */
    private String name;

    /** Uploaded object (ZIP file). */
    private File object;

    /** Current creation/modification request's identifier. */
    private String requestId;

    /** Whether the current request is still being processed. */
    private boolean inProgress;

    /** The status of the current request. */
    private Integer status;

    /** Object permission manager. */
    @EJB(name = "ObjectPermissionManager")
    private ObjectPermissionManager permissionManager;

    /** User context. */
    @EJB
    private UserContext userContext;

    /** Object browser. */
    @EJB
    private ObjectBrowser objectBrowser;


    /**
     * Checks if the current user has the required permissions.
     */
    public void init() {
        if (!FacesContext.getCurrentInstance().isPostback()) {
            if (identifier == null) {
                if (!permissionManager.hasPermission(userContext.getCallerPrincipalName(), null,
                    ObjectPermissionType.CREATE)) {
                    errorPage(HttpServletResponse.SC_FORBIDDEN);
                }
            } else {
                DigitalObject obj;
                try {
                    obj = objectBrowser.getDigitalObject(identifier);
                    if (obj.getCurrentVersion() == null) {
                        errorPage(HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                } catch (ObjectNotFoundException e) {
                    errorPage(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                if (!permissionManager.hasPermission(userContext.getCallerPrincipalName(), obj.getId(),
                    ObjectPermissionType.UPDATE)) {
                    errorPage(HttpServletResponse.SC_FORBIDDEN);
                }
            }
        }
    }


    public String getIdentifier() {
        return identifier;
    }


    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public File getObject() {
        return object;
    }


    public boolean isInProgress() {
        return inProgress;
    }


    public Integer getStatus() {
        return status;
    }


    /**
     * Handler triggered by completion of file upload.
     * 
     * @param event
     *            file upload event.
     */
    public void uploadFile(FileUploadEvent event)
            throws IOException {
        object = File.createTempFile("zmd", "zip");
        object.deleteOnExit();
        FileOutputStream stream = new FileOutputStream(object);
        try {
            IOUtils.copyLarge(event.getUploadedFile().getInputStream(), stream);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }


    /**
     * Creates a new object or a new version of an existing one using the uploaded data.
     * 
     * This operation is handled asynchronously.
     */
    public void save() {
        status = null;

        if (object == null) {
            return;
        }

        if (identifier == null) {

            ObjectCreationRequest request = null;
            try {
                ObjectCreationParser parser = new ObjectCreationParser(zmdConfig.getCacheHome() + "/"
                        + UUID.randomUUID());
                request = parser.parse(new ZipInputStream(new FileInputStream(object)), name);
                objectCreationValidator.validateObjectCreationRequest(request);

                requestId = asyncRequestProcessor.processRequestAsynchronously(ObjectAsyncRequestEnum.CREATE_OBJECT,
                    request);
                inProgress = true;
            } catch (IncompleteDataException e) {
                inProgress = false;
                status = HttpServletResponse.SC_BAD_REQUEST;
            } catch (InvalidDataException e) {
                inProgress = false;
                status = HttpServletResponse.SC_BAD_REQUEST;
            } catch (ObjectCreationException e) {
                inProgress = false;
                status = HttpServletResponse.SC_BAD_REQUEST;
            } catch (Exception e) {
                inProgress = false;
                status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            }
        } else {

            ObjectModificationRequest request = null;
            try {
                ObjectModificationParser parser = new ObjectModificationParser(zmdConfig.getCacheHome() + "/"
                        + UUID.randomUUID());
                request = parser.parse(identifier, new ZipInputStream(new FileInputStream(object)));
                objectModificationValidator.validateObjectModificationRequest(request);

                requestId = asyncRequestProcessor.processRequestAsynchronously(ObjectAsyncRequestEnum.MODIFY_OBJECT,
                    request);
                inProgress = true;
            } catch (IncompleteDataException e) {
                inProgress = false;
                status = HttpServletResponse.SC_BAD_REQUEST;
            } catch (InvalidDataException e) {
                inProgress = false;
                status = HttpServletResponse.SC_BAD_REQUEST;
            } catch (ObjectModificationException e) {
                inProgress = false;
                status = HttpServletResponse.SC_BAD_REQUEST;
            } catch (Exception e) {
                inProgress = false;
                status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            }
        }
    }


    /**
     * Updates the status of the currently running request.
     */
    public void checkStatus() {
        if (requestId != null) {
            try {
                AsyncRequest request = asyncRequestFetcherBean.getAsyncRequest(requestId);
                inProgress = request.isInProgress();
                if (!inProgress) {
                    status = request.getResult().getCode();
                    object.delete();
                }
            } catch (AsyncRequestNotFoundException e) {
                inProgress = false;
                status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            }
        }
    }


    /**
     * Displays an error page with the given http status.
     * 
     * @param status
     *            http status
     */
    private void errorPage(int status) {
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext()
                .getResponse();
        response.setStatus(status);
        FacesContext.getCurrentInstance().responseComplete();
    }
}
