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

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.async.AsyncReadRequestAlreadyPrepared;
import pl.psnc.synat.wrdz.common.async.AsyncReadRequestProcessorBean;
import pl.psnc.synat.wrdz.common.async.AsyncRequestNotFoundException;
import pl.psnc.synat.wrdz.common.async.AsyncRequestProcessor;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.input.object.FileFetchingRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectFetchingRequest;
import pl.psnc.synat.wrdz.zmd.object.ObjectBrowser;
import pl.psnc.synat.wrdz.zmd.object.ObjectNotFoundException;

/**
 * Bean for processing asynchronous read requests concerning object management functionality.
 */
@Stateless
public class ObjectAsyncReadRequestProcessorBean extends AsyncReadRequestProcessorBean implements
        ObjectAsyncReadRequestProcessor {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ObjectAsyncReadRequestProcessorBean.class);

    /**
     * Bean of asynchronous requests processing for object management functionality.
     */
    @EJB(beanName = "ObjectAsyncRequestProcessorBean")
    private AsyncRequestProcessor<ObjectAsyncRequestEnum> asyncRequestProcessorBean;

    /**
     * Object finder bean.
     */
    @EJB
    private ObjectBrowser objectFinder;


    @Override
    public String processGetObject(String identifier, Integer version, Boolean provided, Boolean extracted)
            throws AsyncReadRequestAlreadyPrepared, ObjectNotFoundException {
        ContentVersion objectVersion = objectFinder.getObjectsVersion(identifier, version);
        String requestedUrl = ObjectManagerAsyncHelper.getObjectUniqueContextUrl(identifier,
            objectVersion.getVersion(), provided, extracted);
        try {
            return checkResponse(requestedUrl);
        } catch (AsyncRequestNotFoundException f) {
            logger.debug("There is no prepared OK response and no processing is in progress. Start fetching.");
            ObjectFetchingRequest request = new ObjectFetchingRequest(identifier, objectVersion.getVersion(), provided,
                    extracted);
            String requestId = asyncRequestProcessorBean.processRequestAsynchronously(
                ObjectAsyncRequestEnum.FETCH_OBJECT, request, requestedUrl);
            logger.debug("Fetching started: " + requestId);
            return requestId;
        }
    }


    @Override
    public String processGetDataFiles(String identifier, Integer version, Boolean provided, Boolean extracted,
            String filesList)
            throws AsyncReadRequestAlreadyPrepared, ObjectNotFoundException {
        ContentVersion objectVersion = objectFinder.getObjectsVersion(identifier, version);
        List<String> files = ObjectManagerAsyncHelper.getSortedFilesList(filesList);
        String requestedUrl = ObjectManagerAsyncHelper.getDataFilesUniqueContextUrl(identifier,
            objectVersion.getVersion(), provided, extracted, files);
        try {
            return checkResponse(requestedUrl);
        } catch (AsyncRequestNotFoundException f) {
            logger.debug("There is no prepared OK response and no processing is in progress. Start fetching.");
            FileFetchingRequest request = new FileFetchingRequest(identifier, files);
            request.setVersion(objectVersion.getVersion());
            request.setExtracted(extracted);
            request.setProvided(provided);
            String requestId = asyncRequestProcessorBean.processRequestAsynchronously(
                ObjectAsyncRequestEnum.FETCH_FILES, request, requestedUrl);
            logger.debug("Fetching started: " + requestId);
            return requestId;
        }
    }


    @Override
    public String processGetMainFile(String identifier, Integer version, Boolean provided, Boolean extracted)
            throws AsyncReadRequestAlreadyPrepared, ObjectNotFoundException {
        ContentVersion objectVersion = objectFinder.getObjectsVersion(identifier, version);
        String requestedUrl = ObjectManagerAsyncHelper.getMainFileUniqueContextUrl(identifier,
            objectVersion.getVersion(), provided, extracted);
        try {
            return checkResponse(requestedUrl);
        } catch (AsyncRequestNotFoundException f) {
            logger.debug("There is no prepared OK response and no processing is in progress. Start fetching.");
            FileFetchingRequest request = new FileFetchingRequest(identifier);
            request.setVersion(objectVersion.getVersion());
            request.setExtracted(extracted);
            request.setProvided(provided);
            String requestId = asyncRequestProcessorBean.processRequestAsynchronously(
                ObjectAsyncRequestEnum.FETCH_MAINFILE, request, requestedUrl);
            logger.debug("Fetching started: " + requestId);
            return requestId;
        }
    }


    @Override
    public String processGetMetadata(String identifier, Integer version, Boolean provided)
            throws AsyncReadRequestAlreadyPrepared, ObjectNotFoundException {
        ContentVersion objectVersion = objectFinder.getObjectsVersion(identifier, version);
        String requestedUrl = ObjectManagerAsyncHelper.getMetadataUniqueContextUrl(identifier,
            objectVersion.getVersion(), provided);
        try {
            return checkResponse(requestedUrl);
        } catch (AsyncRequestNotFoundException f) {
            logger.debug("There is no prepared OK response and no processing is in progress. Start fetching.");
            FileFetchingRequest request = new FileFetchingRequest(identifier);
            request.setVersion(objectVersion.getVersion());
            request.setProvided(provided);
            String requestId = asyncRequestProcessorBean.processRequestAsynchronously(
                ObjectAsyncRequestEnum.FETCH_METADATA, request, requestedUrl);
            logger.debug("Fetching started: " + requestId);
            return requestId;
        }
    }

}
