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
package pl.psnc.synat.wrdz.realm.db;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import pl.psnc.synat.wrdz.realm.WrdzUserRealm;
import pl.psnc.synat.wrdz.realm.digest.WrdzDigestAuthHandler;

import com.sun.enterprise.security.auth.digest.api.Password;
import com.sun.enterprise.security.auth.realm.BadRealmException;
import com.sun.enterprise.util.Utility;
import com.sun.logging.LogDomains;

/**
 * Handles queries and connections to the user database.
 */
public class WrdzUserDatabaseHandler extends DatabaseHandler {

    /**
     * System Logger.
     */
    private static final Logger logger = LogDomains
            .getLogger(WrdzUserDatabaseHandler.class, LogDomains.SECURITY_LOGGER);

    /**
     * Queries the database for the password of the user with username specified as query param.
     */
    private final String passwordQuery;

    /**
     * Queries the database for the salt of the user with username specified as query param.
     */
    private final String saltQuery;

    /**
     * Queries the database for the group of the user with username specified as query param.
     */
    private final String groupQuery;
    /**
     * Queries the database for the certificate of the user with username specified as query param.
     */
    private final String certificateQuery;

    /**
     * Queries the database for the username of the user with certificate specified as query param.
     */
    private final String usernameCertQuery;

    /**
     * Handles message digestion.
     */
    private final WrdzDigestAuthHandler digestAuthHandler;


    /**
     * Creates new database handler and initializes it with realm properties.
     * 
     * @param properties
     *            realm properties.
     * @param digestAuthHandler
     *            digest authentication handler performing message digest.
     * @throws BadRealmException
     *             should connection data be incomplete.
     */
    public WrdzUserDatabaseHandler(Properties properties, WrdzDigestAuthHandler digestAuthHandler)
            throws BadRealmException {
        super(properties);
        this.digestAuthHandler = digestAuthHandler;
        this.passwordQuery = "SELECT " + connectionData.getPasswordColumn() + " FROM " + connectionData.getUserTable()
                + " WHERE " + connectionData.getUserNameColumn() + " = ?";
        this.saltQuery = "SELECT " + connectionData.getSaltColumn() + " FROM " + connectionData.getUserTable()
                + " WHERE " + connectionData.getUserNameColumn() + " = ?";
        this.certificateQuery = "SELECT " + connectionData.getCertificateColumn() + " FROM "
                + connectionData.getUserTable() + " WHERE " + connectionData.getUserNameColumn() + " = ?";
        this.groupQuery = "SELECT " + connectionData.getGroupNameColumn() + " FROM " + connectionData.getGroupTable()
                + " WHERE " + connectionData.getGroupTableUserNameColumn() + " = ? ";
        this.usernameCertQuery = "SELECT " + connectionData.getUserNameColumn() + " FROM "
                + connectionData.getUserTable() + " WHERE " + connectionData.getCertificateColumn() + " = ? ";
    }


    public String getPasswordQuery() {
        return passwordQuery;
    }


    public String getSaltQuery() {
        return saltQuery;
    }


    public String getGroupQuery() {
        return groupQuery;
    }


    public String getCertificateQuery() {
        return certificateQuery;
    }


    /**
     * Pushes database-related properties into the specified realm configuration.
     * 
     * @param realm
     *            realm which properties will be set.
     */
    public void pushRealmProperties(WrdzUserRealm realm) {
        connectionData.pushRealmProperties(realm);
    }


