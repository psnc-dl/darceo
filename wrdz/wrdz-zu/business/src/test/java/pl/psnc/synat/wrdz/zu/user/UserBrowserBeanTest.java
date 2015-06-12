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
package pl.psnc.synat.wrdz.zu.user;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import pl.psnc.synat.wrdz.common.BaseTestSuite;
import pl.psnc.synat.wrdz.zu.ZuBaseTest;
import pl.psnc.synat.wrdz.zu.dto.user.UserDto;

public class UserBrowserBeanTest extends ZuBaseTest {

    /** Bean to test. */
    private UserBrowser userBrowser;


    @Before
    public void setUp()
            throws Exception {
        userBrowser = (UserBrowser) BaseTestSuite.getContext().lookup("java:global/wrdz/classes/UserBrowserBean");
    }


    @Test
    public void shouldReturnUserDtoByUsername() {
        UserDto tested = userBrowser.getUser("test");
        UserDto expected = new UserDto(999, "test", "/home");
        Assert.assertEquals(expected, tested);
    }

}
