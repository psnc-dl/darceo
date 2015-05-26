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
package pl.psnc.synat.wrdz.zu;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import pl.psnc.synat.wrdz.common.BaseTestSuite;
import pl.psnc.synat.wrdz.common.Module;
import pl.psnc.synat.wrdz.zu.user.UserBrowserBeanTest;

/**
 * Test suite for the ZU module.
 */
@RunWith(Suite.class)
@SuiteClasses({ UserBrowserBeanTest.class })
public class ZuTestSuite extends BaseTestSuite {

    /**
     * Method runs before all tests.
     * 
     * @throws Exception
     *             if something went wrong
     */
    @BeforeClass
    public static void setUpBeforeRuBaseTestClass()
            throws Exception {
        setUpBeforeBaseTest(Module.COMMON, Module.ZU);
    }


    /**
     * Method runs after all tests.
     * 
     * @throws Exception
     *             if something went wrong
     */
    @AfterClass
    public static void tearDownAfterRuBaseTestClass()
            throws Exception {
        tearDownAfterBaseTestClass(Module.ZU, Module.COMMON);
    }

}
