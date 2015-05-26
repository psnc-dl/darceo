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

import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.dao.object.content.ContentVersionFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link ContentVersion}.
 */
public class ContentVersionFilterFactoryImpl extends GenericQueryFilterFactoryImpl<ContentVersion> implements
        ContentVersionFilterFactory {

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
    public ContentVersionFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ContentVersion> criteriaQuery, Root<ContentVersion> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<ContentVersion> byCreatedBefore(Date date, boolean inclusive) {
        Predicate predicate = null;
        if (inclusive) {
            predicate = criteriaBuilder.lessThanOrEqualTo(root.get(ContentVersion_.createdOn), date);
        } else {
            predicate = criteriaBuilder.lessThan(root.get(ContentVersion_.createdOn), date);
        }
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<ContentVersion> byCreatedAfter(Date date, boolean inclusive) {
        Predicate predicate = null;
        if (inclusive) {
            predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(ContentVersion_.createdOn), date);
        } else {
            predicate = criteriaBuilder.greaterThan(root.get(ContentVersion_.createdOn), date);
        }
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<ContentVersion> byObjectId(Long id) {
        Predicate predicate = criteriaBuilder.equal(root.get(ContentVersion_.object).get(DigitalObject_.id), id);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<ContentVersion> byVersion(Integer versionNo) {
        Predicate predicate = criteriaBuilder.equal(root.get(ContentVersion_.version), versionNo);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<ContentVersion> byVersionNewerThan(Integer versionNo) {
        Predicate predicate = criteriaBuilder.greaterThan(root.get(ContentVersion_.version), versionNo);
        return constructQueryFilter(predicate);
    }

}
