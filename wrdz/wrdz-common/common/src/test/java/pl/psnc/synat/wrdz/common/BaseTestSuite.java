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

import javax.ejb.embeddable.EJBContainer;
import javax.naming.Context;
import javax.transaction.UserTransaction;

/**
 * Base class for all test suites.
 */
public class BaseTestSuite {

    /**
     * EJB container.
     */
    protected static EJBContainer ejbContainer;

    /**
     * JNDI context.
     */
    protected static Context ctx;

    /**
     * User transaction.
     */
    protected static UserTransaction ut;


    /**
     * Method that should be run before all tests (for the specific module).
     * 
     * @param modules
     *            modules
     * @throws Exception
     *             if something went wrong
     */
    public static void setUpBeforeBaseTest(Module... modules)
            throws Exception {
        ejbContainer = GlassfishTestHelper.createContainer();
        ctx = ejbContainer.getContext();
        ut = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
        GlassfishTestHelper.createDatabase(ejbContainer, modules);
    }


    /**
     * Method that should be run after all tests (for the specific module).
     * 
     * @param module
     *            module
     * @throws Exception
     *             if something went wrong
     */
    public static void tearDownAfterBaseTestClass(Module... modules)
            throws Exception {
        GlassfishTestHelper.dropDatabase(ejbContainer, modules);
        ctx.close();
        ejbContainer.close();
    }


    public static Context getContext() {
        return ctx;
    }


    public static UserTransaction getTransaction() {
        return ut;
    }

}
