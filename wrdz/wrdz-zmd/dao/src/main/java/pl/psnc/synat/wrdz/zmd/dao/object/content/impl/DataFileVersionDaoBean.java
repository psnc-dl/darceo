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
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileVersionDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileVersionFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileVersionSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;

/**
 * Default implementation of {@link DataFileVersionDao}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class DataFileVersionDaoBean extends
        ExtendedGenericDaoBean<DataFileVersionFilterFactory, DataFileVersionSorterBuilder, DataFileVersion, Long>
        implements DataFileVersionDao {

    /**
     * Creates new instance of DataFileDaoBean.
     */
    public DataFileVersionDaoBean() {
        super(DataFileVersion.class);
    }


    @Override
    protected DataFileVersionFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DataFileVersion> criteriaQuery, Root<DataFileVersion> root, Long epoch) {
        return new DataFileVersionFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected DataFileVersionSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DataFileVersion> criteriaQuery, Root<DataFileVersion> root, Long epoch) {
        return new DataFileVersionSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }

}
