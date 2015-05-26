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

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.security.auth.Subject;
import javax.security.auth.SubjectDomainCombiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.dao.async.AsyncRequestDao;
import pl.psnc.synat.wrdz.common.dao.async.AsyncRequestResultDao;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequest;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;

/**
 * Base class for all Message Driven Beans which receives asynchronous requests.
 */
public abstract class AsyncMessageDrivenBean implements MessageListener {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(AsyncMessageDrivenBean.class);

    /**
     * DAO for asynchronous requests.
     */
    @EJB
    protected AsyncRequestDao asyncRequestDaoBean;

    /**
     * DAO for asynchronous request results.
     */
    @EJB
    protected AsyncRequestResultDao asyncRequestResultDaoBean;


    @Override
    public void onMessage(Message message) {
        AsyncRequestMessage requestMessage;
        try {
            requestMessage = (AsyncRequestMessage) ((ObjectMessage) message).getObject();
        } catch (JMSException e) {
            logger.error("Retrieving message from the JMS queue failed!", e);
            throw new WrdzRuntimeException(e);
        }
        processAsyncRequestMessage(requestMessage);
    }


    /**
     * Processes a request message.
     * 
     * @param requestMessage
     *            request to process
     */
    protected void processAsyncRequestMessage(final AsyncRequestMessage requestMessage) {
        Principal userPrincipal = requestMessage.getUserPrincipal();
        logger.debug("user principal: " + userPrincipal);
        logger.debug("request id: " + requestMessage.getRequestId());
        AsyncRequest request = asyncRequestDaoBean.findById(requestMessage.getRequestId());
        if (request == null) {
            logger.debug("No such request: " + requestMessage.getRequestId());
            return;
        }
        if (request.getResult() != null) {
            logger.debug("There is already prepared a result for this request: " + requestMessage.getRequestId());
            return;
        }
        Set<Principal> principals = new HashSet<Principal>();
        principals.add(userPrincipal);
        Subject subject = new Subject(false, principals, Collections.EMPTY_SET, Collections.EMPTY_SET);
        AccessControlContext ctx = new AccessControlContext(AccessController.getContext(), new SubjectDomainCombiner(
                subject));
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {

                public Void run() {
                    dispatchMessage(requestMessage);
                    return null;
                }
            }, ctx);
        } catch (PrivilegedActionException e) {
            logger.error("Dispatching the result as a user " + userPrincipal + " failed!", e);
            throw new WrdzRuntimeException(e);
        }

    }


    /**
     * Dispatches the request message and saves its result in the database.
     * 
     * @param requestMessage
     *            request message to pass on
     */
    protected abstract void dispatchMessage(AsyncRequestMessage requestMessage);
}
