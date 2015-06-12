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
package pl.psnc.synat.wrdz.common;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.embeddable.EJBContainer;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main test class for the Common module.
 */
public final class GlassfishTestHelper {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(GlassfishTestHelper.class);

    private static final String APPLICATION_NAME = "wrdz";


    /**
     * Private constructor.
     */
    private GlassfishTestHelper() {
    }


    /**
     * Creates a new EJB container.
     * 
     * @return EJB container
     * @throws Exception
     *             if something went wrong
     */
    public static EJBContainer createContainer()
            throws Exception {
        Map<String, String> props = new HashMap<String, String>();
        props.put("org.glassfish.ejb.embedded.glassfish.installation.root",
            "../../wrdz-common/business/src/test/glassfish");
        props.put("org.glassfish.ejb.embedded.glassfish.instance.root",
            "../../wrdz-common/business/src/test/glassfish/domains/wrdz-domain");
        props.put(EJBContainer.APP_NAME, APPLICATION_NAME);
        return EJBContainer.createEJBContainer(props);
    }


    /**
     * Creates and initializes database for specific module.
     * 
     * @param ejbContainer
     *            ejb container
     * @param module
     *            module
     * @throws Exception
     *             when some error occurs
     */
    public static void createDatabase(EJBContainer ejbContainer, Module... modules)
            throws Exception {
        DataSource dataSource = (DataSource) ejbContainer.getContext().lookup("jdbc/__TimerPool");
        for (Module module : modules) {
            String moduleName = module.name().toLowerCase();
            executeSqlFromFile(dataSource, "../../wrdz-" + moduleName + "/entity/src/main/config/" + moduleName
                    + "-SQL-CREATE-ALL.sql");
            executeSqlFromFile(dataSource, "../../wrdz-" + moduleName + "/entity/src/test/config/" + moduleName
                    + "-SQL-update-test.sql");
        }
    }


    /**
     * Drops all structures from database for specific module.
     * 
     * @param ejbContainer
     *            ejb container
     * @param module
     *            module
     * @throws Exception
     *             when some error occurs
     */
    public static void dropDatabase(EJBContainer ejbContainer, Module... modules)
            throws Exception {
        DataSource dataSource = (DataSource) ejbContainer.getContext().lookup("jdbc/__TimerPool");
        for (Module module : modules) {
            String moduleName = module.name().toLowerCase();
            executeSqlFromFile(dataSource, "../../wrdz-" + moduleName + "/entity/src/main/config/" + moduleName
                    + "-SQL-DROP-ALL.sql");
        }
    }


    /**
     * Execute the SQL command.
     * 
     * @param dataSource
     *            data source
     * @param sql
     *            SQL command
     * @throws Exception
     *             when execution fails
     */
    protected static void executeSql(DataSource dataSource, String sql)
            throws Exception {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.executeUpdate(sql);
        statement.close();
        connection.close();
    }


    /**
     * Execute all SQL commands from the file.
     * 
     * @param dataSource
     *            data source
     * @param sqlFilename
     *            file with SQL commands
     * @throws Exception
     *             when execution fails
     */
    private static void executeSqlFromFile(DataSource dataSource, String sqlFilename)
            throws Exception {
        try {
            Connection connection = dataSource.getConnection();
            String s;
            StringBuffer sb = new StringBuffer();
            BufferedReader in = new BufferedReader(new FileReader(sqlFilename));
            while ((s = in.readLine()) != null) {
                s = s.trim();
                if (s.length() > 0 && !s.startsWith("--")) {
                    if (s.endsWith(";")) {
                        sb.append(s.substring(0, s.length() - 1));
                        Statement statement = connection.createStatement();
                        // adjusting to embedded derby 
                        String sql = adjustSqlToDerby(sb.toString());
                        logger.debug(sql);
                        statement.executeUpdate(sql);
                        statement.close();
                        sb.delete(0, sb.length());
                    } else {
                        sb.append(s + "\n");
                    }
                }
            }
            in.close();
            connection.close();
        } catch (FileNotFoundException e) {
            logger.error("Error while reading the SQL file: " + sqlFilename, e);
            throw e;
        } catch (IOException e) {
            logger.error("Error while reading the SQL file: " + sqlFilename, e);
            throw e;
        } catch (SQLException e) {
            if (e.getMessage().endsWith("already exists")) {
                logger.debug("It seems that database is not empty. Clean it if tests fail!");
            } else {
                logger.error("Error while executing SQL: " + sqlFilename, e);
                throw e;
            }
        }
    }


    /**
     * Adjust SQL to Derby dialect.
     * 
     * @param sql
     *            SQL
     * @return SQL adjusted to Derby dialect
     */
    private static String adjustSqlToDerby(String sql) {
        return sql.replace("BYTEA", "BLOB").replace("bytea", "BLOB").replace("TEXT", "CLOB").replace("text", "CLOB")
                .replaceAll("DECIMAL\\([0-9]+\\)", "DECIMAL")
                .replaceAll("nextval\\('darceo\\.([a-z_]+)'\\)", "NEXT VALUE FOR darceo\\.$1")
                .replace(" WITHOUT TIME ZONE", "").replace("TABLESPACE metadata_store", "");
    }
}
