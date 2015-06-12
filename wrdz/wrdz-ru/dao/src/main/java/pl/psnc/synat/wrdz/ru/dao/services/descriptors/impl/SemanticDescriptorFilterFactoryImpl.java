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
package pl.psnc.synat.wrdz.ru.dao.services.descriptors.impl;

import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.ru.dao.services.descriptors.SemanticDescriptorFilterFactory;
import pl.psnc.synat.wrdz.ru.entity.services.DataManipulationService;
import pl.psnc.synat.wrdz.ru.entity.services.DataManipulationService_;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptorScheme;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptorScheme_;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link SemanticDescriptor}.
 */
public class SemanticDescriptorFilterFactoryImpl extends GenericQueryFilterFactoryImpl<SemanticDescriptor> implements
        SemanticDescriptorFilterFactory {

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
    public SemanticDescriptorFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<SemanticDescriptor> criteriaQuery, Root<SemanticDescriptor> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<SemanticDescriptor> bySchemaName(String name) {
        Join<SemanticDescriptor, SemanticDescriptorScheme> schemes = root.join(SemanticDescriptor_.type);
        Predicate predicate = criteriaBuilder.equal(schemes.get(SemanticDescriptorScheme_.name), name);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<SemanticDescriptor> byLocationUrl(String locationUrl) {
        Predicate predicate = criteriaBuilder.like(root.get(SemanticDescriptor_.locationUrl), locationUrl);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<SemanticDescriptor> byService(long serviceId) {
        ListJoin<SemanticDescriptor, DataManipulationService> services = root
                .join(SemanticDescriptor_.describedServices);
        Predicate predicate = criteriaBuilder.equal(services.get(DataManipulationService_.id), serviceId);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<SemanticDescriptor> byVisibility(boolean exposed) {
        Predicate predicate;
        if (exposed) {
            predicate = criteriaBuilder.isTrue(root.get(SemanticDescriptor_.exposed));
        } else {
            predicate = criteriaBuilder.isFalse(root.get(SemanticDescriptor_.exposed));
        }
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<SemanticDescriptor> byDeleted(boolean deleted) {
        Predicate predicate;
        if (deleted) {
            predicate = criteriaBuilder.isTrue(root.get(SemanticDescriptor_.deleted));
        } else {
            predicate = criteriaBuilder.isFalse(root.get(SemanticDescriptor_.deleted));
        }
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<SemanticDescriptor> byLocation(boolean local) {
        Predicate predicate;
        if (local) {
            predicate = criteriaBuilder.isNull(root.get(SemanticDescriptor_.origin));
        } else {
            predicate = criteriaBuilder.isNotNull(root.get(SemanticDescriptor_.origin));
        }
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<SemanticDescriptor> byIdIn(Collection<Long> ids) {
        Predicate predicate = root.get(SemanticDescriptor_.id).in(ids);
        return constructQueryFilter(predicate);
    }

}
