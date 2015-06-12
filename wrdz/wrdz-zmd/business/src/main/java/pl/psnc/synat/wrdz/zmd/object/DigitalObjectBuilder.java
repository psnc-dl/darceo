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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectDao;
import pl.psnc.synat.wrdz.zmd.dao.object.IdentifierDao;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier;
import pl.psnc.synat.wrdz.zmd.entity.object.content.ContentVersion;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;
import pl.psnc.synat.wrdz.zmd.input.MigrationInformation;
import pl.psnc.synat.wrdz.zmd.input.MigrationInformationUpdate;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.object.content.ContentVersionBuilder;
import pl.psnc.synat.wrdz.zmd.object.identifier.IdentifierScheme;
import pl.psnc.synat.wrdz.zmd.object.migration.MigrationDirection;
import pl.psnc.synat.wrdz.zmd.object.migration.ObjectMigrationManager;
import pl.psnc.synat.wrdz.zmd.output.object.DataFilesBundle;
import pl.psnc.synat.wrdz.zmd.output.object.MetadataFilesBundle;
import pl.psnc.synat.wrdz.zmd.output.object.ObjectCreationCache;
import pl.psnc.synat.wrdz.zmd.output.object.ObjectModificationCache;
import pl.psnc.synat.wrdz.zu.dto.user.UserDto;
import pl.psnc.synat.wrdz.zu.permission.ObjectPermissionManager;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Provides functionality for digital object's structure builder in the database.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
public class DigitalObjectBuilder {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DigitalObjectBuilder.class);

    /** Default identifier scheme. */
    private IdentifierScheme identifierScheme;

    /** Digital object DAO. */
    @EJB
    private DigitalObjectDao digitalObjectDao;

    /** Identifier DAO. */
    @EJB
    private IdentifierDao identifierDao;

    /** Content version builder. */
    @EJB
    private ContentVersionBuilder contentVersionBuilder;

    /** ZMD configuration POJO. */
    @Inject
    private ZmdConfiguration zmdConfig;

    /** Migration factory object. */
    @EJB
    private ObjectMigrationManager objectMigrationManager;

    /** User context. */
    @EJB
    private UserContext userContext;

    /** User browser. */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;

    /** Object permission manager. */
    @EJB(name = "ObjectPermissionManager")
    private ObjectPermissionManager permissionManager;


    /**
     * Initializes instance specific values dependent on injected beans.
     */
    @PostConstruct
    public void initialize() {
        identifierScheme = IdentifierScheme.valueOf(zmdConfig.getDefaultIdentifierType().name());
    }


    /**
     * Creates new digital object with first version.
     * 
     * @param request
     *            object creation request.
     * @param creationCache
     *            object's creation cache containing cached files.
     * @return newly created digital object.
     * @throws ObjectCreationException
     *             should any problems with object creation occur.
     * @throws ObjectModificationException
     *             should any problems with object creation occur.
     */
    public DigitalObject buildDigitalObject(ObjectCreationRequest request, ObjectCreationCache creationCache)
            throws ObjectCreationException, ObjectModificationException {
        DigitalObject object = request.getType().createNew();
        object.setOwnerId(userBrowser.getUserId(userContext.getCallerPrincipalName()));
        object.setName(request.getName());
        digitalObjectDao.persist(object);
        buildMigrations(object, request.getMigratedFrom(), request.getMigratedTo());
        logger.debug("provided id: " + request.getProposedId());
        Identifier identifier = identifierScheme.getGenerator(zmdConfig).generateIdentifier(identifierDao, object,
            request.getProposedId());
        object.setDefaultIdentifier(identifier);
        object.getIdentifiers().add(identifier);
        DataFilesBundle dataFilesBundle = new DataFilesBundle(creationCache.getAddedFiles(), null, null);
        MetadataFilesBundle metadataFilesBundle = new MetadataFilesBundle(creationCache.getAddedMetadata(), null, null);
        ContentVersion contentVersion = contentVersionBuilder.constructContentVersion(object, dataFilesBundle,
            metadataFilesBundle, request.getMainFile(), creationCache.getCachePath(), true,
            request.getMetsProvidedURI(), request.getInputFiles());
        object.setCurrentVersion(contentVersion);
        if (!object.getVersions().contains(contentVersion)) {
            object.getVersions().add(contentVersion);
        }

        UserDto owner = userBrowser.getUser(object.getOwnerId());
        if (owner == null) {
            throw new ObjectCreationException("Unknown owner");
        }
        permissionManager.setOwnerPermissions(owner.getUsername(), object.getId());

        return digitalObjectDao.merge(object);
    }


    /**
     * Updates digital object building its new version and modifying its migration information.
     * 
     * @param request
     *            object's modification request.
     * @param object
     *            digital object to be modified.
     * @param modificationCache
     *            object's modification cache containing cached files.
     * @return returns updated digital object.
     * @throws ObjectModificationException
     *             should any problems with object modification occur.
     */
    public DigitalObject buildDigitalObject(ObjectModificationRequest request, DigitalObject object,
            ObjectModificationCache modificationCache)
            throws ObjectModificationException {
        buildMigrations(object, request.getMigratedFrom(), request.getMigratedToToAdd(),
            request.getMigratedToToModify(), request.getMigratedToToRemove());
        DataFilesBundle dataFilesBundle = new DataFilesBundle(modificationCache.getAddedFiles(),
                modificationCache.getModifiedFiles(), request.getInputFilesToRemove());
        MetadataFilesBundle metadataFilesBundle = new MetadataFilesBundle(modificationCache.getAddedMetadata(),
                modificationCache.getModifiedMetadata(), request.getObjectMetadataToRemove());

        ContentVersion contentVersion = contentVersionBuilder.constructContentVersion(object, dataFilesBundle,
            metadataFilesBundle, request.getMainFile(), modificationCache.getCachePath(),
            request.getDeleteAllContent(), request.getMetsProvidedURI(), request.getInputFilesToAdd());
        object.setCurrentVersion(contentVersion);
        if (!object.getVersions().contains(contentVersion)) {
            object.getVersions().add(contentVersion);
        }
        return digitalObjectDao.merge(object);
    }


    /**
     * Updates digital object building its new version by current state of its structure in database - especially by its
     * migrations.
     * 
     * @param object
     *            digital object to be modified.
     * @param cachePath
     *            cache path - needed for mets.xml file
     * @return returns updated digital object.
     * @throws ObjectModificationException
     *             should any problems with object modification occur.
     */
    public DigitalObject buildDigitalObject(DigitalObject object, String cachePath)
            throws ObjectModificationException {
        DataFilesBundle dataFilesBundle = new DataFilesBundle(null, null, null);
        MetadataFilesBundle metadataFilesBundle = new MetadataFilesBundle(null, null, null);
        ContentVersion contentVersion = contentVersionBuilder.constructContentVersion(object, dataFilesBundle,
            metadataFilesBundle, null, cachePath, false);
        object.setCurrentVersion(contentVersion);
        if (!object.getVersions().contains(contentVersion)) {
            object.getVersions().add(contentVersion);
        }
        return digitalObjectDao.merge(object);
    }


    /**
     * Builds information about objects derived from this object.
     * 
     * @param object
     *            digital object for which migrations are built.
     * @param migratedFrom
     *            representation of operation that resulted in creation of this object.
     * @param migratedTo
     *            representation of operations that created derived objects.
     * @throws ObjectCreationException
     *             when persisting migration information fails.
     */
    @SuppressWarnings("rawtypes")
    private void buildMigrations(DigitalObject object, MigrationInformation migratedFrom,
            Set<MigrationInformation> migratedTo)
            throws ObjectCreationException {
        Migration from = null;
        List<Migration> to = new ArrayList<Migration>();
        if (migratedFrom != null) {
            from = objectMigrationManager.createMigration(migratedFrom, MigrationDirection.FROM);
        }
        if (migratedTo != null && migratedTo.size() > 0) {
            for (MigrationInformation migrationInformation : migratedTo) {
                Migration migration = objectMigrationManager.createMigration(migrationInformation,
                    MigrationDirection.TO);
                to.add(migration);
            }
        }
        associateMigrations(object, from, to);
    }


    /**
     * Builds migration information from the modification request.
     * 
     * @param object
     *            digital object for which migrations are built.
     * @param migratedFrom
     *            representation of operation that resulted in creation of this object.
     * @param migratedToToAdd
     *            representation of operations that created derived objects - new operations
     * @param migratedToToModify
     *            representation of operations that created derived objects - modified operations
     * @param migratedToToRemove
     *            representation of operations that created derived objects - removed operations
     * @throws ObjectModificationException
     *             should any problems with object modification occur.
     */
    @SuppressWarnings("rawtypes")
    private void buildMigrations(DigitalObject object, MigrationInformation migratedFrom,
            Set<MigrationInformation> migratedToToAdd, Set<MigrationInformationUpdate> migratedToToModify,
            Set<String> migratedToToRemove)
            throws ObjectModificationException {
        Migration from = null;
        List<Migration> addedTo = new ArrayList<Migration>();
        if (migratedFrom != null) {
            if (migratedFrom.isEmpty()) {
                objectMigrationManager.deleteMigratedFrom(object);
            } else {
                from = objectMigrationManager.modifyMigratedFrom(object, migratedFrom);
            }
        }
        if (migratedToToAdd != null && migratedToToAdd.size() > 0) {
            for (MigrationInformation migrationInformation : migratedToToAdd) {
                Migration migration = objectMigrationManager.createMigration(migrationInformation,
                    MigrationDirection.TO);
                addedTo.add(migration);
            }
        }
        if (migratedToToRemove != null && migratedToToRemove.size() > 0) {
            objectMigrationManager.deleteMigratedTo(object, migratedToToRemove);
        }
        if (migratedToToModify != null && migratedToToModify.size() > 0) {
            objectMigrationManager.modifyMigratedTo(object, migratedToToModify);
        }
        associateMigrations(object, from, addedTo);
    }


    /**
     * Associates migrations with the digital object.
     * 
     * @param object
     *            digital object to which migrations are associated.
     * @param migratedFrom
     *            representation of operation that resulted in creation of this object.
     * @param migratedTo
     *            representation of operations that created derived objects.
     */
    @SuppressWarnings({ "rawtypes" })
    private void associateMigrations(DigitalObject object, Migration migratedFrom, List<Migration> migratedTo) {
        if (migratedFrom != null) {
            migratedFrom.setMigrationResult(object);
            object.addSource(migratedFrom);
            object.setOwnerId(migratedFrom.getMigrationSource().getOwnerId());
        }
        if (migratedTo != null && migratedTo.size() > 0) {
            for (Migration migration : migratedTo) {
                migration.setMigrationSource(object);
                object.addDerivative(migration);
            }
        }
    }

}
