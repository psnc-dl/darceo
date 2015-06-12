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
package pl.psnc.synat.wrdz.ms.mail;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.ms.config.MsConfiguration;
import pl.psnc.synat.wrdz.ms.dao.messages.NotifyEmailDao;
import pl.psnc.synat.wrdz.ms.entity.messages.InternalMessage;
import pl.psnc.synat.wrdz.ms.entity.messages.NotifyEmail;

/**
 * Default implementation of {@link MsMailer}.
 */
@Stateless
public class MsMailerBean implements MsMailer {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MsMailerBean.class);

    /** Internal message notification email subject. */
    private static final String INTERNAL_SUBJECT = "WRDZ internal message notification";

    /** Internal message notification email body. */
    private static final String INTERNAL_BODY = "A new internal message has arrived.\n\nOrigin: %s\n\nType: %s\n\nData: %s";

    /** Certificate notification email subject. */
    private static final String CERTIFICATE_SUBJECT = "WRDZ certificate expiration warning";

    /** Certificate notification email body. */
    private static final String CERTIFICATE_BODY = "Your user certificate is nearing, or has already surpassed, its expiration date. Please contact the system administrator.";

    /** Module configuration. */
    @Inject
    private MsConfiguration configuration;

    /** Notify email DAO. */
    @EJB
    private NotifyEmailDao emailDao;

    /** Mail session for the system monitor. */
    @Resource(mappedName = "mail/ms")
    private Session session;


    @Override
    public void sendNotification(InternalMessage message) {

        List<NotifyEmail> addresses = emailDao.findAll();
        if (addresses.isEmpty()) {
            return;
        }

        try {
            MimeMessage mail = new MimeMessage(session);
            InternetAddress senderInfo = new InternetAddress(session.getProperty("mail.from.address"),
                    session.getProperty("mail.from.name"));
            mail.setFrom(senderInfo);
            for (NotifyEmail address : addresses) {
                InternetAddress recipientInfo = new InternetAddress(address.getAddress());
                mail.addRecipient(Message.RecipientType.BCC, recipientInfo);
            }
            mail.setSubject(INTERNAL_SUBJECT);
            mail.setText(String.format(INTERNAL_BODY, message.getOrigin(), message.getType().getLabel(),
                message.getData()));
            mail.saveChanges();
            Transport.send(mail, mail.getAllRecipients());

        } catch (MessagingException e) {
            logger.error("Error while sending email", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("Error while sending email", e);
        }
    }


    @Override
    public void sendCertificateExpirationWarning(String emailAddress) {

        try {
            MimeMessage mail = new MimeMessage(session);
            InternetAddress senderInfo = new InternetAddress(session.getProperty("mail.from.address"),
                    session.getProperty("mail.from.name"));
            InternetAddress recipientInfo = new InternetAddress(emailAddress);
            mail.setFrom(senderInfo);
            mail.addRecipient(Message.RecipientType.TO, recipientInfo);
            mail.setSubject(CERTIFICATE_SUBJECT);
            mail.setText(CERTIFICATE_BODY);
            mail.saveChanges();
            Transport.send(mail);

        } catch (MessagingException e) {
            logger.error("Error while sending email", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("Error while sending email", e);
        }
    }

}
