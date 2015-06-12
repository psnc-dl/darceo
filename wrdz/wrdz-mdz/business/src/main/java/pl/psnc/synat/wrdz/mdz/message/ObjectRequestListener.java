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
package pl.psnc.synat.wrdz.mdz.message;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import pl.psnc.synat.wrdz.common.async.AsyncRequestMessage;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.mdz.integrity.IntegrityWorker;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectFetchingRequest;
import pl.psnc.synat.wrdz.zmd.object.async.ObjectAsyncRequestEnum;

/**
 * Listens to the ZMD object information topic and notifies the {@link IntegrityWorker} whenever an object becomes
 * available for download.
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ObjectRequestListener implements MessageListener {

    /** Integrity worker instance. */
    @EJB
    private IntegrityWorker integrityWorker;


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
            integrityWorker.notifyObjectAvailable(request.getIdentifier());
        }
    }
}
