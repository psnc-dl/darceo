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
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.dao.object.content.ContentVersionDao;
import pl.psnc.synat.wrdz.zmd.dao.object.content.ContentVersionFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.content.ContentVersionSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;

/**
 * A class managing the persistence of {@link ContentVersion} class. It implements additional operations available for
 * {@link ContentVersion} object (as defined in {@link ContentVersionDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class ContentVersionDaoBean extends
        ExtendedGenericDaoBean<ContentVersionFilterFactory, ContentVersionSorterBuilder, ContentVersion, Long>
        implements ContentVersionDao {

    /**
     * Creates new instance of ContentVersionDaoBean.
     */
    public ContentVersionDaoBean() {
        super(ContentVersion.class);
    }


    @Override
    protected ContentVersionFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ContentVersion> criteriaQuery, Root<ContentVersion> root, Long epoch) {
        return new ContentVersionFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected ContentVersionSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<ContentVersion> criteriaQuery, Root<ContentVersion> root, Long epoch) {
        return new ContentVersionSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public ContentVersion getContentVersion(long id, int versionNo) {
        ContentVersionFilterFactory filterFactory = createQueryModifier().getQueryFilterFactory();
        QueryFilter<ContentVersion> filter = filterFactory.and(filterFactory.byVersion(versionNo),
            filterFactory.byObjectId(id));
        return findFirstResultBy(filter);
    }

}