    /**
     * Retrieves user's password from the users database.
     * 
     * @param username
     *            name of the user whose password is to be retrieved.
     * @return retrieved password.
     */
    public Password getPassword(String username) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(passwordQuery);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Reader pwdReader = resultSet.getCharacterStream(1);
                final char[] password = extractFromReader(pwdReader);
                final byte[] passwordBytes = Utility.convertCharArrayToByteArray(password, null);
                return digestAuthHandler.extractPasswordInformation(password, passwordBytes);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Cannot validate user with username ", username);
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Cannot validate user", ex);
            }
        } finally {
            close(connection, statement, resultSet);
        }
        return null;
    }


    /**
     * Authenticates user using username and password he provided and comparing it to the data in the user database.
     * 
     * @param username
     *            name of the user who is to be authenticated.
     * @param password
     *            password of the user who is to be authenticated.
     * @return whether or not user data is valid (passed user data matches data in the database).
     */
    public boolean isUserValid(String username, char[] password) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean valid = false;
        byte[] salt = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(saltQuery);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                BigDecimal decimalSalt = resultSet.getBigDecimal(1);
                if (decimalSalt != null) {
                    salt = decimalSalt.toBigIntegerExact().toByteArray();
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot validate user " + username + ", exception: " + ex.toString());
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Cannot validate user", ex);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Invalid user " + username);
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Cannot validate user", ex);
            }
        } finally {
            close(connection, statement, resultSet);
        }
        try {
            char[] hashedPassword = digestAuthHandler.hashPassword(password, salt);
            connection = getConnection();
            statement = connection.prepareStatement(passwordQuery);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Reader reader = resultSet.getCharacterStream(1);
                char[] retrievedPassword = extractFromReader(reader);
                valid = digestAuthHandler.comparePasswords(hashedPassword, retrievedPassword);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot validate user " + username + ", exception: " + ex.toString());
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Cannot validate user", ex);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Invalid user " + username);
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Cannot validate user", ex);
            }
        } finally {
            close(connection, statement, resultSet);
        }
        return valid;
    }


    /**
     * Authenticates user using certificate he provided and comparing it to the data in the user database.
     * 
     * @param x509Certificate
     *            certificate of the user who is to be authenticated.
     * @return whether or not user data is valid (passed user data matches data in the database).
     */
    public boolean isUserValid(X509Certificate x509Certificate) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        boolean valid = false;
        String username = findUsername(x509Certificate);
        try {
            connection = getConnection();
            statement = connection.prepareStatement(certificateQuery);
            statement.setString(1, username);
            rs = statement.executeQuery();
            if (rs.next()) {
                X509Certificate certificateFromDB = null;
                try {
                    CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                    certificateFromDB = (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(
                            Base64.decodeBase64(rs.getString(1))));
                } catch (CertificateException e) {
                    logger.log(Level.SEVERE, "Wrong certificate format or data corrupt.", e);
                    return false;
                }
                if (certificateFromDB.equals(x509Certificate)) {
                    valid = true;
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Cannot validate user " + username + ", exception: " + ex.toString());
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Cannot validate user", ex);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Invalid user " + username);
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Cannot validate user", ex);
            }
        } finally {
            close(connection, statement, rs);
        }
        return valid;
    }


    /**
     * Finds names of groups to which specified user belongs to.
     * 
     * @param username
     *            name of the user whose groups are to be fetched.
     * @return array of groupnames user belongs to or <code>null</code> if groupnames could not be fetched.
     */
    public String[] findGroups(String username) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(groupQuery);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            final List<String> groups = new ArrayList<String>();
            while (resultSet.next()) {
                groups.add(resultSet.getString(1));
            }
            final String[] groupArray = new String[groups.size()];
            return groups.toArray(groupArray);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error while fetching groups for user " + username);
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Cannot load groups", ex);
            }
            return null;
        } finally {
            close(connection, statement, resultSet);
        }
    }


    /**
     * Fetches the username of user using the specified certificate.
     * 
     * @param x509Certificate
     *            certificate of the user who is to be authenticated.
     * @return name of the user possessing the certificate.
     */
    public String findUsername(X509Certificate x509Certificate) {
        String username = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String certificate = StringUtils.newStringUtf8(Base64.encodeBase64(x509Certificate.getEncoded()));
            connection = getConnection();
            statement = connection.prepareStatement(usernameCertQuery);
            statement.setString(1, certificate);
            rs = statement.executeQuery();
            if (rs.next()) {
                username = rs.getString(1);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Cannot validate user " + username + ", exception: " + e.toString());
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Cannot validate user", e);
            }
        } catch (CertificateEncodingException e) {
            logger.log(Level.SEVERE, "Invalid certificate data retrieved.");
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Cannot encode certificate data properly", e);
            }
        } catch (LoginException e) {
            logger.log(Level.SEVERE, "Invalid user " + username);
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Cannot validate user", e);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error " + e.getMessage());
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Unexpected error", e);
            }
        } finally {
            close(connection, statement, rs);
        }
        return username;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((connectionData == null) ? 0 : connectionData.hashCode());
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
        if (!(obj instanceof WrdzUserDatabaseHandler)) {
            return false;
        }
        WrdzUserDatabaseHandler other = (WrdzUserDatabaseHandler) obj;
        if (connectionData == null) {
            if (other.connectionData != null) {
                return false;
            }
        } else if (!connectionData.equals(other.connectionData)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WrdzUserDatabaseHandler [connectionData=").append(connectionData).append("]");
        return builder.toString();
    }


    /**
     * Reads max of 1024 characters from the stream into character array.
     * 
     * @param reader
     *            stream reader.
     * @return array of characters read from the stream.
     * @throws IOException
     *             should any I/O errors during stream reading occur.
     */
    private char[] extractFromReader(Reader reader)
            throws IOException {
        char[] pwd = new char[1024];
        int noOfChars = reader.read(pwd);
        if (noOfChars < 0) {
            noOfChars = 0;
        }
        char[] passwd = new char[noOfChars];
        System.arraycopy(pwd, 0, passwd, 0, noOfChars);
        return passwd;
    }

}
