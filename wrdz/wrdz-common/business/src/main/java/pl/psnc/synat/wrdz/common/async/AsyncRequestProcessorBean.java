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
package pl.psnc.synat.wrdz.common.async;

import java.io.Serializable;
import java.security.Principal;
import java.util.Random;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.dao.async.AsyncRequestDao;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequest;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.common.user.UserContext;

/**
 * General bean that processes asynchronous requests. Every concrete bean that extends this bean should only provide JMS
 * queue. This resource is needed in the skeleton method <code>processRequestAsynchronously</code> that puts the request
 * to the proper queue.
 * 
 * @param <T>
 *            set of subtypes of the request that beans which extend this class can handle
 */
public abstract class AsyncRequestProcessorBean<T extends AsyncRequestEnum> implements AsyncRequestProcessor<T> {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(AsyncRequestProcessorBean.class);

    /**
     * Random number generator.
     */
    private static final Random RANDOM = new Random();

    /**
     * User context.
     */
    @EJB
    private UserContext userContext;

    /**
     * DAO for asynchronous requests.
     */
    @EJB
    private AsyncRequestDao asyncRequestDaoBean;

    /**
     * Connection factory to JMS.
     */
    @Resource(mappedName = "jms/arp")
    private QueueConnectionFactory arpConnectionFactory;


    /**
     * Generates unique request id.
     * 
     * @return request id
     */
    protected String generateRequestId() {
        return "" + System.currentTimeMillis() + Math.abs(Thread.currentThread().getName().hashCode())
                + Math.abs(RANDOM.nextLong());
    }


    @Override
    public String processRequestAsynchronously(T requestType, Serializable requestObject) {
        return processRequestAsynchronously(requestType, requestObject, null);
    }


    @Override
    public String processRequestAsynchronously(T requestType, Serializable requestObject, String requestedUrl) {
        String requestId = generateRequestId();
        logger.debug("requestId: " + requestId);
        logger.debug("requestType.name: " + requestType.getName());
        logger.debug("requestType.baseTypeName: " + requestType.getBaseTypeName());
        logger.debug("requestObject: \n" + requestObject);

        Principal userPrincipal = userContext.getCallerPrincipal();
        logger.debug("user: " + userPrincipal.getName());

        AsyncRequest request = new AsyncRequest(requestId, requestType.getBaseTypeName(), requestType.getName());
        request.setRequestedUrl(requestedUrl);
        asyncRequestDaoBean.persist(request);

        QueueConnection connection = null;
        try {
            connection = (QueueConnection) arpConnectionFactory.createConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            ObjectMessage message = session.createObjectMessage();
            message.setObject(new AsyncRequestMessage(requestObject, requestId, requestType.getName(), userPrincipal));
            session.createProducer(getQueue()).send(message);
        } catch (JMSException e) {
            logger.error("Sending message to the JMS queue failed!", e);
            throw new WrdzRuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    logger.error("Error while closing a connection.", e);
                }
            }
        }
        return requestId;
    }


    /**
     * Gets a JMS queue.
     * 
     * @return JMS queue
     */
    protected abstract Queue getQueue();

}
