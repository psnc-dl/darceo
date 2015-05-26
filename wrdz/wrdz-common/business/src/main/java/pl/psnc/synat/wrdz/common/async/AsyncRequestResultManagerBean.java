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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.config.Configuration;
import pl.psnc.synat.wrdz.common.dao.async.AsyncRequestDao;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequest;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;

/**
 * Default implementation of {@link AsyncRequestResultManager}.
 */
@Stateless
public class AsyncRequestResultManagerBean implements AsyncRequestResultManager {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(AsyncRequestResultManagerBean.class);

    /** Random number generator. */
    private static final Random RANDOM = new Random();

    /** DAO for asynchronous requests. */
    @EJB
    private AsyncRequestDao asyncRequestDaoBean;

    /** Configuration. */
    @Inject
    private Configuration config;


    @Override
    public AsyncRequestResult prepareResult(String requestId) {
        String resultId = "" + System.currentTimeMillis() + Math.abs(Thread.currentThread().getName().hashCode())
                + Math.abs(RANDOM.nextLong());
        AsyncRequest request = asyncRequestDaoBean.findById(requestId);
        if (request != null) {
            request.setInProgress(Boolean.FALSE);
            AsyncRequestResult result = new AsyncRequestResult(resultId, request);
            request.setResult(result);
            return result;
        } else {
            logger.warn("Request not found: " + requestId);
            return null;
        }
    }


    @Override
    public AsyncRequestResult prepareResult(String requestId, Integer code) {
        AsyncRequestResult result = prepareResult(requestId);
        if (result != null) {
            result.setCode(code);
        }
        return result;
    }


    @Override
    public void saveResultString(String resultId, String result)
            throws IOException {
        String root = config.getAsyncCacheHome();
        OutputStream fstream = new FileOutputStream(new File(root + "/" + resultId));
        fstream.write(result.getBytes("utf-8"));
        fstream.close();
    }


    @Override
    public void saveResultFile(String resultId, String path)
            throws IOException {
        File srcfile = new File(path);
        String root = config.getAsyncCacheHome();
        File dstfile = new File(root + "/" + resultId);
        if (!srcfile.renameTo(dstfile)) {
            throw new IOException("File '" + srcfile + "' could not be renamed as '" + dstfile + "'.");
        }
    }
}
