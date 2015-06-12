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
package pl.psnc.synat.wrdz.zmd.object.async;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Queue;

import pl.psnc.synat.wrdz.common.async.AsyncRequestProcessor;
import pl.psnc.synat.wrdz.common.async.AsyncRequestProcessorBean;

/**
 * Subclass of the template class <code>AsyncRequestProcessorBean</code>. This class provides the JMS queue for
 * asynchronous request in the objects management functionality.
 */
@Stateless
public class ObjectAsyncRequestProcessorBean extends AsyncRequestProcessorBean<ObjectAsyncRequestEnum> implements
        AsyncRequestProcessor<ObjectAsyncRequestEnum> {

    /**
     * JMS queue for asynchronous request in the objects management functionality.
     */
    @Resource(mappedName = "queue/arp/zmd-object")
    private Queue queue;


    @Override
    protected Queue getQueue() {
        return queue;
    }

}
