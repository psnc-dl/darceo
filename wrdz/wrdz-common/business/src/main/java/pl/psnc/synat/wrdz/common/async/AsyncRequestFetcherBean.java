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

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.config.Configuration;
import pl.psnc.synat.wrdz.common.dao.async.AsyncRequestDao;
import pl.psnc.synat.wrdz.common.dao.async.AsyncRequestFilterFactory;
import pl.psnc.synat.wrdz.common.dao.async.AsyncRequestResultDao;
import pl.psnc.synat.wrdz.common.dao.async.AsyncRequestResultFilterFactory;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequest;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResultConsts;

/**
 * Bean which fetches responses for asynchronous requests submitted to any module of WRDZ application.
 */
@Stateless
public class AsyncRequestFetcherBean implements AsyncRequestFetcher {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(AsyncRequestFetcherBean.class);

    /**
     * Configuration.
     */
    @Inject
    private Configuration config;

    /**
     * DAO for asynchronous requests.
     */
    @EJB
    private AsyncRequestDao asyncRequestDaoBean;

    /**
     * DAO for results of asynchronous requests.
     */
    @EJB
    private AsyncRequestResultDao asyncRequestResultDaoBean;


    @Override
    public AsyncRequest getAsyncRequest(String requestId)
            throws AsyncRequestNotFoundException {
        AsyncRequest request = asyncRequestDaoBean.findById(requestId);
        if (request != null) {
            return request;
        } else {
            logger.debug("Asynchronous request at " + requestId + " does not exist");
            throw new AsyncRequestNotFoundException("Asynchronous request does not exist.");
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public AsyncRequestResult getAsyncRequestOKResultByRequestedUrl(String requestedUrl)
            throws AsyncRequestResultNotFoundException {
        int days = config.getAsyncCleaningPeriod();
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        Date date = now.getTime();
        date.setTime(date.getTime() - (days - 1) * 86400000L);
        AsyncRequestResultFilterFactory queryFilterFactory = asyncRequestResultDaoBean.createQueryModifier()
                .getQueryFilterFactory();
        AsyncRequestResult result = asyncRequestResultDaoBean.findFirstResultBy(queryFilterFactory.and(
            queryFilterFactory.byRequestedUrl(requestedUrl),
            queryFilterFactory.byCode(AsyncRequestResultConsts.HTTP_CODE_OK),
            queryFilterFactory.byCompletedAfter(date, true)));
        if (result != null) {
            return result;
        } else {
            logger.debug("Result of asynchronous request at " + requestedUrl + " and code 200 does not exist");
            throw new AsyncRequestResultNotFoundException("Result of asynchronous request does not exist.");
        }
    }


    @Override
    public String getAsyncRequestInProgressIdByRequestedUrl(String requestedUrl)
            throws AsyncRequestNotFoundException {
        AsyncRequestFilterFactory queryFilterFactory = asyncRequestDaoBean.createQueryModifier()
                .getQueryFilterFactory();
        AsyncRequest request = asyncRequestDaoBean.findFirstResultBy(queryFilterFactory.and(
            queryFilterFactory.byRequestedUrl(requestedUrl), queryFilterFactory.byInProgress(Boolean.TRUE)));
        if (request != null) {
            return request.getId();
        } else {
            logger.debug("Asynchronous request at " + requestedUrl + " and in progress does not exist");
            throw new AsyncRequestNotFoundException("Asynchronous request does not exist.");
        }
    }


    @Override
    public AsyncRequestResult getAsyncRequestResult(String resultId)
            throws AsyncRequestResultNotFoundException {
        AsyncRequestResult result = asyncRequestResultDaoBean.findById(resultId);
        if (result != null) {
            return result;
        } else {
            logger.info("Result of asynchronous request " + resultId + " does not exist");
            throw new AsyncRequestResultNotFoundException("Result of asynchronous request does not exist.");
        }
    }


    @Override
    public AsyncRequestResult getAsyncRequestResultByRequestedUrl(String requestedUrl)
            throws AsyncRequestResultNotFoundException {
        AsyncRequestResultFilterFactory queryFilterFactory = asyncRequestResultDaoBean.createQueryModifier()
                .getQueryFilterFactory();
        AsyncRequestResult result = asyncRequestResultDaoBean.findFirstResultBy(queryFilterFactory
                .byRequestedUrl(requestedUrl));
        if (result != null) {
            return result;
        } else {
            logger.debug("Result of asynchronous request " + requestedUrl + " does not exist");
            throw new AsyncRequestResultNotFoundException("Result of asynchronous request does not exist.");
        }
    }

}
