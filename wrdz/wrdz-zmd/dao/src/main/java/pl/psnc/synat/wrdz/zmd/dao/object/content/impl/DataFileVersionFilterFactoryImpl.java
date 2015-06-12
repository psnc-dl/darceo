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

import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileVersionFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileVersion_;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFile_;

/**
 * Default implementation of {@link DataFileVersionFilterFactory}.
 */
public class DataFileVersionFilterFactoryImpl extends GenericQueryFilterFactoryImpl<DataFileVersion> implements
        DataFileVersionFilterFactory {

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
    public DataFileVersionFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<DataFileVersion> criteriaQuery, Root<DataFileVersion> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<DataFileVersion> byFilename(String name) {
        Predicate predicate = criteriaBuilder.equal(root.join(DataFileVersion_.dataFile).get(DataFile_.filename), name);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<DataFileVersion> byFilenames(Collection<String> names) {
        Predicate predicate = root.join(DataFileVersion_.dataFile).get(DataFile_.filename).in(names);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<DataFileVersion> byContentVersionNo(Long id, Integer number, boolean isMainFile) {
        Join<?, ContentVersion> versions = null;
        if (isMainFile) {
            versions = root.join(DataFileVersion_.dataFile).join(DataFile_.mainFileIn);
        } else {
            versions = root.join(DataFileVersion_.contentVersion);
        }
        Predicate predicate = criteriaBuilder.equal(versions.get(ContentVersion_.version), number);
        Join<ContentVersion, DigitalObject> objects = versions.join(ContentVersion_.object);
        predicate = criteriaBuilder.and(criteriaBuilder.equal(objects.get(DigitalObject_.id), id), predicate);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<DataFileVersion> byContentVersionNo(Long id, boolean isMainFile) {
        Join<?, ContentVersion> versions = null;
        if (isMainFile) {
            versions = root.join(DataFileVersion_.dataFile).join(DataFile_.mainFileIn);
        } else {
            versions = root.join(DataFileVersion_.contentVersion);
        }
        Predicate predicate = criteriaBuilder.equal(versions.get(ContentVersion_.id), id);
        return constructQueryFilter(predicate);
    }
}
