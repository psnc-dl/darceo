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

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.common.dao.async.AsyncRequestResultDao;
import pl.psnc.synat.wrdz.common.dao.async.AsyncRequestResultFilterFactory;
import pl.psnc.synat.wrdz.common.dao.async.AsyncRequestResultSorterBuilder;
import pl.psnc.synat.wrdz.common.entity.async.AsyncRequestResult;

/**
 * A class managing the persistence of {@link AsyncRequestResult} class. It implements additional operations available
 * for {@link AsyncRequestResult} object (as defined in {@link AsyncRequestResultDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class AsyncRequestResultDaoBean
        extends
        ExtendedGenericDaoBean<AsyncRequestResultFilterFactory, AsyncRequestResultSorterBuilder, AsyncRequestResult, String>
        implements AsyncRequestResultDao {

    /**
     * Creates new instance of AsyncRequestResultDaoBean.
     */
    public AsyncRequestResultDaoBean() {
        super(AsyncRequestResult.class);
    }


    @Override
    protected AsyncRequestResultFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<AsyncRequestResult> criteriaQuery, Root<AsyncRequestResult> root, Long epoch) {
        return new AsyncRequestResultFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected AsyncRequestResultSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<AsyncRequestResult> criteriaQuery, Root<AsyncRequestResult> root, Long epoch) {
        return new AsyncRequestResultSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
