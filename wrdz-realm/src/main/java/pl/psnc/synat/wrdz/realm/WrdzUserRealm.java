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

import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jvnet.hk2.annotations.Service;

import pl.psnc.synat.wrdz.realm.db.WrdzUserDatabaseHandler;
import pl.psnc.synat.wrdz.realm.digest.WrdzDigestAuthHandler;
import pl.psnc.synat.wrdz.realm.user.WrdzUserCredentials;

import com.sun.enterprise.security.auth.digest.api.DigestAlgorithmParameter;
import com.sun.enterprise.security.auth.digest.api.Password;
import com.sun.enterprise.security.auth.realm.BadRealmException;
import com.sun.enterprise.security.auth.realm.DigestRealm;
import com.sun.enterprise.security.auth.realm.DigestRealmBase;
import com.sun.enterprise.security.auth.realm.IASRealm;
import com.sun.enterprise.security.auth.realm.InvalidOperationException;
import com.sun.enterprise.security.auth.realm.NoSuchRealmException;
import com.sun.enterprise.security.auth.realm.NoSuchUserException;
import com.sun.logging.LogDomains;

/**
 * WRDZ user realm handling both simple and certificate credentials.
 */
@Service
public class WrdzUserRealm extends DigestRealmBase implements DigestRealm {

    /**
     * System Logger.
     */
    private static final Logger logger = LogDomains.getLogger(WrdzUserRealm.class, LogDomains.SECURITY_LOGGER);

    /**
     * Name of the parameter storing JAAS context name of the realm.
     */
    public static final String JAAS_CONTEXT_PARAM = "jaas-context";

    /**
     * Default value for the JAAS context name parameter.
     */
    public static final String JAAS_CONTEXT_PARAM_VALUE = "wrdz-realm";

    /**
     * Authentication type.
     */
    private static final String AUTH_TYPE = "WRDZ User Realm";

    /**
     * Digest algorithm parameter name.
     */
    private static final String PARAM_DIGEST_ALGORITHM = "digest-algorithm";

    /**
     * Groups cache.
     */
    @SuppressWarnings("rawtypes")
    private Map<String, Vector> groupCache;

    /**
     * Reference to empty vector (introduced probably for optimization reasons by GlassFish team).
     */
    private Vector<String> emptyVector;

    /**
     * JAAS context name.
     */
    private String jaasCtxName;

    /**
     * Handles database operations.
     */
    private WrdzUserDatabaseHandler databaseHandler;


    @SuppressWarnings("rawtypes")
    @Override
    public void init(Properties properties)
            throws BadRealmException, NoSuchRealmException {
        super.init(properties);
        jaasCtxName = properties.getProperty(JAAS_CONTEXT_PARAM, JAAS_CONTEXT_PARAM_VALUE);
        WrdzDigestAuthHandler digestAuthHandler = new WrdzDigestAuthHandler(properties, properties.getProperty(
            PARAM_DIGEST_ALGORITHM, getDefaultDigestAlgorithm()));
        databaseHandler = new WrdzUserDatabaseHandler(properties, digestAuthHandler);
        this.setProperty(IASRealm.JAAS_CONTEXT_PARAM, jaasCtxName);
        databaseHandler.pushRealmProperties(this);
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest(toString());
        }
        groupCache = new HashMap<String, Vector>();
        emptyVector = new Vector<String>();
    }


    @Override
    public String getAuthType() {
        return AUTH_TYPE;
    }


    @SuppressWarnings("rawtypes")
    @Override
    public Enumeration getGroupNames(String username)
            throws InvalidOperationException, NoSuchUserException {
        Vector vector = groupCache.get(username);
        if (vector == null) {
            String[] groups = databaseHandler.findGroups(username);
            cacheUsersGroups(username, groups);
            vector = groupCache.get(username);
        }
        return vector.elements();
    }


    @Override
    public String getJAASContext() {
        return jaasCtxName;
    }


    /**
     * Performs user's authentication using extracted credentials against users' database.
     * 
     * @param credentials
     *            extracted user's credentials.
     * @return list of group names user belongs to or <code>null</code> if user could not be authenticated.
     */
    public String[] authenticate(WrdzUserCredentials credentials) {
        String username = credentials.getUsername();
        final X509Certificate[] certificates = credentials.getCertificates();
        if (certificates != null && certificates.length > 0) {
            for (int i = 0; i < certificates.length; i++) {
                if (databaseHandler.isUserValid(certificates[i])) {
                    username = databaseHandler.findUsername(certificates[i]);
                    credentials.setUsername(username);
                    return fetchUserGroups(username);
                }
            }
        } else if (databaseHandler.isUserValid(username, credentials.getPassword())) {
            return fetchUserGroups(username);
        }
        return null;
    }


    @Override
    public boolean validate(String username, DigestAlgorithmParameter[] digestParams) {
        final Password pass = databaseHandler.getPassword(username);
        if (pass == null) {
            return false;
        }
        return validate(pass, digestParams);
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((databaseHandler == null) ? 0 : databaseHandler.hashCode());
        result = prime * result + ((jaasCtxName == null) ? 0 : jaasCtxName.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof WrdzUserRealm)) {
            return false;
        }
        WrdzUserRealm other = (WrdzUserRealm) obj;
        if (databaseHandler == null) {
            if (other.databaseHandler != null) {
                return false;
            }
        } else if (!databaseHandler.equals(other.databaseHandler)) {
            return false;
        }
        if (jaasCtxName == null) {
            if (other.jaasCtxName != null) {
                return false;
            }
        } else if (!jaasCtxName.equals(other.jaasCtxName)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WrdzUserRealm [jaasCtxName=").append(jaasCtxName).append(", databaseHandler=")
                .append(databaseHandler).append("]");
        return builder.toString();
    }


    /**
     * Fetches names of groups specified user belongs to.
     * 
     * @param username
     *            name of the user.
     * @return array of group names.
     */
    private String[] fetchUserGroups(String username) {
        String[] groups;
        groups = databaseHandler.findGroups(username);
        groups = addAssignGroups(groups);
        cacheUsersGroups(username, groups);
        return groups;
    }


    /**
     * Caches user's groups.
     * 
     * @param username
     *            name of the user.
     * @param usersGroups
     *            groups user belongs to.
     */
    private void cacheUsersGroups(String username, String[] usersGroups) {
        Vector<String> groups = null;
        if (usersGroups == null) {
            groups = emptyVector;
        } else {
            groups = new Vector<String>(usersGroups.length + 1);
            for (int i = 0; i < usersGroups.length; i++) {
                groups.add(usersGroups[i]);
            }
        }
        synchronized (this) {
            groupCache.put(username, groups);
        }
    }

}
