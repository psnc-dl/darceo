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
package pl.psnc.synat.wrdz.mdz;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.BaseTest;

/**
 * Base class for all tests in the MDZ module.
 */
@Ignore
public class MdzBaseTest extends BaseTest {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MdzBaseTest.class);


    /**
     * Setup before test case method.
     * 
     * @throws Exception
     *             should setup before test case fail.
     */
    @Before
    public void setUpRuBaseTest()
            throws Exception {
        logger.debug(this.getClass() + " MDZ test set up");
        setUpBaseTest();
    }


    /**
     * Tear down after test case method.
     * 
     * @throws Exception
     *             should tear down after test case fail.
     */
    @After
    public void tearDownRuBaseTest()
            throws Exception {
        logger.debug(this.getClass() + " MDZ test tear down");
        tearDownBaseTest();
    }

}
