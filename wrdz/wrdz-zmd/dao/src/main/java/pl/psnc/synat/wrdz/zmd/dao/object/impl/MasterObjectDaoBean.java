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
import pl.psnc.synat.wrdz.zmd.dao.object.MasterObjectDao;
import pl.psnc.synat.wrdz.zmd.dao.object.MasterObjectFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.MasterObjectSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.MasterObject;

/**
 * A class managing the persistence of {@link MasterObject} class. It implements additional operations available for
 * {@link MasterObject} object (as defined in {@link MasterObjectDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class MasterObjectDaoBean extends
        ExtendedGenericDaoBean<MasterObjectFilterFactory, MasterObjectSorterBuilder, MasterObject, Long> implements
        MasterObjectDao {

    /**
     * Creates new instance of MasterObjectDaoBean.
     */
    public MasterObjectDaoBean() {
        super(MasterObject.class);
    }


    @Override
    protected MasterObjectFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<MasterObject> criteriaQuery, Root<MasterObject> root, Long epoch) {
        return new MasterObjectFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected MasterObjectSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<MasterObject> criteriaQuery, Root<MasterObject> root, Long epoch) {
        return new MasterObjectSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
