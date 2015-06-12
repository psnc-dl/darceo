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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileFormatFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileFormat;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileFormat_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link DataFileFormat}.
 */
public class DataFileFormatFilterFactoryImpl extends GenericQueryFilterFactoryImpl<DataFileFormat> implements
        DataFileFormatFilterFactory {

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
    public DataFileFormatFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DataFileFormat> criteriaQuery, Root<DataFileFormat> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<DataFileFormat> byPuid(String puid) {
        Predicate predicate = criteriaBuilder.equal(root.get(DataFileFormat_.puid), puid);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<DataFileFormat> byMimeType(String mimeType) {
        Predicate predicate = criteriaBuilder.equal(root.get(DataFileFormat_.mimeType), mimeType);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<DataFileFormat> byVersion(String version) {
        Predicate predicate = null;
        if (version != null) {
            predicate = criteriaBuilder.equal(root.get(DataFileFormat_.version), version);
        } else {
            predicate = criteriaBuilder.isNull(root.get(DataFileFormat_.version));
        }
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<DataFileFormat> byIdGreaterThan(long minId) {
        Predicate predicate = criteriaBuilder.greaterThan(root.get(DataFileFormat_.id), minId);
        return constructQueryFilter(predicate);
    }
}
