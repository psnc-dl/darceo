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
package pl.psnc.synat.wrdz.zmd.dao.object.metadata.impl;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.GenericQueryFilterFactoryImpl;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.MetadataNamespaceFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataNamespace;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataNamespace_;

/**
 * Class providing easy access to creation of filtering arguments for the queries concerning {@link MetadataNamespace}.
 */
public class MetadataNamespaceFilterFactoryImpl extends GenericQueryFilterFactoryImpl<MetadataNamespace> implements
        MetadataNamespaceFilterFactory {

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
    public MetadataNamespaceFilterFactoryImpl(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<MetadataNamespace> criteriaQuery, Root<MetadataNamespace> root, Long epoch)
            throws IllegalArgumentException {
        super(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public QueryFilter<MetadataNamespace> byXmlns(String xmlns) {
        Predicate predicate = criteriaBuilder.equal(root.get(MetadataNamespace_.xmlns), xmlns);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<MetadataNamespace> bySchemaLocation(String schemaLocation) {
        Predicate predicate = null;
        if (schemaLocation != null) {
            predicate = criteriaBuilder.equal(root.get(MetadataNamespace_.schemaLocation), schemaLocation);
        } else {
            predicate = criteriaBuilder.isNull(root.get(MetadataNamespace_.schemaLocation));
        }
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<MetadataNamespace> byType(NamespaceType type) {
        Predicate predicate = criteriaBuilder.equal(root.get(MetadataNamespace_.type), type);
        return constructQueryFilter(predicate);
    }


    @Override
    public QueryFilter<MetadataNamespace> byType(List<NamespaceType> types) {
        Predicate predicate = root.get(MetadataNamespace_.type).in(types);
        return constructQueryFilter(predicate);
    }

}
