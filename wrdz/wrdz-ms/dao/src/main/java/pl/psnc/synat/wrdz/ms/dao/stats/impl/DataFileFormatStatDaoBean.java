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
import pl.psnc.synat.wrdz.ms.dao.stats.DataFileFormatStatDao;
import pl.psnc.synat.wrdz.ms.entity.stats.DataFileFormatStat;
import pl.psnc.synat.wrdz.ms.entity.stats.DataFileFormatStat_;

/**
 * Default implementation of {@link DataFileFormatStatDao}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class DataFileFormatStatDaoBean extends GenericDaoBean<DataFileFormatStat, Long> implements
        DataFileFormatStatDao {

    /**
     * Creates a new instance of DataFileFormatStatDaoBean.
     */
    public DataFileFormatStatDaoBean() {
        super(DataFileFormatStat.class);
    }


    @Override
    public List<DataFileFormatStat> findAll(String username) {
        CriteriaQuery<DataFileFormatStat> query = criteriaBuilder.createQuery(clazz);
        Root<DataFileFormatStat> root = query.from(clazz);

        if (username != null) {
            query.where(criteriaBuilder.equal(root.get(DataFileFormatStat_.username), username));
        } else {
            query.where(criteriaBuilder.isNull(root.get(DataFileFormatStat_.username)));
        }

        return entityManager.createQuery(query).getResultList();
    }


    @Override
    public void deleteAll() {
        entityManager.createQuery("delete from DataFileFormatStat").executeUpdate();
        entityManager.flush();
    }
}
