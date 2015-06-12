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
package pl.psnc.synat.wrdz.zmd.object;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.dsa.exception.DataStorageResourceException;
import pl.psnc.synat.wrdz.common.utility.UuidGenerator;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.storage.DataStorageAccess;

/**
 * Bean providing functionality of updating of digital objects which are origins and derivatives of other objects.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class ObjectUpdaterBean implements ObjectUpdater {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ObjectUpdaterBean.class);

    /**
     * Object browser bean.
     */
    @EJB
    private ObjectBrowser objectBrowser;

    /**
     * Digital object builder bean.
     */
    @EJB
    private DigitalObjectBuilder digitalObjectBuilder;

    /**
     * Provides an access to objects in data storage.
     */
    @EJB
    private DataStorageAccess dataStorageAccessBean;

    /**
     * ZMD module configuration.
     */
    @Inject
    private ZmdConfiguration zmdConfiguration;

    /**
     * Absolute path to the cache root folder.
     */
    private String cacheHome;

    /**
     * Identifier generator for cache session.
     */
    @Inject
    private UuidGenerator uuidGenerator;


    /**
     * Initializes this EJB.
     */
    @PostConstruct
    public void initialize() {
        cacheHome = zmdConfiguration.getCacheHome();
    }


    @Override
    public void updateOriginOrDerivative(String identifier)
            throws ObjectModificationException {
        if (identifier != null) {
            try {
                DigitalObject source = objectBrowser.getDigitalObject(identifier);
                String cachePath = cacheHome + "/" + uuidGenerator.generateCacheFolderName() + "/";
                source = digitalObjectBuilder.buildDigitalObject(source, cachePath);
                try {
                    dataStorageAccessBean.createVersion(source.getCurrentVersion());
                } catch (DataStorageResourceException e) {
                    logger.error("Object creation in data storage failed!", e);
                    throw new ObjectModificationException("Could not update object origin - data storge exception.");
                }
            } catch (ObjectNotFoundException e) {
                logger.debug("Is seems that identifier " + identifier + " refers to some remote object");
            }
        }
    }

}
