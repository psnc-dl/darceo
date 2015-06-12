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
package pl.psnc.synat.wrdz.ru.dao.registries.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.ru.dao.registries.RemoteRegistryFilterFactory;
import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry;
import pl.psnc.synat.wrdz.ru.entity.registries.RemoteRegistry_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link RemoteRegistry}.
 */
public class RemoteRegistryFilterFactoryImpl extends GenericQueryFilterFactoryImpl<RemoteRegistry> implements
        RemoteRegistryFilterFactory {

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
    public RemoteRegistryFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<RemoteRegistry> criteriaQuery, Root<RemoteRegistry> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<RemoteRegistry> byId(long id) {
        Predicate predicate = criteriaBuilder.equal(root.get(RemoteRegistry_.id), id);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<RemoteRegistry> byLocationUrl(String location) {
        Predicate predicate = criteriaBuilder.like(root.get(RemoteRegistry_.locationUrl), location);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<RemoteRegistry> byReadEnabled(boolean readEnabled) {
        Predicate predicate;
        if (readEnabled) {
            predicate = criteriaBuilder.isTrue(root.get(RemoteRegistry_.readEnabled));
        } else {
            predicate = criteriaBuilder.isFalse(root.get(RemoteRegistry_.readEnabled));
        }
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<RemoteRegistry> byHarvested(boolean harvested) {
        Predicate predicate;
        if (harvested) {
            predicate = criteriaBuilder.isTrue(root.get(RemoteRegistry_.harvested));
        } else {
            predicate = criteriaBuilder.isFalse(root.get(RemoteRegistry_.harvested));
        }
        return constructQueryFilter(predicate);
    }

}
