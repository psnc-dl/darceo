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
package pl.psnc.synat.wrdz.ms.messages;

import java.util.Date;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.ms.dao.messages.InternalMessageDao;
import pl.psnc.synat.wrdz.ms.entity.messages.InternalMessage;
import pl.psnc.synat.wrdz.ms.mail.MsMailer;
import pl.psnc.synat.wrdz.ms.types.InternalMessageType;

/**
 * Message-driven bean that reads format risk notifications from MDZ and persists them in the MS database.
 */
@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class FormatMessageBean implements MessageListener {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FormatMessageBean.class);

    /** Value of the message origin property. */
    private static final String MESSAGE_ORIGIN = "Source Data Monitor";

    /** Internal message DAO. */
    @EJB
    private InternalMessageDao messageDao;

    /** Mailer. */
    @EJB
    private MsMailer mailer;


    @Override
    public void onMessage(Message message) {
        try {
            InternalMessage internalMessage = new InternalMessage();
            internalMessage.setType(InternalMessageType.FORMAT_AT_RISK);
            internalMessage.setOrigin(MESSAGE_ORIGIN);
            internalMessage.setReceivedOn(new Date());
            internalMessage.setData(((TextMessage) message).getText());

            messageDao.persist(internalMessage);

            mailer.sendNotification(internalMessage);
        } catch (JMSException e) {
            logger.error("Retrieving message from the JMS queue failed!", e);
        }
    }
}
