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
package pl.psnc.synat.wrdz.zmd;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.BaseTestSuite;
import pl.psnc.synat.wrdz.common.Module;
import pl.psnc.synat.wrdz.zmd.download.ConnectionHelperBeanTest;
import pl.psnc.synat.wrdz.zmd.object.IdentifierBrowserBeanTest;
import pl.psnc.synat.wrdz.zmd.object.ObjectManagerBeanTest;

/**
 * Test suite for the ZMD module.
 */
@RunWith(Suite.class)
@SuiteClasses({ ConnectionHelperBeanTest.class, ObjectManagerBeanTest.class, IdentifierBrowserBeanTest.class })
public class ZmdTestSuite extends BaseTestSuite {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ZmdTestSuite.class);


    /**
     * Method runs before all tests.
     * 
     * @throws Exception
     *             if something went wrong
     */
    @BeforeClass
    public static void setUpBeforeZmdBaseTestClass()
            throws Exception {
        setUpBeforeBaseTest(Module.COMMON, Module.ZU, Module.ZMD);
    }


    /**
     * Method runs after all tests.
     * 
     * @throws Exception
     *             if something went wrong
     */
    @AfterClass
    public static void tearDownAfterZmdBaseTestClass()
            throws Exception {
        //     cleanZmdCache();
        //     cleanStorage();
        tearDownAfterBaseTestClass(Module.ZMD, Module.ZU, Module.COMMON);
    }


    /**
     * Cleans ZMD cache directory.
     * 
     * @throws Exception
     *             if something went wrong
     */
    private static void cleanZmdCache()
            throws Exception {
        String zmdCacheHome = new XMLConfiguration("zmd-wrdz-config.xml").getString("cache.home");
        String[] content = new File(zmdCacheHome).list();
        for (int i = 0; i < content.length; i++) {
            FileUtils.deleteQuietly(new File(zmdCacheHome + "/" + content[i]));
        }
    }


    /**
     * Cleans ZMD storage.
     * 
     * @throws Exception
     *             if something went wrong
     */
    private static void cleanStorage()
            throws Exception {
        Configuration config = new XMLConfiguration("test-config.xml");
        String storageType = config.getString("storage.type");
        if (storageType.equals("FS")) {
            String storageRoot = config.getString("storage.root");
            String[] content = new File(storageRoot).list();
            for (int i = 0; i < content.length; i++) {
                FileUtils.deleteQuietly(new File(storageRoot + "/" + content[i]));
            }
        } else {
            logger.error("Cleaner for SFTP storage not yet implemented!");
        }
    }

}
