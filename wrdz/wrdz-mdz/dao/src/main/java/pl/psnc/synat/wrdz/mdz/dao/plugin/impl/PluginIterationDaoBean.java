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
package pl.psnc.synat.wrdz.mdz.dao.plugin.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericDaoBean;
import pl.psnc.synat.wrdz.mdz.dao.plugin.PluginIterationDao;
import pl.psnc.synat.wrdz.mdz.entity.plugin.PluginIteration;
import pl.psnc.synat.wrdz.mdz.entity.plugin.PluginIteration_;

/**
 * Default implementation of {@link PluginIterationDao}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class PluginIterationDaoBean extends GenericDaoBean<PluginIteration, String> implements PluginIterationDao {

    /**
     * Creates a new instance of FileFormatDaoBean.
     */
    public PluginIterationDaoBean() {
        super(PluginIteration.class);
    }


    @Override
    public PluginIteration getLast(String pluginName) {
        CriteriaQuery<PluginIteration> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<PluginIteration> root = criteriaQuery.from(clazz);
        criteriaQuery.where(criteriaBuilder.equal(root.get(PluginIteration_.pluginName), pluginName));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(PluginIteration_.finishedOn)),
            criteriaBuilder.desc(root.get(PluginIteration_.id)));
        criteriaQuery.select(root);
        List<PluginIteration> results = entityManager.createQuery(criteriaQuery).setMaxResults(1).getResultList();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }


    @Override
    public long countAll(String pluginName) {
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<PluginIteration> root = criteriaQuery.from(clazz);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get(PluginIteration_.pluginName), pluginName));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }


    @Override
    public void deleteAll(String pluginName) {
        Query query = entityManager.createQuery("delete from PluginIteration p where p.pluginName = :pluginName");
        query.setParameter("pluginName", pluginName);
        query.executeUpdate();
    }


    @Override
    public Date getFirstStarted(String pluginName) {
        CriteriaQuery<Date> criteriaQuery = criteriaBuilder.createQuery(Date.class);
        Root<PluginIteration> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root.get(PluginIteration_.startedOn));
        criteriaQuery.where(criteriaBuilder.equal(root.get(PluginIteration_.pluginName), pluginName));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(PluginIteration_.startedOn)));
        List<Date> results = entityManager.createQuery(criteriaQuery).setMaxResults(1).getResultList();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }


    @Override
    public Date getLastFinished(String pluginName) {
        CriteriaQuery<Date> criteriaQuery = criteriaBuilder.createQuery(Date.class);
        Root<PluginIteration> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root.get(PluginIteration_.finishedOn));
        criteriaQuery.where(criteriaBuilder.equal(root.get(PluginIteration_.pluginName), pluginName));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get(PluginIteration_.finishedOn)));
        List<Date> results = entityManager.createQuery(criteriaQuery).setMaxResults(1).getResultList();
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }
}
