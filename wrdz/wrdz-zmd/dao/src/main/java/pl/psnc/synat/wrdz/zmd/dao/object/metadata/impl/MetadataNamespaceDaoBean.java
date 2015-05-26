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

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.TreeMap;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import pl.psnc.synat.wrdz.common.dao.ExtendedGenericDaoBean;
import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.MetadataNamespaceDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.MetadataNamespaceFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.MetadataNamespaceSorterBuilder;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataFile_;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataNamespace;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataNamespace_;
import pl.psnc.synat.wrdz.zmd.entity.types.MetadataType;

/**
 * A class managing the persistence of {@link MetadataNamespace} class. It implements additional operations available
 * for {@link MetadataNamespace} object (as defined in {@link MetadataNamespaceDao} ).
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class MetadataNamespaceDaoBean extends
        ExtendedGenericDaoBean<MetadataNamespaceFilterFactory, MetadataNamespaceSorterBuilder, MetadataNamespace, Long>
        implements MetadataNamespaceDao {

    /**
     * Creates new instance of MetadataNamespaceDaoBean.
     */
    public MetadataNamespaceDaoBean() {
        super(MetadataNamespace.class);
    }


    @Override
    protected MetadataNamespaceFilterFactory createQueryFilterFactory(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<MetadataNamespace> criteriaQuery, Root<MetadataNamespace> root, Long epoch) {
        return new MetadataNamespaceFilterFactoryImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    protected MetadataNamespaceSorterBuilder createQuerySorterBuilder(CriteriaBuilder criteriaBuilder,
            CriteriaQuery<MetadataNamespace> criteriaQuery, Root<MetadataNamespace> root, Long epoch) {
        return new MetadataNamespaceSorterBuilderImpl(criteriaBuilder, criteriaQuery, root, epoch);
    }


    @Override
    public Map<NamespaceType, Long> getObjectCounts() {
        return getCounts(EnumSet.of(MetadataType.OBJECTS_EXTRACTED, MetadataType.OBJECTS_PROVIDED));
    }


    @Override
    public Map<NamespaceType, Long> getDataFileCounts() {
        return getCounts(EnumSet.of(MetadataType.FILES_EXTRACTED, MetadataType.FILES_PROVIDED));
    }


    /**
     * Returns a map containing metadata formats and the number of metadata files using them.
     * 
     * Only the formats that have a non-zero metadata file count are included in the map.
     * 
     * @param types
     *            the types of metadata files to be taken into account
     * @return a <format type, number of metadata files using it> map
     */
    protected Map<NamespaceType, Long> getCounts(Collection<MetadataType> types) {
        CriteriaQuery<Tuple> query = criteriaBuilder.createTupleQuery();
        Root<MetadataFile> root = query.from(MetadataFile.class);
        Join<MetadataFile, MetadataNamespace> joinNamespace = root.join(MetadataFile_.usedNamespaces);

        query.where(root.get(MetadataFile_.type).in(types));

        query.groupBy(joinNamespace.get(MetadataNamespace_.type));

        query.multiselect(joinNamespace.get(MetadataNamespace_.type), criteriaBuilder.count(root));

        Map<NamespaceType, Long> results = new TreeMap<NamespaceType, Long>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            results.put((NamespaceType) tuple.get(0), (Long) tuple.get(1));
        }

        return results;
    }

}
