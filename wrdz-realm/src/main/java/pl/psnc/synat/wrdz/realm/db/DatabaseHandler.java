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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;

import com.sun.appserv.connectors.internal.api.ConnectorRuntime;
import com.sun.enterprise.security.auth.realm.BadRealmException;
import com.sun.enterprise.security.common.Util;
import com.sun.logging.LogDomains;

/**
 * Abstract class for database handlers.
 */
public abstract class DatabaseHandler {

    /**
     * System Logger.
     */
    private static final Logger logger = LogDomains.getLogger(DatabaseHandler.class, LogDomains.SECURITY_LOGGER);

    /**
     * Object storing connection data.
     */
    protected final DatabaseConnectable connectionData;

    /**
     * Container's connector runtime.
     */
    protected final ConnectorRuntime connectorRuntime;


    /**
     * Creates new database handler and initializes it with realm properties.
     * 
     * @param properties
     *            realm properties.
     * @throws BadRealmException
     *             should connection data be incomplete.
     */
    public DatabaseHandler(Properties properties)
            throws BadRealmException {
        connectionData = new WrdzUserDatabaseData(properties);
        connectionData.validate();
        connectorRuntime = Util.getDefaultHabitat().getByContract(ConnectorRuntime.class);
    }


    /**
     * Establishes connection to the database through the specified datasource.
     * 
     * @return established connection to the database.
     * @throws LoginException
     *             should any problems with datasource finding or configuration occur.
     */
    protected Connection getConnection()
            throws LoginException {
        try {
            final DataSource dataSource = (DataSource) connectorRuntime.lookupNonTxResource(
                connectionData.getDatasourceJndi(), false);
            Connection connection = null;
            if (connectionData.getDatabaseUser() != null && connectionData.getDatabasePassword() != null) {
                connection = dataSource.getConnection(connectionData.getDatabaseUser(),
                    connectionData.getDatabasePassword());
            } else {
                connection = dataSource.getConnection();
            }
            return connection;
        } catch (NamingException namingException) {
            String message = "Cannot find specified JNDI resource " + connectionData.getDatasourceJndi();
            logger.log(Level.SEVERE, message);
            throw (LoginException) new LoginException(message).initCause(namingException);
        } catch (SQLException sqlException) {
            String message = "Cannot connect through JNDI resource " + connectionData.getDatasourceJndi()
                    + " using username " + connectionData.getDatabaseUser();
            logger.log(Level.SEVERE, message);
            throw (LoginException) new LoginException(message).initCause(sqlException);
        }
    }


    /**
     * Closes the SQL objects.
     * 
     * @param connection
     *            SQL connection to close.
     * @param statement
     *            SQL prepared statement to close.
     * @param resultSet
     *            SQL query result set to close.
     */
    protected void close(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException exception) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Cannot close result set.", exception);
                }
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException exception) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Cannot close statement.", exception);
                }
            }
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Cannot close connection.", ex);
                }
            }
        }
    }

}
