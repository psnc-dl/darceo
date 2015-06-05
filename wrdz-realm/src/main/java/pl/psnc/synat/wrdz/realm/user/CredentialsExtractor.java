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
package pl.psnc.synat.wrdz.realm.user;

import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

import pl.psnc.synat.wrdz.realm.WrdzUserRealm;

import com.sun.enterprise.security.auth.login.common.PasswordCredential;
import com.sun.enterprise.security.auth.realm.Realm;
import com.sun.logging.LogDomains;

/**
 * Provides convenient method for extraction of user's credentials from the {@link Subject} object supplied by the
 * container.
 */
public final class CredentialsExtractor {

    /**
     * System Logger.
     */
    private static final Logger logger = LogDomains.getLogger(CredentialsExtractor.class, LogDomains.SECURITY_LOGGER);


    /**
     * Hidden default constructor (utility class should not have visible constructors).
     */
    private CredentialsExtractor() {
        // empty hidden default constructor
    }


    /**
     * Extracts user's credentials from the {@link Subject} object supplied by the container.
     * 
     * @param subject
     *            credentials carrier object supplied by the container.
     * @return internal representation of user's credentials.
     * @throws LoginException
     *             if no suitable credentials have been found.
     */
    public static WrdzUserCredentials extractCredentials(Subject subject)
            throws LoginException {
        WrdzUserCredentials wrdzUser = new WrdzUserCredentials();
        boolean certificateFound = fillInCertificateCredentials(subject, wrdzUser);
        boolean simpleCredentialsFound = fillInSimpleCredentials(subject, wrdzUser);
        if (certificateFound || simpleCredentialsFound) {
            return wrdzUser;
        } else {
            logger.log(Level.SEVERE, "No authentication information found.");
            throw new LoginException("No authentication information provided.");
        }
    }


    /**
     * Fills in user's certificate credentials from the {@link Subject} object supplied by the container into the
     * internal representation passed to this method.
     * 
     * @param subject
     *            credentials carrier object supplied by the container.
     * @param wrdzUserCredentials
     *            internal representation of user's credentials to fill in.
     * @return information whether or not the certificate credentials have been found and successfully filled into
     *         internal representation passed to this method.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static boolean fillInCertificateCredentials(Subject subject, WrdzUserCredentials wrdzUserCredentials) {
        List certificateCredentials = extractCertificateCredentials(subject);
        if (certificateCredentials == null
                || !fillInCurrentRealm(WrdzUserRealm.JAAS_CONTEXT_PARAM_VALUE, wrdzUserCredentials)) {
            return false;
        }
        X509Certificate[] certificates = null;
        try {
            certificates = (X509Certificate[]) certificateCredentials
                    .toArray(new X509Certificate[certificateCredentials.size()]);
            wrdzUserCredentials.setCertificates(certificates);
        } catch (Exception ex) {
            logger.log(Level.FINE, "No Certificate(s) found: {0}", ex.toString());
            return false;
        }
        wrdzUserCredentials.setCertificatePrincipal(certificates[0].getSubjectX500Principal());
        return true;
    }


    /**
     * Extracts user's certificate credentials from the {@link Subject} object supplied by the container.
     * 
     * @param subject
     *            credentials carrier object supplied by the container.
     * @return list of objects representing certificate chain of the credentials.
     */
    @SuppressWarnings("rawtypes")
    private static List extractCertificateCredentials(Subject subject) {
        Iterator<List> credentialsIterator = subject.getPublicCredentials(List.class).iterator();
        if (!credentialsIterator.hasNext()) {
            return null;
        }
        List certificateCredentials = credentialsIterator.next();
        if (certificateCredentials == null || certificateCredentials.isEmpty()) {
            return null;
        }
        return certificateCredentials;
    }


    /**
     * Fills in user's simple credentials (username/password pair) from the {@link Subject} object supplied by the
     * container into the internal representation passed to this method.
     * 
     * @param subject
     *            credentials carrier object supplied by the container.
     * @param wrdzUserCredentials
     *            internal representation of user's credentials to fill in.
     * @return information whether or not the simple credentials have been found and successfully filled into internal
     *         representation passed to this method.
     */
    @SuppressWarnings("rawtypes")
    private static boolean fillInSimpleCredentials(Subject subject, WrdzUserCredentials wrdzUserCredentials) {
        PasswordCredential passwordCredential = null;
        Iterator privateCredentialIterator = subject.getPrivateCredentials().iterator();
        while (privateCredentialIterator.hasNext() && passwordCredential == null) {
            Object privCred = privateCredentialIterator.next();
            if (privCred instanceof PasswordCredential) {
                passwordCredential = (PasswordCredential) privCred;
            }
        }
        if (passwordCredential == null || !fillInCurrentRealm(passwordCredential.getRealm(), wrdzUserCredentials)) {
            return false;
        }
        wrdzUserCredentials.setUsername(passwordCredential.getUser());
        wrdzUserCredentials.setPassword(passwordCredential.getPassword());
        return true;
    }


    /**
     * Fills in current user realm into the internal representation passed to this method.
     * 
     * @param realmName
     *            name of the currently used realm.
     * @param wrdzUserCredentials
     *            internal representation of user's credentials to fill in.
     * @return indicator whether or not finding and filling in the current realm succeeded.
     */
    private static boolean fillInCurrentRealm(String realmName, WrdzUserCredentials wrdzUserCredentials) {
        Realm currentRealm = null;
        try {
            currentRealm = Realm.getInstance(realmName);
        } catch (Exception e) {
            logger.log(Level.FINE, "No such realm: {0}", e.toString());
            return false;
        }
        if (currentRealm == null) {
            logger.log(Level.FINE, "No realm available.");
            return false;
        }
        if (!(currentRealm instanceof WrdzUserRealm)) {
            logger.log(Level.FINE, "Bad realm");
            return false;
        }
        wrdzUserCredentials.setCurrentRealm((WrdzUserRealm) currentRealm);
        return true;
    }
}
