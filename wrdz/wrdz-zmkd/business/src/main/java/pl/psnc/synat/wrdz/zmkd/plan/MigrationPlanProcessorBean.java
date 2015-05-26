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
package pl.psnc.synat.wrdz.zmkd.plan;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.config.WrdzModule;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.common.https.HttpsClientHelper;
import pl.psnc.synat.wrdz.common.utility.UuidGenerator;
import pl.psnc.synat.wrdz.common.utility.ZipUtility;
import pl.psnc.synat.wrdz.zmd.entity.types.MigrationType;
import pl.psnc.synat.wrdz.zmd.object.IdentifierBrowser;
import pl.psnc.synat.wrdz.zmkd.config.ZmkdConfiguration;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationItemLogDao;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationItemLogFilterFactory;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationItemLog;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationItemStatus;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan;
import pl.psnc.synat.wrdz.zmkd.object.DataFileInfo;
import pl.psnc.synat.wrdz.zmkd.object.DigitalObjectInfo;
import pl.psnc.synat.wrdz.zmkd.object.MetsReader;
import pl.psnc.synat.wrdz.zmkd.plan.execution.InconsistentServiceDescriptionException;
import pl.psnc.synat.wrdz.zmkd.plan.execution.PlanExecutionManager;
import pl.psnc.synat.wrdz.zmkd.plan.execution.PlanExecutionParser;
import pl.psnc.synat.wrdz.zmkd.plan.execution.TransformationException;
import pl.psnc.synat.wrdz.zmkd.plan.execution.TransformationInfo;
import pl.psnc.synat.wrdz.zu.dto.user.UserDto;
import pl.psnc.synat.wrdz.zu.permission.ObjectPermissionManager;
import pl.psnc.synat.wrdz.zu.types.ObjectPermissionType;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Default migration plan processor implementation.
 */
