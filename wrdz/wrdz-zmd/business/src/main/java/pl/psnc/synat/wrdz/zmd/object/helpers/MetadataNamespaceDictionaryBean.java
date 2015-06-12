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
package pl.psnc.synat.wrdz.zmd.object.helpers;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.metadata.xmlns.Namespace;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.MetadataNamespaceDao;
import pl.psnc.synat.wrdz.zmd.dao.object.metadata.MetadataNamespaceFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.MetadataNamespace;

/**
 * Provides access and basic operation on the metadata namespace dictionary.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class MetadataNamespaceDictionaryBean {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(MetadataNamespaceDictionaryBean.class);

    /**
     * Validation warnings DAO.
     */
    @EJB
    private MetadataNamespaceDao metadataNamespaceDao;


    /**
     * Gets the metadata namespace form the dictionary for passed recognized namespace.
     * 
     * @param namespace
     *            namespaces recognized in the XML file
     * @return corresponding nemaspaces from the dictionary or null
     */
    public MetadataNamespace getMetadataNamespace(Namespace namespace) {
        if (namespace == null) {
            return null;
        }
        return getNamespace(namespace);
    }


    /**
     * Gets the metadata namespaces form the dictionary for passed recognized namespaces.
     * 
     * @param namespaces
     *            namespaces recognized in the XML file
     * @return corresponding nemaspaces from the dictionary or null
     */
    public List<MetadataNamespace> getMetadataNamespaces(List<Namespace> namespaces) {
        if (namespaces == null || namespaces.size() == 0) {
            return null;
        }
        List<MetadataNamespace> metadataNamespaces = new ArrayList<MetadataNamespace>();
        for (Namespace namespace : namespaces) {
            metadataNamespaces.add(getNamespace(namespace));
        }
        return metadataNamespaces;
    }


    /**
     * Checks if there is metadata namespace (in the dictionary) that corresponds to the namespace recognized in the XML
     * file and if not it creates it. At the end it returns this corresponding nemaspace from the dictionary.
     * 
     * @param namespace
     *            namespace recognized in the XML file
     * @return corresponding nemaspace from the dictionary
     */
    @SuppressWarnings("unchecked")
    private MetadataNamespace getNamespace(Namespace namespace) {
        MetadataNamespaceFilterFactory queryFilterFactory = metadataNamespaceDao.createQueryModifier()
                .getQueryFilterFactory();
        MetadataNamespace existingMetadataNamespace = metadataNamespaceDao.findFirstResultBy(queryFilterFactory.and(
            queryFilterFactory.byXmlns(namespace.getUri()),
            queryFilterFactory.bySchemaLocation(namespace.getSchemaLocation()),
            queryFilterFactory.byType(namespace.getType())));
        if (existingMetadataNamespace != null) {
            logger.debug("namespace " + namespace + " exists in the dictionary");
            return existingMetadataNamespace;
        } else {
            logger.debug("namespace " + namespace + " does not exist in the dictionary");
            MetadataNamespace newMetadataNamespace = new MetadataNamespace(namespace.getUri(),
                    namespace.getSchemaLocation(), namespace.getType());
            metadataNamespaceDao.persist(newMetadataNamespace);
            return newMetadataNamespace;
        }
    }

}
