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

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.zmd.dao.object.OptimizedObjectDao;
import pl.psnc.synat.wrdz.zmd.dao.object.OptimizedObjectFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.OptimizedObjectSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.OptimizedObject;

/**
 * A class managing the persistence of {@link OptimizedObject} class. It implements additional operations available for
 * {@link OptimizedObject} object (as defined in {@link OptimizedObjectDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class OptimizedObjectDaoBean extends
        ExtendedGenericDaoBean<OptimizedObjectFilterFactory, OptimizedObjectSorterBuilder, OptimizedObject, Long>
        implements OptimizedObjectDao {

    /**
     * Creates new instance of OptimizedObjectDaoBean.
     */
    public OptimizedObjectDaoBean() {
        super(OptimizedObject.class);
    }


    @Override
    protected OptimizedObjectFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<OptimizedObject> criteriaQuery, Root<OptimizedObject> root, Long epoch) {
        return new OptimizedObjectFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected OptimizedObjectSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<OptimizedObject> criteriaQuery, Root<OptimizedObject> root, Long epoch) {
        return new OptimizedObjectSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
