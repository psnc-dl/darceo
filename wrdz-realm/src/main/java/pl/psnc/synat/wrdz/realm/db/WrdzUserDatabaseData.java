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

import java.util.Properties;

import pl.psnc.synat.wrdz.realm.WrdzUserRealm;

import com.sun.enterprise.security.auth.realm.BadRealmException;

/**
 * Class containing connection and querying parameters for WRDZ user database.
 */
public class WrdzUserDatabaseData implements DatabaseConnectable {

    /**
     * Name of the database user.
     */
    private final String databaseUser;

    /**
     * Password of the database user.
     */
    private final String databasePassword;

    /**
     * JNDI name of the datasource serving the connection.
     */
    private final String datasourceJndi;

    /**
     * Name of the table containing users' data.
     */
    private final String userTable;

    /**
     * Name of the column in the users' table containing username.
     */
    private final String userNameColumn;

    /**
     * Name of the column in the users' table containing password.
     */
    private final String passwordColumn;

    /**
     * Name of the column in the users' table containing the salt for the password.
     */
    private final String saltColumn;

    /**
     * Name of the table containing groups' data.
     */
    private final String groupTable;

    /**
     * Name of the column in the users' table containing certificate.
     */
    private final String certificateColumn;

    /**
     * Name of the column in the groups' table containing group name.
     */
    private final String groupNameColumn;

    /**
     * Name of the column in the groups' table containing user name.
     */
    private final String groupTableUserNameColumn;


    /**
     * Creates new object storing database access and manipulation data initializing it with realm's properties.
     * 
     * @param properties
     *            realm configuration properties.
     */
    public WrdzUserDatabaseData(Properties properties) {
        databaseUser = properties.getProperty(PARAM_DB_USER);
        databasePassword = properties.getProperty(PARAM_DB_PASSWORD);
        datasourceJndi = properties.getProperty(PARAM_DATASOURCE_JNDI);
        userTable = properties.getProperty(PARAM_USER_TABLE);
        userNameColumn = properties.getProperty(PARAM_USER_NAME_COLUMN);
        passwordColumn = properties.getProperty(PARAM_PASSWORD_COLUMN);
        saltColumn = properties.getProperty(PARAM_SALT_COLUMN);
        groupTable = properties.getProperty(PARAM_GROUP_TABLE);
        certificateColumn = properties.getProperty(PARAM_CERTIFICATE_COLUMN);
        groupNameColumn = properties.getProperty(PARAM_GROUP_NAME_COLUMN);
        groupTableUserNameColumn = properties.getProperty(PARAM_GROUP_TABLE_USER_NAME_COLUMN, getUserNameColumn());
    }


    @Override
    public void pushRealmProperties(WrdzUserRealm realm) {
        if (databaseUser != null && databasePassword != null) {
            realm.setProperty(PARAM_DB_USER, databaseUser);
            realm.setProperty(PARAM_DB_PASSWORD, databasePassword);
        }
        realm.setProperty(PARAM_DATASOURCE_JNDI, datasourceJndi);
    }


    @Override
    public String getDatabaseUser() {
        return databaseUser;
    }


    @Override
    public String getDatabasePassword() {
        return databasePassword;
    }


    @Override
    public String getDatasourceJndi() {
        return datasourceJndi;
    }


    @Override
    public String getUserTable() {
        return userTable;
    }


    @Override
    public String getUserNameColumn() {
        return userNameColumn;
    }


    @Override
    public String getPasswordColumn() {
        return passwordColumn;
    }


    @Override
    public String getSaltColumn() {
        return saltColumn;
    }


    @Override
    public String getGroupTable() {
        return groupTable;
    }


    @Override
    public String getCertificateColumn() {
        return certificateColumn;
    }


    @Override
    public String getGroupNameColumn() {
        return groupNameColumn;
    }


    @Override
    public String getGroupTableUserNameColumn() {
        return groupTableUserNameColumn;
    }


