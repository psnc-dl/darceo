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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.MigrationFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration_;
import pl.psnc.synat.wrdz.zmd.entity.types.MigrationType;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link Migration}.
 */
@SuppressWarnings("rawtypes")
public class MigrationFilterFactoryImpl extends GenericQueryFilterFactoryImpl<Migration> implements
        MigrationFilterFactory {

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
    public MigrationFilterFactoryImpl(CriteriaBuilder criteriaBuilder, CriteriaQuery<Migration> criteriaQuery,
            Root<Migration> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<Migration> byResultIdentifier(String identifier) {
        Predicate predicate = criteriaBuilder.equal(root.get(Migration_.resultIdentifier), identifier);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<Migration> bySourceIdentifier(String identifier) {
        Predicate predicate = criteriaBuilder.equal(root.get(Migration_.sourceIdentifier), identifier);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<Migration> byType(MigrationType type) {
        Predicate predicate = criteriaBuilder.equal(root.get(Migration_.type), type);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<Migration> byResult(long resultId) {
        Predicate predicate = criteriaBuilder.equal(root.join(Migration_.migrationResult).get(DigitalObject_.id),
            resultId);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<Migration> bySource(long sourceId) {
        Predicate predicate = criteriaBuilder.equal(root.join(Migration_.migrationSource).get(DigitalObject_.id),
            sourceId);
        return constructQueryFilter(predicate);
    }

}
