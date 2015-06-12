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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.servlet.http.HttpServletResponse;

import pl.psnc.synat.wrdz.common.async.AsyncRequestFetcher;
import pl.psnc.synat.wrdz.common.async.AsyncRequestNotFoundException;
import pl.psnc.synat.wrdz.common.async.AsyncRequestProcessor;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequest;
import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectDeletionRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectVersionDeletionRequest;
import pl.psnc.synat.wrdz.zmd.object.async.ObjectAsyncRequestEnum;
import pl.psnc.synat.wrdz.zu.permission.ObjectPermissionManager;
import pl.psnc.synat.wrdz.zu.types.ObjectPermissionType;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Handles the object management view.
 */
@ManagedBean
@ViewScoped
public class ObjectsBean {

    /** Object browser. */
    @EJB
    private ObjectBrowser objectBrowser;

    /** Object permission manager. */
    @EJB(name = "ObjectPermissionManager")
    private ObjectPermissionManager permissionManager;

    /** User context. */
    @EJB
    private UserContext userContext;

    /** User browser. */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;

    /** Fetches asynchronous request responses. Duh. */
    @EJB
    protected AsyncRequestFetcher asyncRequestFetcherBean;

    /** Executes requests. */
    @EJB(beanName = "ObjectAsyncRequestProcessorBean")
    private AsyncRequestProcessor<ObjectAsyncRequestEnum> asyncRequestProcessor;

    /** Whether the current user is an admin. */
    private boolean isAdmin;

    /** Readable object identifiers. */
    private Set<Long> readableIds;

    /** Updateable object identifiers. */
    private Set<Long> updateableIds;

    /** Deletable object identifiers. */
    private Set<Long> deletableIds;

    /** Grantable object identifiers. */
    private Set<Long> grantableIds;

    /** Object versions. */
    private Map<String, List<ContentVersion>> versions;

    /** Expanded objects (for view purposes only). */
    private Map<String, Boolean> expanded;

    /** Current deletion request's identifier. */
    private String deletionRequestId;

    /** Whether the current deletion request is still being processed. */
    private boolean deletionInProgress;

    /** The status of the current deletion request. */
    private Integer deletionStatus;

    /** Digital object data model. */
    private ObjectDataModel model;

    /** Identifier filter. */
    private String identifierFilter;

    /** Name filter. */
    private String nameFilter;


    /**
     * Refreshes the underlying object model.
     */
    public void refresh() {
        isAdmin = userBrowser.isAdmin(userContext.getCallerPrincipalName());

        Set<Long> identifiers = null;
        if (!isAdmin) {
            readableIds = new HashSet<Long>(permissionManager.fetchWithPermission(userContext.getCallerPrincipalName(),
                ObjectPermissionType.READ));
            updateableIds = new HashSet<Long>(permissionManager.fetchWithPermission(
                userContext.getCallerPrincipalName(), ObjectPermissionType.UPDATE));
            deletableIds = new HashSet<Long>(permissionManager.fetchWithPermission(
                userContext.getCallerPrincipalName(), ObjectPermissionType.DELETE));
            grantableIds = new HashSet<Long>(permissionManager.fetchWithPermission(
                userContext.getCallerPrincipalName(), ObjectPermissionType.GRANT));

            identifiers = new HashSet<Long>();
            identifiers.addAll(readableIds);
            identifiers.addAll(updateableIds);
            identifiers.addAll(deletableIds);
            identifiers.addAll(grantableIds);
        }
        model = new ObjectDataModel(objectBrowser, identifiers);

        versions = new HashMap<String, List<ContentVersion>>();
        expanded = new HashMap<String, Boolean>();
    }


    /**
     * Returns the object data model.
     * 
     * @return object data model
     */
    public ObjectDataModel getModel() {
        if (model == null) {
            refresh();
        }
        return model;
    }


    public String getIdentifierFilter() {
        return identifierFilter;
    }


    /**
     * Sets the identifier filter value and propagates it to the underlying data model.
     * 
     * @param identifierFilter
     *            identifier filter value
     */
    public void setIdentifierFilter(String identifierFilter) {
        this.identifierFilter = identifierFilter;
        model.setIdentifierFilter(identifierFilter);
    }


