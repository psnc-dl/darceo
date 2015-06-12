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
package pl.psnc.synat.wrdz.mdz.dao.integrity.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericDaoBean;
import pl.psnc.synat.wrdz.mdz.dao.integrity.DigitalObjectDao;
import pl.psnc.synat.wrdz.mdz.entity.integrity.DigitalObject;
import pl.psnc.synat.wrdz.mdz.entity.integrity.DigitalObject_;

/**
 * Default implementation of {@link DigitalObjectDao}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class DigitalObjectDaoBean extends GenericDaoBean<DigitalObject, String> implements DigitalObjectDao {

    /**
     * Creates a new instance of FileFormatDaoBean.
     */
    public DigitalObjectDaoBean() {
        super(DigitalObject.class);
    }


    @Override
    public DigitalObject getLast() {
        CriteriaQuery<DigitalObject> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<DigitalObject> root = criteriaQuery.from(clazz);
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(DigitalObject_.addedOn)));
        criteriaQuery.select(root);
        List<DigitalObject> results = entityManager.createQuery(criteriaQuery).setMaxResults(1).getResultList();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }


    @Override
    public Long countCorrupted() {
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<DigitalObject> root = criteriaQuery.from(clazz);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get(DigitalObject_.correct), false));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }


    @Override
    public void deleteAll() {
        entityManager.createQuery("delete from MdzDigitalObject").executeUpdate();
    }


    @Override
    public Date getFirstAdded() {
        CriteriaQuery<Date> criteriaQuery = criteriaBuilder.createQuery(Date.class);
        Root<DigitalObject> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root.get(DigitalObject_.addedOn));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(DigitalObject_.addedOn)));
        List<Date> results = entityManager.createQuery(criteriaQuery).setMaxResults(1).getResultList();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }


    @Override
    public Date getLastVerified() {
        CriteriaQuery<Date> criteriaQuery = criteriaBuilder.createQuery(Date.class);
        Root<DigitalObject> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root.get(DigitalObject_.verifiedOn));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(DigitalObject_.verifiedOn)));
        List<Date> results = entityManager.createQuery(criteriaQuery).setMaxResults(1).getResultList();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

}
