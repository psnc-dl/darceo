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

import pl.psnc.synat.wrdz.realm.WrdzUserRealm;

import com.sun.enterprise.security.auth.realm.BadRealmException;

/**
 * Describes the contract of the class containing connection and querying parameters for realm's user database.
 */
public interface DatabaseConnectable {

    /**
     * JNDI name of datasource instance parameter key.
     */
    String PARAM_DATASOURCE_JNDI = "datasource-jndi";

    /**
     * Database username parameter key.
     */
    String PARAM_DB_USER = "db-user";

    /**
     * Database user's password parameter key.
     */
    String PARAM_DB_PASSWORD = "db-password";

    /**
     * User table name parameter key.
     */
    String PARAM_USER_TABLE = "user-table";

    /**
     * User column name parameter key.
     */
    String PARAM_USER_NAME_COLUMN = "user-name-column";

    /**
     * Password column name parameter key.
     */
    String PARAM_PASSWORD_COLUMN = "password-column";

    /**
     * Password column name parameter key.
     */
    String PARAM_SALT_COLUMN = "salt-column";

    /**
     * Certificate column name parameter key.
     */
    String PARAM_CERTIFICATE_COLUMN = "certificate-column";

    /**
     * Group table name parameter key.
     */
    String PARAM_GROUP_TABLE = "group-table";

    /**
     * Groupname column name parameter key.
     */
    String PARAM_GROUP_NAME_COLUMN = "group-name-column";

    /**
     * Group table username column name parameter key.
     */
    String PARAM_GROUP_TABLE_USER_NAME_COLUMN = "group-table-user-name-column";

    /**
     * Digest password encoding algorithm name parameter key.
     */
    String PARAM_DIGEST_PASSWORD_ENC_ALGORITHM = "digestrealm-password-enc-algorithm";

    /**
     * Error message for missing property in the realm setup.
     */
    String MISSING_PROPERTY = "Realm setup lacks required property named ";


    /**
     * Gets the configured value of the database username parameter.
     * 
     * @return name of the database user.
     */
    String getDatabaseUser();


    /**
     * Gets the configured value of the database user password parameter.
     * 
     * @return password of the database user.
     */
    String getDatabasePassword();


    /**
     * Gets the configured value of the datasource JNDI name parameter.
     * 
     * @return datasource JNDI name.
     */
    String getDatasourceJndi();


    /**
     * Gets the configured value of the users' table name parameter.
     * 
     * @return user's table name.
     */
    String getUserTable();


    /**
     * Gets the configured value of the name of the username column parameter.
     * 
     * @return name of the column storing username in users' table.
     */
    String getUserNameColumn();


    /**
     * Gets the configured value of the name of the password column parameter.
     * 
     * @return name of the column storing password in users' table.
     */
    String getPasswordColumn();


    /**
     * Gets the configured value of the name of the salt column parameter.
     * 
     * @return name of the column storing salt in users' table.
     */
    String getSaltColumn();


    /**
     * Gets the configured value of the groups' table name parameter.
     * 
     * @return groups' table name.
     */
    String getGroupTable();


    /**
     * Gets the configured value of the name of the certificate column parameter.
     * 
     * @return name of the column storing certificate in users' table.
     */
    String getCertificateColumn();


    /**
     * Gets the configured value of the name of the group name column parameter.
     * 
     * @return name of the column storing group name in groups' table.
     */
    String getGroupNameColumn();


    /**
     * Gets the configured value of the name of the username column parameter.
     * 
     * @return name of the column storing username in groups' table.
     */
    String getGroupTableUserNameColumn();


    /**
     * Validates database connection parameters.
     * 
     * @throws BadRealmException
     *             should validation fail.
     */
    void validate()
            throws BadRealmException;


    /**
     * Pushes database-related properties into the specified realm configuration.
     * 
     * @param realm
     *            realm which properties will be set.
     */
    void pushRealmProperties(WrdzUserRealm realm);

}
