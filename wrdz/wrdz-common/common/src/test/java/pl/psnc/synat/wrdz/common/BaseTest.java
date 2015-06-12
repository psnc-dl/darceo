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

import javax.transaction.Status;

import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all tests.
 */
@Ignore
public abstract class BaseTest {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(BaseTest.class);


    /**
     * Method that should be run before every test.
     * 
     * @throws Exception
     *             if something went wrong
     */
    public void setUpBaseTest()
            throws Exception {
        BaseTestSuite.getTransaction().begin();
    }


    /**
     * Method that should be run after every test.
     * 
     * @throws Exception
     *             if something went wrong
     */
    public void tearDownBaseTest()
            throws Exception {
        try {
            logger.debug("Transaction STATUS " + BaseTestSuite.getTransaction().getStatus());
            if (BaseTestSuite.getTransaction().getStatus() != Status.STATUS_MARKED_ROLLBACK) {
                BaseTestSuite.getTransaction().commit();
            } else {
                BaseTestSuite.getTransaction().rollback();
            }
        } catch (Exception e) {
            logger.error("Error while cimmitting or rolling back a transaction", e);
            throw e;
        }
    }

}