@Stateless
public class MigrationPlanProcessorBean implements MigrationPlanProcessor {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MigrationPlanProcessorBean.class);

    /** Mets metadata file path. */
    private static final String METS_PATH = "metadata" + File.separator + "extracted" + File.separator + "mets.xml";

    /** Digital object's content path prefix (in paths retrieved from METS file). */
    private static final String CONTENT_PREFIX = "content/";

    /** Http parameter for the origin object identifier. */
    private static final String ORIGIN_ID = "origin-id";

    /** Http parameter for the origin (migration) type. */
    private static final String ORIGIN_TYPE = "origin-type";

    /** Http parameter for the origin (migration) date. */
    private static final String ORIGIN_DATE = "origin-date";

    /** Format of the {@link ORIGIN_DATE} parameter value. */
    private static final String ORIGIN_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /** Http parameter for the file content. */
    private static final String FILE_SRC = "file-%d-src";

    /** Http parameter for the file destination name. */
    private static final String FILE_DEST = "file-%d-dest";

    /** Http parameter for the file sequence value. */
    private static final String FILE_SEQ = "file-%d-seq";

    /** Character encoding. */
    private static final Charset ENCODING = Charset.forName("UTF-8");

    /** Injected session context. */
    @Resource
    private SessionContext ctx;

    /** Migration plan manager. */
    @EJB
    private MigrationPlanManager migrationPlanManager;

    /** Migration item manager. */
    @EJB
    private MigrationItemManager migrationItemManager;

    /** Migration item log bean. */
    @EJB
    private MigrationItemLogDao migrationItemLogDao;

    /** Migration path info retriever. */
    @EJB
    private MigrationPathRetriever migrationPathRetriever;

    /** Plan execution parser. */
    @EJB
    PlanExecutionParser planExecutionParser;

    /** Plan execution manager. */
    @EJB
    private PlanExecutionManager planExecutionManager;

    /** Mets file reader. */
    @EJB
    private MetsReader reader;

    /** Identifier browser. */
    @EJB(name = "IdentifierBrowser")
    private IdentifierBrowser identifierBrowser;

    /** Object permission manager. */
    @EJB(name = "ObjectPermissionManager")
    private ObjectPermissionManager objectPermissionManager;

    /** User browser. */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;

    /** HTTPS client helper. */
    @Inject
    private HttpsClientHelper httpsClientHelper;

    /** Generates cache identifiers. */
    @Inject
    private UuidGenerator uuidGenerator;

    /** Module configuration. */
    @Inject
    private ZmkdConfiguration zmkdConfiguration;


    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<Void> processAll(long planId) {
        MigrationPlanProcessor proxy = ctx.getBusinessObject(MigrationPlanProcessor.class);

        boolean finished = false;

        List<TransformationInfo> path = null;
        try {
            path = planExecutionParser.parseTransformationPath(migrationPathRetriever.retrieveActivePath(planId));
        } catch (InconsistentServiceDescriptionException e) {
            logger.error(e.getMessage(), e);
            finished = true;
        }

        while (!ctx.wasCancelCalled() && !finished) {
            try {
                MigrationProcessingResult migrationProcessingResult = proxy.processOne(planId, path);
                switch (migrationProcessingResult) {
                    case PROCESSED:
                        finished = false;
                        break;
                    case PAUSED:
                        finished = true;
                        break;
                    case FINISHED:
                        finished = true;
                        migrationPlanManager.logFinished(planId);
                        break;
                    default:
                        throw new WrdzRuntimeException("Unexpected result: " + migrationProcessingResult);
                }
            } catch (MigrationProcessingException e) {
                logger.error(e.getMessage(), e);
            } catch (MigrationPlanNotFoundException nfe) {
                throw new WrdzRuntimeException("Plan not found", nfe);
            }
        }

        return new AsyncResult<Void>(null);
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public MigrationProcessingResult processOne(long planId, List<TransformationInfo> path)
            throws MigrationProcessingException, MigrationPlanNotFoundException {

        MigrationPlan plan = migrationPlanManager.getMigrationPlanById(planId);
        UserDto owner = userBrowser.getUser(plan.getOwnerId());
        if (owner == null) {
            throw new WrdzRuntimeException("Missing owner");
        }

        String objectIdentifier = getCurrentObjectIdentifier(planId);
        if (objectIdentifier == null) {
            return MigrationProcessingResult.FINISHED;
        }

        Long objectId = identifierBrowser.getObjectId(objectIdentifier);
        if (objectId == null) {
            migrationItemManager.logError(planId, objectIdentifier, null);
            throw new MigrationProcessingException(planId, "Object does not exist: " + objectIdentifier);
        }
        if (!objectPermissionManager.hasPermission(owner.getUsername(), objectId, ObjectPermissionType.METADATA_UPDATE)) {
            migrationItemManager.logPermissionError(planId, objectIdentifier, null);
            throw new MigrationProcessingException(planId, "Insufficient access rights: " + objectIdentifier);
        }

        migrationItemManager.logMigrationStarted(planId, objectIdentifier);

        HttpClient client = httpsClientHelper.getHttpsClient(WrdzModule.ZMKD);

        HttpGet get = new HttpGet(zmkdConfiguration.getZmdObjectUrl(objectIdentifier));
        HttpResponse response = null;

        File digitalObjectFile;
        File workDir;

        try {

            synchronized (this) {
                response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
                    migrationPlanManager.logWaitingForObject(planId, objectIdentifier);
                    return MigrationProcessingResult.PAUSED;
                }
            }

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                migrationItemManager.logFetchingError(planId, objectIdentifier, response.getStatusLine()
                        .getStatusCode() + "");
                throw new MigrationProcessingException(planId, "Could not fetch object from ZMD: "
                        + response.getStatusLine());
            }

            workDir = new File(zmkdConfiguration.getWorkingDirectory(uuidGenerator.generateCacheFolderName()));
            workDir.mkdir();

            digitalObjectFile = httpsClientHelper.storeResponseEntity(workDir, response.getEntity(),
                response.getFirstHeader("Content-Disposition"));

            ZipUtility.unzip(digitalObjectFile, workDir);

        } catch (IOException e) {
            migrationItemManager.logFetchingError(planId, objectIdentifier, null);
            throw new MigrationProcessingException(planId, "Could not fetch object from ZMD", e);
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }

        DigitalObjectInfo objectInfo = reader.parseMets(workDir, METS_PATH);
        try {
            planExecutionManager.transform(objectInfo, path);
        } catch (TransformationException e) {
            migrationItemManager.logServiceError(planId, objectIdentifier, e.getServiceIri());
            throw new MigrationProcessingException(planId, "Transformation failed", e);
        }

        Map<String, File> targetFiles = new HashMap<String, File>();
        Map<String, Integer> fileSequence = new HashMap<String, Integer>();
        for (DataFileInfo dataFileInfo : objectInfo.getFiles()) {
            String filename = dataFileInfo.getPath();
            if (filename.startsWith(CONTENT_PREFIX)) {
                filename = filename.substring(CONTENT_PREFIX.length());
            }
            targetFiles.put(filename, dataFileInfo.getFile());
            if (dataFileInfo.getSequence() != null) {
                fileSequence.put(filename, dataFileInfo.getSequence());
            }
        }

        MigrationType originType;
        switch (objectInfo.getType()) {
            case MASTER:
                originType = MigrationType.TRANSFORMATION;
                break;
            case OPTIMIZED:
                originType = MigrationType.OPTIMIZATION;
                break;
            case CONVERTED:
                originType = MigrationType.CONVERSION;
                break;
            default:
                throw new WrdzRuntimeException("Unexpected type: " + objectInfo.getType());
        }

        try {
            String requestId = saveObject(client, targetFiles, fileSequence, objectIdentifier, originType);
            migrationItemManager.logUploaded(planId, objectIdentifier, requestId);
        } catch (IOException e) {
            migrationItemManager.logCreationError(planId, objectIdentifier, null);
            throw new MigrationProcessingException(planId, "Upload failed", e);
        }

        return MigrationProcessingResult.PROCESSED;
    }


    /**
     * Creates a new object in ZMD.
     * 
     * @param client
     *            http client instance
     * @param files
     *            map of the new object's files; keys contain the file names/paths inside the new object's structure
     * @param fileSequence
     *            map of the new object's file sequence values; keys contain the file names/paths inside the new
     *            object's structure
     * @param originIdentifier
     *            identifier of the object the new object was migrated from
     * @param originType
     *            the type of migration performed
     * @return creation request identifier
     * @throws IOException
     *             if object upload fails
     */
    private String saveObject(HttpClient client, Map<String, File> files, Map<String, Integer> fileSequence,
            String originIdentifier, MigrationType originType)
            throws IOException {

        DateFormat format = new SimpleDateFormat(ORIGIN_DATE_FORMAT);

        HttpPost post = new HttpPost(zmkdConfiguration.getZmdObjectUrl());
        MultipartEntity entity = new MultipartEntity();

        try {

            entity.addPart(ORIGIN_ID, new StringBody(originIdentifier, ENCODING));
            entity.addPart(ORIGIN_TYPE, new StringBody(originType.name(), ENCODING));
            entity.addPart(ORIGIN_DATE, new StringBody(format.format(new Date()), ENCODING));

            int i = 0;
            for (Entry<String, File> dataFile : files.entrySet()) {
                FileBody file = new FileBody(dataFile.getValue());
                StringBody path = new StringBody(dataFile.getKey(), ENCODING);

                entity.addPart(String.format(FILE_SRC, i), file);
                entity.addPart(String.format(FILE_DEST, i), path);

                if (fileSequence.containsKey(dataFile.getKey())) {
                    StringBody seq = new StringBody(fileSequence.get(dataFile.getKey()).toString(), ENCODING);
                    entity.addPart(String.format(FILE_SEQ, i), seq);
                }

                i++;
            }
        } catch (UnsupportedEncodingException e) {
            throw new WrdzRuntimeException("The encoding " + ENCODING + " is not supported");
        }

        post.setEntity(entity);

        HttpResponse response = client.execute(post);
        EntityUtils.consumeQuietly(response.getEntity());
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
            String location = response.getFirstHeader("location").getValue();
            return location.substring(location.lastIndexOf('/') + 1);
        } else {
            throw new IOException("Unexpected response: " + response.getStatusLine());
        }
    }


    /**
     * Returns the identifier of a digital object to be processed.
     * 
     * If that last object stored in the local has already been processed, this method retrieves the next object's
     * identifier and sets the new status for this identifier before returning it.
     * 
     * @param planId
     *            migration plan identifier
     * @return current the identifier of a digital object to be processed, or <code>null</code> if there are no more
     *         'next' objects
     */
    private String getCurrentObjectIdentifier(long planId) {
        MigrationItemLogFilterFactory filterFactory = migrationItemLogDao.createQueryModifier().getQueryFilterFactory();

        QueryFilter<MigrationItemLog> filterInProgress = filterFactory.and(filterFactory.byMigrationPlan(planId),
            filterFactory.byStatus(MigrationItemStatus.IN_PROGRESS));
        MigrationItemLog migrationItemLogInProgress = migrationItemLogDao.findFirstResultBy(filterInProgress);

        if (migrationItemLogInProgress != null) {
            return migrationItemLogInProgress.getObjectIdentifier();
        }

        QueryFilter<MigrationItemLog> filterNotYetStarted = filterFactory.and(filterFactory.byMigrationPlan(planId),
            filterFactory.byStatus(MigrationItemStatus.NOT_YET_STARTED));

        MigrationItemLog migrationItemLogNotYetStarted = migrationItemLogDao.findFirstResultBy(filterNotYetStarted);
        if (migrationItemLogNotYetStarted != null) {
            return migrationItemLogNotYetStarted.getObjectIdentifier();
        }
        return null;
    }

}
