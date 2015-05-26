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
package pl.psnc.synat.wrdz.zmd.dao.object.impl;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.dao.object.OptimizedObjectFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.OptimizedObject;
import pl.psnc.synat.wrdz.zmd.entity.object.OptimizedObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Optimization;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Optimization_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link OptimizedObject}.
 */
public class OptimizedObjectFilterFactoryImpl extends GenericQueryFilterFactoryImpl<OptimizedObject> implements
        OptimizedObjectFilterFactory {

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
    public OptimizedObjectFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<OptimizedObject> criteriaQuery, Root<OptimizedObject> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<OptimizedObject> byOptimizedFrom(Long id) {
        Join<OptimizedObject, Optimization> optimization = root.join(OptimizedObject_.optimizedFrom);
        Predicate predicate = criteriaBuilder.equal(optimization.get(Optimization_.source).get(DigitalObject_.id), id);
        return constructQueryFilter(predicate);
    }
}
