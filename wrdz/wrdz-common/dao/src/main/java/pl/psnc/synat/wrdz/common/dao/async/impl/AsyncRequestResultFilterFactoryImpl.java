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
package pl.psnc.synat.wrdz.common.dao.async.impl;

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.dao.async.AsyncRequestResultFilterFactory;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequest;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult_;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequest_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link AsyncRequestResult}.
 */
public class AsyncRequestResultFilterFactoryImpl extends GenericQueryFilterFactoryImpl<AsyncRequestResult> implements
        AsyncRequestResultFilterFactory {

    /**
     * Constructs this factory initializing it with required arguments.
     * 
     * @param criteriaBuilder
     *            reference to {@link CriteriaBuilder} object.
     * @param criteriaQuery
     *            query upon which filters will be build
     * @param root
     *            object representing root type of the entity this filter factory manages
     * @param epoch
     *            builder's epoch
     * 
     * @throws IllegalArgumentException
     *             thrown if one of arguments is <code>null</code>
     * 
     */
    public AsyncRequestResultFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<AsyncRequestResult> criteriaQuery, Root<AsyncRequestResult> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<AsyncRequestResult> byRequestId(String requestId) {
        Join<AsyncRequestResult, AsyncRequest> request = root.join(AsyncRequestResult_.request);
        Predicate predicate = criteriaBuilder.equal(request.get(AsyncRequest_.id), requestId);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<AsyncRequestResult> byCompletedBefore(Date date, boolean inclusive) {
        Predicate predicate = null;
        if (inclusive) {
            predicate = criteriaBuilder.lessThanOrEqualTo(root.get(AsyncRequestResult_.completedOn), date);
        } else {
            predicate = criteriaBuilder.lessThan(root.get(AsyncRequestResult_.completedOn), date);
        }
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<AsyncRequestResult> byCompletedAfter(Date date, boolean inclusive) {
        Predicate predicate = null;
        if (inclusive) {
            predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(AsyncRequestResult_.completedOn), date);
        } else {
            predicate = criteriaBuilder.greaterThan(root.get(AsyncRequestResult_.completedOn), date);
        }
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<AsyncRequestResult> byRequestedUrl(String requestedUrl) {
        Join<AsyncRequestResult, AsyncRequest> request = root.join(AsyncRequestResult_.request);
        Predicate predicate = criteriaBuilder.equal(request.get(AsyncRequest_.requestedUrl), requestedUrl);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<AsyncRequestResult> byCode(Integer code) {
        Predicate predicate = criteriaBuilder.equal(root.get(AsyncRequestResult_.code), code);
        return constructQueryFilter(predicate);
    }

}
