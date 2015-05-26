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

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmd.dao.object.IdentifierFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject_;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier_;
import pl.psnc.synat.wrdz.zmd.entity.types.IdentifierType;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link Identifier}.
 */
public class IdentifierFilterFactoryImpl extends GenericQueryFilterFactoryImpl<Identifier> implements
        IdentifierFilterFactory {

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
    public IdentifierFilterFactoryImpl(CriteriaBuilder criteriaBuilder, CriteriaQuery<Identifier> criteriaQuery,
            Root<Identifier> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<Identifier> byObject(Long objectId) {
        Predicate predicate = criteriaBuilder.equal(root.get(Identifier_.object).get(DigitalObject_.id), objectId);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<Identifier> byIdentifier(String identifier) {
        Predicate predicate = criteriaBuilder.equal(root.get(Identifier_.identifier), identifier);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<Identifier> byType(IdentifierType type) {
        Predicate predicate = criteriaBuilder.equal(root.get(Identifier_.type), type);
        return constructQueryFilter(predicate);
    }

}
