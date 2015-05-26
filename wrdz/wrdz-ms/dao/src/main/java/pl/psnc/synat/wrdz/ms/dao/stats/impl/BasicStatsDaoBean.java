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
package pl.psnc.synat.wrdz.ms.dao.stats.impl;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericDaoBean;
import pl.psnc.synat.wrdz.ms.dao.stats.BasicStatsDao;
import pl.psnc.synat.wrdz.ms.entity.stats.BasicStats;
import pl.psnc.synat.wrdz.ms.entity.stats.BasicStats_;

/**
 * Default implementation of {@link BasicStatsDao}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class BasicStatsDaoBean extends GenericDaoBean<BasicStats, Long> implements BasicStatsDao {

    /**
     * Creates a new instance of BasicStatsDaoBean.
     */
    public BasicStatsDaoBean() {
        super(BasicStats.class);
    }


    @Override
    public BasicStats find(String username) {

        CriteriaQuery<BasicStats> query = criteriaBuilder.createQuery(clazz);
        Root<BasicStats> root = query.from(clazz);

        if (username != null) {
            query.where(criteriaBuilder.equal(root.get(BasicStats_.username), username));
        } else {
            query.where(criteriaBuilder.isNull(root.get(BasicStats_.username)));
        }

        List<BasicStats> results = entityManager.createQuery(query).setMaxResults(1).getResultList();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }


    @Override
    public void deleteAll() {
        entityManager.createQuery("delete from BasicStats").executeUpdate();
        entityManager.flush();
    }
}
