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
package pl.psnc.synat.wrdz.zmkd.message;

import java.util.List;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.http.HttpStatus;

import pl.psnc.synat.wrdz.common.async.AsyncRequestMessage;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectFetchingRequest;
import pl.psnc.synat.wrdz.zmd.object.async.ObjectAsyncRequestEnum;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan;
import pl.psnc.synat.wrdz.zmkd.plan.DeliveryPlanExecutor;
import pl.psnc.synat.wrdz.zmkd.plan.MigrationItemManager;
import pl.psnc.synat.wrdz.zmkd.plan.MigrationPlanManager;
import pl.psnc.synat.wrdz.zmkd.plan.MigrationPlanProcessorsManager;

/**
 * Listens to the ZMD object information topic and notifies the {@link IntegrityWorker} whenever an object becomes
 * available for download.
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "subscriptionDurability", propertyValue = "Durable"),
        @ActivationConfigProperty(propertyName = "clientID", propertyValue = "zmkdObjectRequestListener"),
        @ActivationConfigProperty(propertyName = "subscriptionName", propertyValue = "zmkdObjectRequestListenerSub") })
public class ObjectRequestListener implements MessageListener {

    /** Migration plan processors manager. */
    @EJB
    private MigrationPlanProcessorsManager migrationPlanProcessorsManager;

    /** Migration plan manager. */
    @EJB
    private MigrationPlanManager migrationPlanManager;

    /** Migration item manager. */
    @EJB
    private MigrationItemManager migrationItemManager;

    /** Delivery plan executor. */
    @EJB
    private DeliveryPlanExecutor deliveryPlanExecutor;


    @Override
    public void onMessage(Message message) {

        AsyncRequestMessage requestMessage;
        try {
            requestMessage = (AsyncRequestMessage) ((ObjectMessage) message).getObject();
        } catch (JMSException e) {
            throw new WrdzRuntimeException(e.getMessage(), e);
        }

        if (ObjectAsyncRequestEnum.FETCH_OBJECT.name().equals(requestMessage.getRequestType())) {
            ObjectFetchingRequest request = (ObjectFetchingRequest) requestMessage.getRequest();

            deliveryPlanExecutor.notifyAvailable(request.getIdentifier());

            List<MigrationPlan> plans = migrationPlanManager.findPlansWaitingForObject(request.getIdentifier());
            for (MigrationPlan plan : plans) {
                migrationPlanProcessorsManager.notifyObjectAvailable(plan);
            }
        } else if (ObjectAsyncRequestEnum.CREATE_OBJECT.name().equals(requestMessage.getRequestType())) {
            if (requestMessage.getResultCode() != null && requestMessage.getResultCode() == HttpStatus.SC_OK) {
                migrationItemManager.logCreationSuccessful(requestMessage.getRequestId());
            } else {
                migrationItemManager.logCreationError(requestMessage.getRequestId(),
                    "" + requestMessage.getResultCode());
            }
        }
    }
}
