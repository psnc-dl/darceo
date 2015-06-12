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
package pl.psnc.synat.wrdz.zu.certificate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.ScheduleExpression;
import javax.ejb.SessionContext;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.zu.config.ZuConfiguration;
import pl.psnc.synat.wrdz.zu.dao.user.UserAuthenticationDao;
import pl.psnc.synat.wrdz.zu.entity.user.UserAuthentication;

/**
 * Responsible for checking user certificates for expiration.
 * 
 * @see ZuConfiguration
 */
@Singleton
@Startup
public class CertificateChecker {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(CertificateChecker.class);

    /** Module configuration. */
    @Inject
    private ZuConfiguration configuration;

    /** Injected timer service. */
    @Resource
    private TimerService timerService;

    /** Injected session context. */
    @Resource
    private SessionContext ctx;

    /** User authentication DAO. */
    @EJB
    private UserAuthenticationDao userAuthDao;

    /** JMS certificate queue. */
    @Resource(mappedName = "queue/info/zu-certificate")
    private Queue certificateQueue;

    /** JMS queue connection factory for ZP module. */
    @Resource(mappedName = "jms/info")
    private QueueConnectionFactory queueConnectionFactory;


    /**
     * Creates and activates the timer for the certificate checking task.
     */
    @PostConstruct
    protected void init() {
        ScheduleExpression expression = configuration.getCertificateCheckSchedule();
        timerService.createCalendarTimer(expression);
    }


    /**
     * Checks the user certificates and reports the ones that are beyond expiration threshold.
     * 
     * @param timer
     *            timer that triggered the event
     */
    @Timeout
    protected void onTimeout(Timer timer) {
        CertificateChecker proxy = ctx.getBusinessObject(CertificateChecker.class);

        logger.info("Certificate checking started");
        long start = System.currentTimeMillis();
        proxy.checkCertificates();
        logger.info("Certificate checking finished (took " + (System.currentTimeMillis() - start) + " ms)");
    }


    /**
     * Checks whether any active users' certificates are beyond the expiration threshold.
     */
    @TransactionAttribute
    public void checkCertificates() {

        List<UserAuthentication> auths = userAuthDao.findAll();

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, configuration.getCertificateCheckThreshold());
        Date cutoff = cal.getTime();

        for (UserAuthentication auth : auths) {
            if (auth.getActive() && auth.getCertificate() != null) {
                try {
                    X509Certificate certificate = X509Certificate
                            .getInstance(Base64.decodeBase64(auth.getCertificate()));
                    if (cutoff.after(certificate.getNotAfter())) {
                        notifyExpirationCheckFail(auth.getUser().getUsername());
                    }
                } catch (CertificateException e) {
                    logger.warn("Certificate could not be read", e);
                }
            }
        }
    }


    /**
     * Notifies the system monitor that the given user has a certificate that's beyond the expiration threshold.
     * 
     * @param username
     *            name of the user with the (nearly) expired certificate
     */
    private void notifyExpirationCheckFail(String username) {
        QueueConnection connection = null;
        try {
            connection = queueConnectionFactory.createQueueConnection();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            TextMessage message = session.createTextMessage();
            message.setText(username);
            session.createProducer(certificateQueue).send(message);
        } catch (JMSException e) {
            logger.error("Sending message to the JMS queue failed", e);
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
