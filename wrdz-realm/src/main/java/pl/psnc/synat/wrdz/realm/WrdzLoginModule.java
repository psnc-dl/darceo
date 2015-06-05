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
package pl.psnc.synat.wrdz.realm;

import java.security.Principal;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.glassfish.security.common.Group;
import org.glassfish.security.common.PrincipalImpl;

import pl.psnc.synat.wrdz.realm.user.CredentialsExtractor;
import pl.psnc.synat.wrdz.realm.user.WrdzUserCredentials;

import com.sun.enterprise.security.auth.realm.certificate.CertificateRealm;
import com.sun.enterprise.security.web.integration.PrincipalGroupFactory;
import com.sun.logging.LogDomains;

/**
 * WRDZ login module enabling both simple and certificate authentication.
 */
public class WrdzLoginModule implements LoginModule {

    /**
     * System Logger.
     */
    private static final Logger logger = LogDomains.getLogger(WrdzLoginModule.class, LogDomains.SECURITY_LOGGER);

    /**
     * Subject being authenticated.
     */
    private Subject subject;

    /**
     * State shared with other login modules.
     */
    protected Map<String, ?> sharedState;
    /**
     * Options configured for this LoginModule.
     */
    protected Map<String, ?> options;

    /**
     * {@link CallbackHandler} communicating with the end user.
     */
    private CallbackHandler callbackHandler;

    /**
     * Indicates whether or not authentication succeeded.
     */
    private boolean authenticated = false;

    /**
     * Indicates whether or not committing of principal information succeeded.
     */
    private boolean commited = false;

    /**
     * Internal representation of user's credentials.
     */
    private WrdzUserCredentials userCredentials;

    /**
     * User defined login module.
     */
    protected LoginModule userDefinedLoginModule = null;

    /**
     * Application name.
     */
    protected String appName = null;


    @Override
    public final void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
            Map<String, ?> options) {
        this.subject = subject;
        this.sharedState = sharedState;
        this.options = options;
        this.callbackHandler = callbackHandler;
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Login module initialized: " + this.getClass().toString());
        }
    }


    @Override
    public final boolean login()
            throws LoginException {
        extractCredentials();
        authenticateUser();
        logger.fine("JAAS login complete.");
        return true;
    }


    @Override
    public final boolean commit()
            throws LoginException {
        if (!authenticated) {
            return false;
        }
        String realmName = userCredentials.getCurrentRealm().getName();
        PrincipalImpl userPrincipal = PrincipalGroupFactory.getPrincipalInstance(userCredentials.getUsername(),
            realmName);
        userCredentials.setUserPrincipal(userPrincipal);
        Set<Principal> principalSet = subject.getPrincipals();
        if (!principalSet.contains(userPrincipal)) {
            principalSet.add(userPrincipal);
        }
        String[] groupsList = userCredentials.getGroups();
        for (int i = 0; i < groupsList.length; i++) {
            if (groupsList[i] != null) {
                Group g = PrincipalGroupFactory.getGroupInstance(groupsList[i], realmName);
                if (!principalSet.contains(g)) {
                    principalSet.add(g);
                }
            }
        }
        userCredentials = null;
        commited = true;
        logger.fine("JAAS authentication committed.");
        return true;
    }


    @Override
    public final boolean abort()
            throws LoginException {
        logger.fine("JAAS authentication aborted.");
        if (!authenticated) {
            return false;
        } else if (authenticated && !commited) {
            authenticated = false;
            userCredentials = null;
        } else {
            logout();
        }
        return true;
    }


    @Override
    public final boolean logout()
            throws LoginException {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("JAAS logout for: " + subject.toString());
        }
        subject.getPrincipals().clear();
        subject.getPublicCredentials().clear();
        subject.getPrivateCredentials().clear();
        authenticated = false;
        commited = false;
        userCredentials = null;
        return true;
    }


    public void setLoginModuleForAuthentication(LoginModule userDefinedLoginModule) {
        this.userDefinedLoginModule = userDefinedLoginModule;
    }


    /**
     * Authenticates user.
     * 
     * @throws LoginException
     *             should authentication data be incomplete or login failure occurred.
     */
    protected final void authenticateUser()
            throws LoginException {
        if (!userCredentials.isValid()) {
            String message = "Incomplete credentials: " + userCredentials;
            logger.finest(message);
            throw new LoginException(message);
        }
        String[] groupList = userCredentials.getCurrentRealm().authenticate(userCredentials);
        if (groupList == null) { // JAAS behavior
            String message = "Login failed for credentials: " + userCredentials;
            logger.finest(message);
            throw new LoginException(message);
        }
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, new StringBuilder("JDBC login succeeded for: ").append(userCredentials.toString())
                    .append("groups: ").append(Arrays.toString(groupList)).toString());
        }
        userCredentials.setGroups(Arrays.copyOf(groupList, groupList.length));
        authenticated = true;
    }


    /**
     * Extracts user's credentials passed to the login module from the container.
     * 
     * @throws LoginException
     *             if no suitable credentials have been found.
     */
    private void extractCredentials()
            throws LoginException {
        try {
            userCredentials = CredentialsExtractor.extractCredentials(subject);
        } catch (LoginException exception) {
            authenticated = false;
            throw exception;
        }
        CertificateRealm.AppContextCallback appContext = new CertificateRealm.AppContextCallback();
        try {
            callbackHandler.handle(new Callback[] { appContext });
            appName = appContext.getModuleID();
        } catch (Exception exception) {
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, "Callback handler failed with error ", exception);
            }
        }
    }
}
