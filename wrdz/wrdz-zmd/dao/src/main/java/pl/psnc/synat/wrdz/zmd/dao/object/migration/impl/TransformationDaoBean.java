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
package pl.psnc.synat.wrdz.zmd.dao.object.migration.impl;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.TransformationDao;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.TransformationFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.TransformationSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Transformation;

/**
 * A class managing the persistence of {@link Transformation} class. It implements additional operations available for
 * {@link Transformation} object (as defined in {@link TransformationDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class TransformationDaoBean extends
        ExtendedGenericDaoBean<TransformationFilterFactory, TransformationSorterBuilder, Transformation, Long>
        implements TransformationDao {

    /**
     * Creates new instance of RemoteRepositoryAuthenticationDaoBean.
     */
    public TransformationDaoBean() {
        super(Transformation.class);
    }


    @Override
    protected TransformationFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Transformation> criteriaQuery, Root<Transformation> root, Long epoch) {
        return new TransformationFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected TransformationSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<Transformation> criteriaQuery, Root<Transformation> root, Long epoch) {
        return new TransformationSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
