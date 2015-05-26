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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.config.WrdzModule;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.common.https.HttpsClientHelper;
import pl.psnc.synat.wrdz.mdz.config.MdzConfiguration;
import pl.psnc.synat.wrdz.mdz.dao.integrity.DigitalObjectDao;
import pl.psnc.synat.wrdz.mdz.entity.integrity.DigitalObject;
import pl.psnc.synat.wrdz.mdz.message.MdzMessenger;
import pl.psnc.synat.wrdz.zmd.object.IdentifierBrowser;

/**
 * Default data integrity processor implementation.
 * 
 * <p>
 * The {@link #processAll()} method works by continuously calling the {@link #processOne()} method via a proxy until all
 * formats have been processed or the processing is cancelled using the {@link Future#cancel(boolean)} method.
 * 
 * Each {@link #processOne()} execution happens in a separate transaction to ensure that errors will not cause all
 * previous processing results to be rolled back.
 */
@Singleton
public class IntegrityProcessorBean implements IntegrityProcessor {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(IntegrityProcessorBean.class);

    /** Injected session context. */
    @Resource
    private SessionContext ctx;

    /** File format DAO. */
    @EJB
    private DigitalObjectDao objectDao;

    /** Identifier browser used to fetch digital object identifiers from ZMD. */
    @EJB(name = "IdentifierBrowser")
    private IdentifierBrowser identifierBrowser;

    /** Integrity verifier. */
    @EJB
    private IntegrityVerifier integrityVerifier;

    /** Communications manager. */
    @EJB
    private MdzMessenger messenger;

    /** HTTPS client helper. */
    @Inject
    private HttpsClientHelper httpsClientHelper;

    /** Module configuration. */
    @Inject
    private MdzConfiguration configuration;

    /** Identifier of the object the processor is currently waiting for. */
    private String waitingForIdentifier;


    @Override
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<Void> processAll() {

        if (waitingForIdentifier != null) {
            logger.warn("Processing started, but processor is waiting for an object. This can cause synchronization problems.");
        }

        IntegrityProcessor proxy = ctx.getBusinessObject(IntegrityProcessor.class);

        boolean finished = false;

        while (!ctx.wasCancelCalled() && !finished) {
            IntegrityProcessingResult result = proxy.processOne();
            switch (result) {
                case PROCESSED:
                    finished = false;
                    break;
                case PAUSED:
                    finished = true;
                    break;
                case FINISHED:
                    finished = true;
                    proxy.finishCycle();
                    break;
                default:
                    throw new WrdzRuntimeException("Unexpected result: " + result);
            }
        }

        return new AsyncResult<Void>(null);
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public IntegrityProcessingResult processOne() {

        DigitalObject object = getNextObject();
        if (object == null) {
            return IntegrityProcessingResult.FINISHED;
        }

        HttpClient client = httpsClientHelper.getHttpsClient(WrdzModule.MDZ);

        HttpGet get = new HttpGet(configuration.getZmdObjectUrl(object.getIdentifier()));
        HttpResponse response = null;

        try {

            synchronized (this) {
                response = client.execute(get);

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
                    waitingForIdentifier = object.getIdentifier();
                    return IntegrityProcessingResult.PAUSED;
                }
            }

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                boolean corrupted = false;

                File file = storeTemporarily(response.getEntity());
                try {
                    corrupted = integrityVerifier.isCorrupted(object.getIdentifier(), file);
                } finally {
                    if (!file.delete()) {
                        logger.warn("Could not delete temporary file: " + file.getAbsolutePath());
                    }
                }

                if (corrupted) {
                    messenger.notifyObjectCorrupted(object.getIdentifier());
                }
                object.setVerifiedOn(new Date());
                object.setCorrect(!corrupted);

                return IntegrityProcessingResult.PROCESSED;
            } else {
                throw new WrdzRuntimeException("Unexpected response: " + response.getStatusLine());
            }

        } catch (IOException e) {
            throw new WrdzRuntimeException("Could not fetch object from ZMD", e);
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }
    }


    @Override
    public boolean isWaitingFor(String identifier) {
        synchronized (this) {
            if (waitingForIdentifier != null && waitingForIdentifier.equals(identifier)) {
                waitingForIdentifier = null;
                return true;
            }
            return false;
        }
    }


    @Override
    public void clearWait() {
        synchronized (this) {
            waitingForIdentifier = null;
        }
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void finishCycle() {
        long total = objectDao.countAll();
        long corrupted = objectDao.countCorrupted();
        Date start = objectDao.getFirstAdded();
        Date end = objectDao.getLastVerified();

        String log = String.format(
            "Finished integrity verification [%s - %s]: verified %d objects, out of which %d were corrupted", start,
            end, total, corrupted);

        logger.info(log);

        objectDao.deleteAll();
    }


    /**
     * Returns the next DigitalObject to be processed.
     * <p>
     * If that last object stored in the local has already been processed, this method retrieves the next object's
     * identifier from ZMD and persists an entity representing that identifier before returning it.
     * <p>
     * If the last processed object is also the newest object in ZMD, this method returns <code>null</code>.
     * 
     * @return next DigitalObject to be processed, or <code>null</code> if there are no more 'next' objects
     */
    private DigitalObject getNextObject() {
        DigitalObject object = objectDao.getLast();

        if (object == null || object.getVerifiedOn() != null) {
            String identifier = object != null ? object.getIdentifier() : null;
            String nextIdentifier = identifierBrowser.findNextActiveIdentifier(identifier);
            if (nextIdentifier != null) {
                object = new DigitalObject();
                object.setIdentifier(nextIdentifier);
                object.setAddedOn(new Date());
                objectDao.persist(object);
            } else {
                object = null;
            }
        }

        return object;
    }


    /**
     * Stores the given http entity in a temporary file.
     * 
     * @param entity
     *            entity to be stored
     * @return the temporary file containing the entity data
     * @throws IOException
     *             if any read/write errors occur
     */
    private File storeTemporarily(HttpEntity entity)
            throws IOException {
        File file = File.createTempFile("integrity", null);
        file.deleteOnExit();

        try {

            FileOutputStream stream = new FileOutputStream(file);
            try {
                entity.writeTo(stream);
            } finally {
                IOUtils.closeQuietly(stream);
            }

        } catch (IOException e) {
            file.delete();
            throw e;
        } finally {
            EntityUtils.consumeQuietly(entity);
        }

        return file;
    }

}
