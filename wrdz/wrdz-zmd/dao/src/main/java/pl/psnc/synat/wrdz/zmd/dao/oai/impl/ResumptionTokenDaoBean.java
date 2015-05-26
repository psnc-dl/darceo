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
package pl.psnc.synat.wrdz.zmd.dao.oai.impl;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.persistence.TemporalType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.zmd.dao.oai.ResumptionTokenDao;
import pl.psnc.synat.wrdz.zmd.dao.oai.ResumptionTokenFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.oai.ResumptionTokenSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.oai.ResumptionToken;

/**
 * A class managing the persistence of {@link ResumptionToken} class. It implements additional operations available for
 * {@link ResumptionToken} object (as defined in {@link ResumptionTokenDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ResumptionTokenDaoBean extends
        ExtendedGenericDaoBean<ResumptionTokenFilterFactory, ResumptionTokenSorterBuilder, ResumptionToken, String>
        implements ResumptionTokenDao {

    /**
     * Creates new instance of ResumptionTokenDaoBean.
     */
    public ResumptionTokenDaoBean() {
        super(ResumptionToken.class);
    }


    @Override
    protected ResumptionTokenFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ResumptionToken> criteriaQuery, Root<ResumptionToken> root, Long epoch) {
        return new ResumptionTokenFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected ResumptionTokenSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ResumptionToken> criteriaQuery, Root<ResumptionToken> root, Long epoch) {
        return new ResumptionTokenSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public int deleteStaleTokens(Date date) {
        Query query = entityManager.createQuery("DELETE FROM ResumptionToken rt WHERE rt.expirationDate < :date");
        query.setParameter("date", date, TemporalType.TIMESTAMP);
        return query.executeUpdate();
    }

}