    @Override
    public void validate()
            throws BadRealmException {
        if (datasourceJndi == null) {
            throw new BadRealmException(MISSING_PROPERTY + PARAM_DATASOURCE_JNDI);
        }
        if (userTable == null) {
            throw new BadRealmException(MISSING_PROPERTY + PARAM_USER_TABLE);
        }
        if (groupTable == null) {
            throw new BadRealmException(MISSING_PROPERTY + PARAM_GROUP_TABLE);
        }
        if (userNameColumn == null) {
            throw new BadRealmException(MISSING_PROPERTY + PARAM_USER_NAME_COLUMN);
        }
        if (passwordColumn == null) {
            throw new BadRealmException(MISSING_PROPERTY + PARAM_PASSWORD_COLUMN);
        }
        if (certificateColumn == null) {
            throw new BadRealmException(MISSING_PROPERTY + PARAM_CERTIFICATE_COLUMN);
        }
        if (groupNameColumn == null) {
            throw new BadRealmException(MISSING_PROPERTY + PARAM_GROUP_NAME_COLUMN);
        }
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((certificateColumn == null) ? 0 : certificateColumn.hashCode());
        result = prime * result + ((databasePassword == null) ? 0 : databasePassword.hashCode());
        result = prime * result + ((databaseUser == null) ? 0 : databaseUser.hashCode());
        result = prime * result + ((datasourceJndi == null) ? 0 : datasourceJndi.hashCode());
        result = prime * result + ((groupNameColumn == null) ? 0 : groupNameColumn.hashCode());
        result = prime * result + ((groupTable == null) ? 0 : groupTable.hashCode());
        result = prime * result + ((groupTableUserNameColumn == null) ? 0 : groupTableUserNameColumn.hashCode());
        result = prime * result + ((passwordColumn == null) ? 0 : passwordColumn.hashCode());
        result = prime * result + ((saltColumn == null) ? 0 : saltColumn.hashCode());
        result = prime * result + ((userNameColumn == null) ? 0 : userNameColumn.hashCode());
        result = prime * result + ((userTable == null) ? 0 : userTable.hashCode());
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
        if (!(obj instanceof WrdzUserDatabaseData)) {
            return false;
        }
        WrdzUserDatabaseData other = (WrdzUserDatabaseData) obj;
        if (certificateColumn == null) {
            if (other.certificateColumn != null) {
                return false;
            }
        } else if (!certificateColumn.equals(other.certificateColumn)) {
            return false;
        }
        if (databasePassword == null) {
            if (other.databasePassword != null) {
                return false;
            }
        } else if (!databasePassword.equals(other.databasePassword)) {
            return false;
        }
        if (databaseUser == null) {
            if (other.databaseUser != null) {
                return false;
            }
        } else if (!databaseUser.equals(other.databaseUser)) {
            return false;
        }
        if (datasourceJndi == null) {
            if (other.datasourceJndi != null) {
                return false;
            }
        } else if (!datasourceJndi.equals(other.datasourceJndi)) {
            return false;
        }
        if (groupNameColumn == null) {
            if (other.groupNameColumn != null) {
                return false;
            }
        } else if (!groupNameColumn.equals(other.groupNameColumn)) {
            return false;
        }
        if (groupTable == null) {
            if (other.groupTable != null) {
                return false;
            }
        } else if (!groupTable.equals(other.groupTable)) {
            return false;
        }
        if (groupTableUserNameColumn == null) {
            if (other.groupTableUserNameColumn != null) {
                return false;
            }
        } else if (!groupTableUserNameColumn.equals(other.groupTableUserNameColumn)) {
            return false;
        }
        if (passwordColumn == null) {
            if (other.passwordColumn != null) {
                return false;
            }
        } else if (!passwordColumn.equals(other.passwordColumn)) {
            return false;
        }
        if (saltColumn == null) {
            if (other.saltColumn != null) {
                return false;
            }
        } else if (!saltColumn.equals(other.saltColumn)) {
            return false;
        }
        if (userNameColumn == null) {
            if (other.userNameColumn != null) {
                return false;
            }
        } else if (!userNameColumn.equals(other.userNameColumn)) {
            return false;
        }
        if (userTable == null) {
            if (other.userTable != null) {
                return false;
            }
        } else if (!userTable.equals(other.userTable)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WrdzUserDatabaseData [databaseUser=").append(databaseUser).append(", databasePassword=")
                .append(databasePassword).append(", datasourceJndi=").append(datasourceJndi).append(", userTable=")
                .append(userTable).append(", userNameColumn=").append(userNameColumn).append(", passwordColumn=")
                .append(passwordColumn).append(", saltColumn=").append(saltColumn).append(", groupTable=")
                .append(groupTable).append(", certificateColumn=").append(certificateColumn)
                .append(", groupNameColumn=").append(groupNameColumn).append(", groupTableUserNameColumn=")
                .append(groupTableUserNameColumn).append("]");
        return builder.toString();
    }
}
