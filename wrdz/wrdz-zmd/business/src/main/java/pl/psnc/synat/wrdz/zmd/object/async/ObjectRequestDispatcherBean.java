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
import java.io.Serializable;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.async.AsyncMessageDrivenBean;
import pl.psnc.synat.wrdz.common.async.AsyncRequestMessage;
import pl.psnc.synat.wrdz.common.async.AsyncRequestResultManager;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResultConsts;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
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
 * Message Driven Bean which receives asynchronous requests for object management.
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ObjectRequestDispatcherBean extends AsyncMessageDrivenBean implements MessageListener {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ObjectRequestDispatcherBean.class);

    /** Object request processor. */
    @EJB
    private ObjectRequestProcessor requestProcessor;

    /** Asynchronous request result manager. */
    @EJB
    private AsyncRequestResultManager resultManager;

    /** JMS topic on which the notice, that request processing is finished, is sent. */
    @Resource(mappedName = "topic/info/zmd-object")
    private Topic topic;

    /** JMS queue connection factory for ZP module. */
    @Resource(mappedName = "jms/info")
    private TopicConnectionFactory infoConnectionFactory;


    @Override
    protected void dispatchMessage(AsyncRequestMessage requestMessage) {
        ObjectAsyncRequestEnum type = ObjectAsyncRequestEnum.valueOf(requestMessage.getRequestType());
        Serializable request = requestMessage.getRequest();
        String requestId = requestMessage.getRequestId();

        AsyncRequestResult result = null;

        try {
            switch (type) {
                case CREATE_OBJECT:
                    result = requestProcessor.createObject((ObjectCreationRequest) request, requestId);
                    break;
                case MODIFY_OBJECT:
                    result = requestProcessor.modifyObject((ObjectModificationRequest) request, requestId);
                    break;
                case DELETE_VERSION:
                    result = requestProcessor.deleteVersion((ObjectVersionDeletionRequest) request, requestId);
                    break;
                case DELETE_OBJECT:
                    result = requestProcessor.deleteObject((ObjectDeletionRequest) request, requestId);
                    break;
                case FETCH_OBJECT:
                    result = requestProcessor.fetchObject((ObjectFetchingRequest) request, requestId);
                    break;
                case FETCH_FILES:
                    result = requestProcessor.fetchFiles((FileFetchingRequest) request, requestId);
                    break;
                case FETCH_MAINFILE:
                    result = requestProcessor.fetchMainFile((FileFetchingRequest) request, requestId);
                    break;
                case FETCH_METADATA:
                    result = requestProcessor.fetchMetadata((FileFetchingRequest) request, requestId);
                    break;
                default:
                    throw new WrdzRuntimeException("Unknown type of the asynchronous request " + type);
            }
        } catch (NotAuthorizedException e) {
            logger.error("Not authorized.", e);
            result = resultManager.prepareResult(requestId, AsyncRequestResultConsts.HTTP_CODE_FORBIDDEN);
        } catch (ObjectNotFoundException e) {
            logger.error("Requested object: " + request + " does not exist. ", e);
            result = resultManager.prepareResult(requestId, AsyncRequestResultConsts.HTTP_CODE_NOT_FOUND);
        } catch (FileNotFoundException e) {
            logger.error("One or more of specified files does not exist. ", e);
            result = resultManager.prepareResult(requestId, AsyncRequestResultConsts.HTTP_CODE_NOT_FOUND);
        } catch (ObjectCreationException e) {
            logger.error("Error during object creation procedure. ", e);
            result = resultManager.prepareResult(requestId, AsyncRequestResultConsts.HTTP_CODE_INTERNAL_SERVER_ERROR);
        } catch (ObjectModificationException e) {
            logger.error("Error during object modification procedure. ", e);
            result = resultManager.prepareResult(requestId, AsyncRequestResultConsts.HTTP_CODE_INTERNAL_SERVER_ERROR);
        } catch (ObjectDeletionException e) {
            logger.error("Error during object deletion procedure. ", e);
            result = resultManager.prepareResult(requestId, AsyncRequestResultConsts.HTTP_CODE_INTERNAL_SERVER_ERROR);
        } catch (FetchingException e) {
            logger.error("Error during content fetching procedure. ", e);
            result = resultManager.prepareResult(requestId, AsyncRequestResultConsts.HTTP_CODE_INTERNAL_SERVER_ERROR);
        } catch (IOException e) {
            logger.error("Error while saving the request result file.", e);
            result = resultManager.prepareResult(requestId, AsyncRequestResultConsts.HTTP_CODE_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Unexpected error.", e);
            result = resultManager.prepareResult(requestId, AsyncRequestResultConsts.HTTP_CODE_INTERNAL_SERVER_ERROR);
        }
        requestMessage.setResultCode(result.getCode());
        notifyAsyncRequestMessageProcessed(requestMessage);
    }


    /**
     * Forwards the given request message to a topic queue, informing its subscribers that the request concerning a
     * digital object has been successfully processed.
     * 
     * @param requestMessage
     *            request that has been processed
     */
    protected void notifyAsyncRequestMessageProcessed(AsyncRequestMessage requestMessage) {
        TopicConnection connection = null;
        try {
            connection = infoConnectionFactory.createTopicConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            ObjectMessage message = session.createObjectMessage(requestMessage);
            session.createProducer(topic).send(message);
        } catch (JMSException e) {
            throw new WrdzRuntimeException("Sending message to the JMS queue failed", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    logger.error("Error while closing a connection.", e);
                }
            }
        }
    }
}