    public String getNameFilter() {
        return nameFilter;
    }


    /**
     * Sets the name filter value and propagates it to the underlying data model.
     * 
     * @param nameFilter
     *            name filter value
     */
    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
        model.setNameFilter(nameFilter);
    }


    /**
     * Checks whether the user has the given permission to the object with the given identifier.
     * 
     * @param objectIdentifier
     *            object identifier (database primary key)
     * @param permission
     *            required permission
     * @return <code>true</code> if the user has the required permission to the given resource, <code>false</code>
     *         otherwise
     */
    public boolean hasPermission(Long objectIdentifier, String permission) {
        if (isAdmin) {
            return true;
        }

        if (ObjectPermissionType.READ.name().equals(permission.toUpperCase())) {
            return readableIds.contains(objectIdentifier);
        }
        if (ObjectPermissionType.UPDATE.name().equals(permission.toUpperCase())) {
            return updateableIds.contains(objectIdentifier);
        }
        if (ObjectPermissionType.DELETE.name().equals(permission.toUpperCase())) {
            return deletableIds.contains(objectIdentifier);
        }
        if (ObjectPermissionType.GRANT.name().equals(permission.toUpperCase())) {
            return grantableIds.contains(objectIdentifier);
        }
        return false;
    }


    /**
     * Checks whether the user has the CREATE object permission.
     * 
     * @return <code>true</code> if the user has the CREATE permission, <code>false</code> otherwise
     */
    public boolean hasCreatePermission() {
        if (isAdmin) {
            return true;
        }

        return permissionManager.hasPermission(userContext.getCallerPrincipalName(), null, ObjectPermissionType.CREATE);
    }


    /**
     * Fetches object versions.
     * 
     * @param objectIdentifier
     *            object identifier
     * @return object versions
     */
    public List<ContentVersion> getVersions(String objectIdentifier) {

        // do not preload collapsed subtables
        if (!expanded.containsKey(objectIdentifier) || !expanded.get(objectIdentifier)) {
            return Collections.emptyList();
        }

        if (!versions.containsKey(objectIdentifier)) {
            try {
                versions.put(objectIdentifier, objectBrowser.getContentVersions(objectIdentifier, false));
            } catch (ObjectNotFoundException e) {
                versions.put(objectIdentifier, Collections.<ContentVersion> emptyList());
            }
        }
        return versions.get(objectIdentifier);
    }


    public Map<String, Boolean> getExpanded() {
        return expanded;
    }


    /**
     * Deletes the object.
     * 
     * @param identifier
     *            object identifier
     */
    public void deleteObject(String identifier) {
        try {
            ObjectDeletionRequest request = new ObjectDeletionRequest(identifier);
            deletionRequestId = asyncRequestProcessor.processRequestAsynchronously(
                ObjectAsyncRequestEnum.DELETE_OBJECT, request);
            deletionInProgress = true;
        } catch (Exception e) {
            deletionInProgress = false;
            deletionStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
    }


    /**
     * Deletes the version.
     * 
     * @param objectIdentifier
     *            object identifier
     * @param version
     *            object version number
     */
    public void deleteVersion(String objectIdentifier, int version) {
        try {
            ObjectVersionDeletionRequest request = new ObjectVersionDeletionRequest(objectIdentifier, version);
            deletionRequestId = asyncRequestProcessor.processRequestAsynchronously(
                ObjectAsyncRequestEnum.DELETE_VERSION, request);
            deletionInProgress = true;
        } catch (Exception e) {
            deletionInProgress = false;
            deletionStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }
    }


    public Integer getDeletionStatus() {
        return deletionStatus;
    }


    public boolean isDeletionInProgress() {
        return deletionInProgress;
    }


    /**
     * Updates the status of the currently running deletion request.
     */
    public void checkDeletionStatus() {
        if (deletionRequestId != null) {
            try {
                AsyncRequest request = asyncRequestFetcherBean.getAsyncRequest(deletionRequestId);
                deletionInProgress = request.isInProgress();
                if (!deletionInProgress) {
                    deletionStatus = request.getResult().getCode();
                }
            } catch (AsyncRequestNotFoundException e) {
                deletionInProgress = false;
                deletionStatus = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            }
        }
    }
}
