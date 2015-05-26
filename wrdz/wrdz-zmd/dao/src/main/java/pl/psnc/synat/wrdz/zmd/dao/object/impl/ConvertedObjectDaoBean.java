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
import pl.psnc.synat.wrdz.zmd.dao.object.ConvertedObjectDao;
import pl.psnc.synat.wrdz.zmd.dao.object.ConvertedObjectFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.ConvertedObjectSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.ConvertedObject;

/**
 * A class managing the persistence of {@link ConvertedObject} class. It implements additional operations available for
 * {@link ConvertedObject} object (as defined in {@link ConvertedObjectDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ConvertedObjectDaoBean extends
        ExtendedGenericDaoBean<ConvertedObjectFilterFactory, ConvertedObjectSorterBuilder, ConvertedObject, Long>
        implements ConvertedObjectDao {

    /**
     * Creates new instance of ConvertedObjectDaoBean.
     */
    public ConvertedObjectDaoBean() {
        super(ConvertedObject.class);
    }


    @Override
    protected ConvertedObjectFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ConvertedObject> criteriaQuery, Root<ConvertedObject> root, Long epoch) {
        return new ConvertedObjectFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected ConvertedObjectSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ConvertedObject> criteriaQuery, Root<ConvertedObject> root, Long epoch) {
        return new ConvertedObjectSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
