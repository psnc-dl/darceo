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
import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.config.WrdzModule;
import pl.psnc.synat.wrdz.common.https.HttpsClientHelper;
import pl.psnc.synat.wrdz.common.utility.UuidGenerator;
import pl.psnc.synat.wrdz.common.utility.ZipUtility;
import pl.psnc.synat.wrdz.zmkd.config.ZmkdConfiguration;
import pl.psnc.synat.wrdz.zmkd.dao.plan.DeliveryPlanDao;
import pl.psnc.synat.wrdz.zmkd.entity.plan.ConversionPath;
import pl.psnc.synat.wrdz.zmkd.entity.plan.DeliveryPlan;
import pl.psnc.synat.wrdz.zmkd.entity.plan.DeliveryPlanStatus;
import pl.psnc.synat.wrdz.zmkd.object.DigitalObjectInfo;
import pl.psnc.synat.wrdz.zmkd.object.MetsReader;
import pl.psnc.synat.wrdz.zmkd.plan.execution.DeliveryException;
import pl.psnc.synat.wrdz.zmkd.plan.execution.DeliveryInfo;
import pl.psnc.synat.wrdz.zmkd.plan.execution.InconsistentServiceDescriptionException;
import pl.psnc.synat.wrdz.zmkd.plan.execution.PlanExecutionManager;
import pl.psnc.synat.wrdz.zmkd.plan.execution.PlanExecutionParser;
import pl.psnc.synat.wrdz.zmkd.plan.execution.TransformationException;
import pl.psnc.synat.wrdz.zmkd.plan.execution.TransformationInfo;

/**
 * Default delivery plan processor implementation.
 */
@Stateless
public class DeliveryPlanProcessorBean implements DeliveryPlanProcessor {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DeliveryPlanProcessorBean.class);

    /** Mets metadata file path. */
    private static final String METS_PATH = "metadata" + File.separator + "extracted" + File.separator + "mets.xml";

    /** Delivery plan manager. */
    @EJB
    private DeliveryPlanManager planManager;

    /** Delivery plan executor. */
    @EJB
    private DeliveryPlanExecutor planExecutor;

    /** Delivery plan DAO. */
    @EJB
    private DeliveryPlanDao planDao;

    /** Plan execution parser. */
    @EJB
    PlanExecutionParser planExecutionParser;

    /** Plan execution manager. */
    @EJB
    private PlanExecutionManager planExecutionManager;

    /** Mets file reader. */
    @EJB
    private MetsReader reader;

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
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Future<Void> process(String planId) {

        DeliveryPlan plan = planDao.findById(planId);
        if (plan == null) {
            logger.error("No such plan: " + planId);
            return new AsyncResult<Void>(null);
        }

        planManager.changeStatus(planId, DeliveryPlanStatus.RUNNING);

        String objectIdentifier = plan.getObjectIdentifier();

        HttpClient client = httpsClientHelper.getHttpsClient(WrdzModule.ZMKD);

        HttpGet get = new HttpGet(zmkdConfiguration.getZmdObjectUrl(objectIdentifier));
        HttpResponse response = null;

        File digitalObjectFile;
        File workDir;

        try {

            planExecutor.setWait(planId, objectIdentifier);

            response = client.execute(get);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_ACCEPTED) {
                planExecutor.confirmWait(planId);
                return new AsyncResult<Void>(null);
            }

            planExecutor.clearWait(planId);

            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                planManager.changeStatus(planId, DeliveryPlanStatus.ERROR);
                logger.error("Unexpected response: " + response.getStatusLine());
                return new AsyncResult<Void>(null);
            }

            workDir = new File(zmkdConfiguration.getWorkingDirectory(uuidGenerator.generateCacheFolderName()));
            workDir.mkdir();

            digitalObjectFile = httpsClientHelper.storeResponseEntity(workDir, response.getEntity(),
                response.getFirstHeader("Content-Disposition"));

            ZipUtility.unzip(digitalObjectFile, workDir);

        } catch (IOException e) {
            planExecutor.clearWait(planId);
            planManager.changeStatus(planId, DeliveryPlanStatus.ERROR);
            logger.error(e.getMessage(), e);
            return new AsyncResult<Void>(null);
        } finally {
            if (response != null) {
                EntityUtils.consumeQuietly(response.getEntity());
            }
        }

        DigitalObjectInfo objectInfo = reader.parseMets(workDir, METS_PATH);
        String clientLocation = null;

        try {
            for (ConversionPath conversion : plan.getConversionPaths()) {
                List<TransformationInfo> path = planExecutionParser.parseTransformationPath(conversion);
                planExecutionManager.transform(objectInfo, path);
            }
        } catch (InconsistentServiceDescriptionException e) {
            planManager.changeStatus(planId, DeliveryPlanStatus.ERROR);
            logger.error(e.getMessage(), e);
            return new AsyncResult<Void>(null);
        } catch (TransformationException e) {
            planManager.changeStatus(planId, DeliveryPlanStatus.ERROR);
            logger.error(e.getMessage(), e);
            return new AsyncResult<Void>(null);
        }
        try {
            DeliveryInfo deliveryInfo = planExecutionParser.parseDelivery(plan.getDelivery());
            clientLocation = planExecutionManager.deliver(objectInfo, deliveryInfo);
        } catch (InconsistentServiceDescriptionException e) {
            planManager.changeStatus(planId, DeliveryPlanStatus.ERROR);
            logger.error(e.getMessage(), e);
            return new AsyncResult<Void>(null);
        } catch (DeliveryException e) {
            planManager.changeStatus(planId, DeliveryPlanStatus.ERROR);
            logger.error(e.getMessage(), e);
            return new AsyncResult<Void>(null);
        }

        planManager.completePlan(planId, clientLocation);

        return new AsyncResult<Void>(null);
    }
}
