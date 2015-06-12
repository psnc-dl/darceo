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
package pl.psnc.synat.wrdz.common.async;

import org.junit.Before;
import org.junit.Test;

import pl.psnc.synat.wrdz.common.BaseTestSuite;
import pl.psnc.synat.wrdz.common.CommonBaseTest;

public class AsyncRequestFetcherBeanTest extends CommonBaseTest {

    /** Bean to test. */
    private AsyncRequestFetcher asyncRequestFetcher;


    @Before
    public void setUp()
            throws Exception {
        asyncRequestFetcher = (AsyncRequestFetcher) BaseTestSuite.getContext().lookup(
            "java:global/wrdz/classes/AsyncRequestFetcherBean");
    }


    @Test(expected = AsyncRequestNotFoundException.class)
    public void shouldGetAsyncRequestNotFoundException()
            throws Exception {
        asyncRequestFetcher.getAsyncRequest("null");
    }

}
