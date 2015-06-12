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
package pl.psnc.synat.wrdz.zmd.dao.object.content.impl;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileFormatNameDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileFormatNameFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileFormatNameSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileFormatName;

/**
 * A class managing the persistence of {@link DataFileFormatName} class. It implements additional operations available
 * for {@link DataFileFormatName} object (as defined in {@link DataFileFormatNameDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class DataFileFormatNameDaoBean
        extends
        ExtendedGenericDaoBean<DataFileFormatNameFilterFactory, DataFileFormatNameSorterBuilder, DataFileFormatName, Long>
        implements DataFileFormatNameDao {

    /**
     * Creates new instance of DataFileFormatNameDaoBean.
     */
    public DataFileFormatNameDaoBean() {
        super(DataFileFormatName.class);
    }


    @Override
    protected DataFileFormatNameFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DataFileFormatName> criteriaQuery, Root<DataFileFormatName> root, Long epoch) {
        return new DataFileFormatNameFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected DataFileFormatNameSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DataFileFormatName> criteriaQuery, Root<DataFileFormatName> root, Long epoch) {
        return new DataFileFormatNameSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
