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
package pl.psnc.synat.wrdz.ru.dao.services.impl;

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.ru.dao.services.RegistryOperationFilterFactory;
import pl.psnc.synat.wrdz.ru.entity.services.RegistryOperation;
import pl.psnc.synat.wrdz.ru.entity.services.RegistryOperation_;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.SemanticDescriptor_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link RegistryOperation}.
 */
public class RegistryOperationFilterFactoryImpl extends GenericQueryFilterFactoryImpl<RegistryOperation> implements
        RegistryOperationFilterFactory {

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
    public RegistryOperationFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<RegistryOperation> criteriaQuery, Root<RegistryOperation> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<RegistryOperation> byLocationUrl(String locationUrl) {
        Join<RegistryOperation, SemanticDescriptor> registries = root.join(RegistryOperation_.target);
        Predicate predicate = criteriaBuilder.like(registries.get(SemanticDescriptor_.locationUrl), locationUrl);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<RegistryOperation> byDateFrom(Date from) {
        Predicate predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(RegistryOperation_.date), from);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<RegistryOperation> bySemanticDescriptorId(long descriptorId) {
        Predicate predicate = criteriaBuilder.equal(root.get(RegistryOperation_.target).get(SemanticDescriptor_.id),
            descriptorId);
        return constructQueryFilter(predicate);
    }

}
