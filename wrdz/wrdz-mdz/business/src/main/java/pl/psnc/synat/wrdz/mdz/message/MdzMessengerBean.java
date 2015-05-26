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

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.mdz.entity.format.FileFormat;
import pl.psnc.synat.wrdz.mdz.plugin.PluginExecutionReport;

/**
 * Default implementation of the messenger interface.
 */
@Stateless
public class MdzMessengerBean implements MdzMessenger {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MdzMessengerBean.class);

    /** JMS format queue. */
    @Resource(mappedName = "queue/info/mdz-format")
    private Queue formatQueue;

    /** JMS integrity queue. */
    @Resource(mappedName = "queue/info/mdz-object")
    private Queue objectQueue;

    /** JMS plugin topic. */
    @Resource(mappedName = "topic/info/mdz-plugin")
    private Topic pluginTopic;

    /** JMS queue connection factory for ZP module. */
    @Resource(mappedName = "jms/info")
    private QueueConnectionFactory queueConnectionFactory;

    /** JMS topic connection factory for ZP module. */
    @Resource(mappedName = "jms/info")
    private TopicConnectionFactory topicConnectionFactory;


    @Override
    public void notifyMigrationRequired(FileFormat format) {
        QueueConnection connection = null;
        try {
            connection = queueConnectionFactory.createQueueConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage();
            message.setText(format.getPuid());
            session.createProducer(formatQueue).send(message);
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


    @Override
    public void notifyObjectCorrupted(String identifier) {
        QueueConnection connection = null;
        try {
            connection = queueConnectionFactory.createQueueConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage();
            message.setText(identifier);
            session.createProducer(objectQueue).send(message);
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


    @Override
    public void forwardPluginReport(PluginExecutionReport report) {
        TopicConnection connection = null;
        try {
            connection = topicConnectionFactory.createTopicConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            ObjectMessage message = session.createObjectMessage();
            message.setObject(report);
            session.createProducer(pluginTopic).send(message);
        } catch (JMSException e) {
            throw new WrdzRuntimeException("Sending message to the JMS topic failed", e);
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
