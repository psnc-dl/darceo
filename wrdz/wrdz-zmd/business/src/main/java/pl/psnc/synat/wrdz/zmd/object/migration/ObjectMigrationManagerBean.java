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
package pl.psnc.synat.wrdz.zmd.object.migration;

import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectDao;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.IdentifierDao;
import pl.psnc.synat.wrdz.zmd.dao.object.IdentifierFilterFactory;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.MigrationDao;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.MigrationFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.ConvertedObject;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier;
import pl.psnc.synat.wrdz.zmd.entity.object.LosslessObject;
import pl.psnc.synat.wrdz.zmd.entity.object.MasterObject;
import pl.psnc.synat.wrdz.zmd.entity.object.OptimizedObject;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;
import pl.psnc.synat.wrdz.zmd.entity.types.MigrationType;
import pl.psnc.synat.wrdz.zmd.input.MigrationInformation;
import pl.psnc.synat.wrdz.zmd.input.MigrationInformationUpdate;
import pl.psnc.synat.wrdz.zmd.object.ObjectDerivatives;
import pl.psnc.synat.wrdz.zmd.object.ObjectModificationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectNotFoundException;
import pl.psnc.synat.wrdz.zmd.object.ObjectOrigin;

/**
 * Bean providing functionalities of operations on migrations of digital objects.
 */
@Stateless
public class ObjectMigrationManagerBean implements ObjectMigrationManager {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ObjectMigrationManagerBean.class);

    /**
     * Object's identifier DAO.
     */
    @EJB
    private IdentifierDao identifierDaoBean;

    /**
     * Digital object DAO bean.
     */
    @EJB
    private DigitalObjectDao digitalObjectDaoBean;

    /**
     * Migration DAO bean.
     */
    @EJB
    private MigrationDao migrationDaoBean;

    /**
     * Builds object migrations.
     */
    @EJB
    private ObjectMigrationBuilder objectMigrationBuilder;


    @Override
    @SuppressWarnings({ "rawtypes" })
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public Migration createMigration(MigrationInformation migrationInfo, MigrationDirection direction) {
        Migration migration = null;
        String identifier = migrationInfo.getIdentifier();
        IdentifierFilterFactory queryFilterFactory = identifierDaoBean.createQueryModifier().getQueryFilterFactory();
        List<Identifier> matchedIds = identifierDaoBean.findBy(queryFilterFactory.byIdentifier(identifier), false);
        if (matchedIds != null && matchedIds.size() == 1) {
            migration = objectMigrationBuilder.buildMigration(migrationInfo, direction, matchedIds.get(0).getObject());
        } else {
            migration = objectMigrationBuilder.buildMigration(migrationInfo, direction, null);
        }
        return migration;
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void deleteMigratedFrom(DigitalObject object) {
        switch (object.getType()) {
            case CONVERTED:
                ConvertedObject converted = (ConvertedObject) object;
                if (converted.getConvertedFrom() != null) {
                    disassociateSource(converted.getConvertedFrom());
                    migrationDaoBean.delete(converted.getConvertedFrom());
                    converted.setConvertedFrom(null);
                }
                break;
            case MASTER:
                MasterObject master = (MasterObject) object;
                if (master.getTransformedFrom() != null) {
                    disassociateSource(master.getTransformedFrom());
                    migrationDaoBean.delete(master.getTransformedFrom());
                    master.setTransformedFrom(null);
                }
                break;
            case OPTIMIZED:
                OptimizedObject optimized = (OptimizedObject) object;
                if (optimized.getOptimizedFrom() != null) {
                    disassociateSource(optimized.getOptimizedFrom());
                    migrationDaoBean.delete(optimized.getOptimizedFrom());
                    optimized.setOptimizedFrom(null);
                }
                break;
            default:
                throw new WrdzRuntimeException("Unsupported migration type.");
        }

    }


    @Override
    @SuppressWarnings({ "rawtypes" })
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public Migration modifyMigratedFrom(DigitalObject object, MigrationInformation migrationInfo) {
        switch (object.getType()) {
            case CONVERTED:
                if (migrationInfo.getType() == MigrationType.CONVERSION) {
                    ConvertedObject converted = (ConvertedObject) object;
                    if (converted.getConvertedFrom() == null) {
                        return createMigration(migrationInfo, MigrationDirection.FROM);
                    } else {
                        Migration migration = modifyMigratedFrom(converted.getConvertedFrom(), migrationInfo);
                        return migration;
                    }
                } else {
                    throw new WrdzRuntimeException("Specified migration type" + migrationInfo.getType().name()
                            + " cannot result in creation of the object type " + object.getType().name());
                }
            case MASTER:
                if (migrationInfo.getType() == MigrationType.TRANSFORMATION) {
                    MasterObject master = (MasterObject) object;
                    if (master.getTransformedFrom() == null) {
                        return createMigration(migrationInfo, MigrationDirection.FROM);
                    } else {
                        Migration migration = modifyMigratedFrom(master.getTransformedFrom(), migrationInfo);
                        return migration;
                    }
                } else {
                    throw new WrdzRuntimeException("Specified migration type" + migrationInfo.getType().name()
                            + " cannot result in creation of the object type " + object.getType().name());
                }
            case OPTIMIZED:
                if (migrationInfo.getType() == MigrationType.OPTIMIZATION) {
                    OptimizedObject optimized = (OptimizedObject) object;
                    if (optimized.getOptimizedFrom() == null) {
                        return createMigration(migrationInfo, MigrationDirection.FROM);
                    } else {
                        Migration migration = modifyMigratedFrom(optimized.getOptimizedFrom(), migrationInfo);
                        return migration;
                    }
                } else {
                    throw new WrdzRuntimeException("Specified migration type" + migrationInfo.getType().name()
                            + " cannot result in creation of the object type " + object.getType().name());
                }
            default:
                throw new WrdzRuntimeException("Unrecognized object type.");
        }
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void deleteMigratedTo(DigitalObject object, Set<String> toDelete)
            throws ObjectModificationException {
        boolean wasFound;
        for (String identifier : toDelete) {
            wasFound = findAndRemoveMigratedTo(object.getConvertedTo(), identifier);
            if (!wasFound && object instanceof LosslessObject) {
                LosslessObject lossless = (LosslessObject) object;
                wasFound = findAndRemoveMigratedTo(lossless.getOptimizedTo(), identifier);
                if (!wasFound && object instanceof MasterObject) {
                    MasterObject master = (MasterObject) object;
                    if (!findAndRemoveMigratedTo(master.getTransformedTo(), identifier)) {
                        logger.debug("Could not find migration to object with a given identifier: " + identifier);
                        throw new ObjectModificationException("Migration to object with a given identifier: "
                                + identifier + "does not exist. Unable to perform modification operation.");
                    }
                }
            }
        }
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.MANDATORY)
    public void modifyMigratedTo(DigitalObject object, Set<MigrationInformationUpdate> toModify)
            throws ObjectModificationException {
        boolean wasFound;
        for (MigrationInformationUpdate update : toModify) {
            wasFound = findAndModifyMigratedTo(object.getConvertedTo(), update);
            if (!wasFound && object instanceof LosslessObject) {
                LosslessObject lossless = (LosslessObject) object;
                wasFound = findAndModifyMigratedTo(lossless.getOptimizedTo(), update);
                if (!wasFound && object instanceof MasterObject) {
                    MasterObject master = (MasterObject) object;
                    if (!findAndModifyMigratedTo(master.getTransformedTo(), update)) {
                        logger.debug("Could not find migration to object with a given identifier: "
                                + update.getIdentifier());
                        throw new ObjectModificationException("Migration to object with a given identifier: "
                                + update.getIdentifier() + "does not exist. Unable to perform modification operation.");
                    }
                }
            }
        }
    }


    @Override
    @SuppressWarnings("rawtypes")
    public Migration getMigrationByResultIdentifier(String identifier)
            throws MigrationNotFoundException {
        MigrationFilterFactory queryFilterFactory = migrationDaoBean.createQueryModifier().getQueryFilterFactory();
        Migration migration = migrationDaoBean.findFirstResultBy(queryFilterFactory.byResultIdentifier(identifier));
        if (migration != null) {
            return migration;
        } else {
            throw new MigrationNotFoundException("Migration does not exist.");
        }
    }


    @Override
    @SuppressWarnings("rawtypes")
    public List<Migration> getMigrationsBySourceIdentifier(String identifier)
            throws MigrationNotFoundException {
        MigrationFilterFactory queryFilterFactory = migrationDaoBean.createQueryModifier().getQueryFilterFactory();
        List<Migration> migrations = migrationDaoBean.findBy(queryFilterFactory.bySourceIdentifier(identifier), false);
        if (migrations != null && !migrations.isEmpty()) {
            return migrations;
        } else {
            throw new MigrationNotFoundException("Migration does not exist.");
        }
    }


    @Override
    public ObjectOrigin getOrigin(String identifier)
            throws ObjectNotFoundException {
        try {
            DigitalObject object = getDigitalObject(identifier);
            ObjectOriginBuilder builder = null;
            if (object instanceof MasterObject) {
                MasterObject master = (MasterObject) object;
                builder = new ObjectOriginBuilder(master.getTransformedFrom());
            } else if (object instanceof OptimizedObject) {
                OptimizedObject optimized = (OptimizedObject) object;
                builder = new ObjectOriginBuilder(optimized.getOptimizedFrom());
            } else if (object instanceof ConvertedObject) {
                ConvertedObject converted = (ConvertedObject) object;
                builder = new ObjectOriginBuilder(converted.getConvertedFrom());
            }
            return builder.build();
        } catch (ObjectNotFoundException e) {
            logger.debug("Digital object: " + identifier + " does not exists in WRDZ.");
            try {
                ReverseObjectOriginBuilder builder = new ReverseObjectOriginBuilder(
                        getMigrationByResultIdentifier(identifier));
                return builder.build();
            } catch (MigrationNotFoundException f) {
                logger.debug("Digital object: " + identifier + " is not a result of migration of any object from WRDZ.");
                throw e;
            }
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public ObjectDerivatives getDerivatives(String identifier)
            throws ObjectNotFoundException {
        try {
            DigitalObject object = getDigitalObject(identifier);
            ObjectDerivativesBuilder builder = null;
            if (object instanceof MasterObject) {
                MasterObject master = (MasterObject) object;
                builder = new ObjectDerivativesBuilder((List<Migration<?, ?>>) (List<?>) master.getTransformedTo())
                        .addMigrations((List<Migration<?, ?>>) (List<?>) master.getOptimizedTo()).addMigrations(
                            (List<Migration<?, ?>>) (List<?>) master.getConvertedTo());
            } else if (object instanceof OptimizedObject) {
                OptimizedObject optimized = (OptimizedObject) object;
                builder = new ObjectDerivativesBuilder((List<Migration<?, ?>>) (List<?>) optimized.getOptimizedTo())
                        .addMigrations((List<Migration<?, ?>>) (List<?>) optimized.getConvertedTo());
            } else if (object instanceof ConvertedObject) {
                ConvertedObject converted = (ConvertedObject) object;
                builder = new ObjectDerivativesBuilder((List<Migration<?, ?>>) (List<?>) converted.getConvertedTo());
            }
            return builder.build();
        } catch (ObjectNotFoundException e) {
            logger.debug("Digital object: " + identifier + " does not exists in WRDZ.");
            try {
                ReverseObjectDerivativesBuilder builder = new ReverseObjectDerivativesBuilder(
                        (List<Migration<?, ?>>) (List<?>) getMigrationsBySourceIdentifier(identifier));
                return builder.build();
            } catch (MigrationNotFoundException f) {
                logger.debug("Digital object: " + identifier
                        + " is not a source of migration for any object from WRDZ.");
                throw e;
            }
        }
    }


    /**
     * Gets the digital object at the public identifier. If it does not exist the <code>ObjectNotFoundException</code>
     * is thrown.
     * 
     * @param identifier
     *            public identifier of the object
     * @return digital object
     * @throws ObjectNotFoundException
     *             when the object does not exist
     */
    private DigitalObject getDigitalObject(String identifier)
            throws ObjectNotFoundException {
        DigitalObjectFilterFactory queryFilterFactory = digitalObjectDaoBean.createQueryModifier()
                .getQueryFilterFactory();
        DigitalObject object = digitalObjectDaoBean.findFirstResultBy(queryFilterFactory.byIdentifier(identifier));
        if (object != null) {
            return object;
        } else {
            logger.debug("Digital object " + identifier + " does not exists.");
            throw new ObjectNotFoundException("Digital object " + identifier + " does not exists.");
        }
    }


    /**
     * Modifies the existing migration information concerning the migration that created the object.
     * 
     * @param migration
     *            migration from the source object to be modified.
     * @param migrationInfo
     *            modification information
     * @return modified migration entry.
     */
    @SuppressWarnings({ "rawtypes" })
    private Migration modifyMigratedFrom(Migration migration, MigrationInformation migrationInfo) {
        String identifier = migrationInfo.getIdentifier();
        IdentifierFilterFactory queryFilterFactory = identifierDaoBean.createQueryModifier().getQueryFilterFactory();
        List<Identifier> matchedIds = identifierDaoBean.findBy(queryFilterFactory.byIdentifier(identifier), false);
        migrationInfo = inheritUnmodifiedFields(migration, migrationInfo);
        Migration result = null;
        if (matchedIds != null && matchedIds.size() == 1) {
            DigitalObject object = matchedIds.get(0).getObject();
            result = objectMigrationBuilder.buildMigration(migrationInfo, MigrationDirection.FROM, object);
        } else {
            result = objectMigrationBuilder.buildMigration(migrationInfo, MigrationDirection.FROM, null);
        }
        migrationDaoBean.delete(migration);
        if (migration.getMigrationSource() != null) {
            disassociateSource(migration);
        }
        migrationDaoBean.flush();
        migrationDaoBean.persist(result);
        return result;
    }


    /**
     * Fills in migration modification information object with data inherited from previous version of migration.
     * 
     * @param migration
     *            migration before modification.
     * @param migrationInfo
     *            migration modification information.
     * @return filled in migration modification information.
     */
    @SuppressWarnings("rawtypes")
    private MigrationInformation inheritUnmodifiedFields(Migration migration, MigrationInformation migrationInfo) {
        String identifier = migrationInfo.getIdentifier();
        if (identifier == null) {
            identifier = migration.getMigrationSource() != null ? migration.getMigrationSource().getDefaultIdentifier()
                    .getIdentifier() : migrationInfo.getIdentifier();
        }
        MigrationType type = migrationInfo.getType() != null ? migrationInfo.getType() : migration.getType();
        MigrationInformation result = new MigrationInformation(identifier, type);
        if (migrationInfo.getDate() == null) {
            migrationInfo.setDate(migration.getDate());
        }
        if (migrationInfo.getInfo() == null) {
            migrationInfo.setInfo(migration.getInfo());
        }
        if (migrationInfo.getResolver() == null) {
            migrationInfo.setResolver(migration.getResultIdentifierResolver());
        }
        return result;
    }


    /**
     * Finds and removes information about object's migration to derivative object, from the list of such migrations.
     * 
     * @param migratedTo
     *            list of object's migrations to derivative objects.
     * @param identifier
     *            identifier of the derivative object.
     * @return <code>true</code> if migration was found and deleted, otherwise <code>false</code>.
     */
    @SuppressWarnings("rawtypes")
    private boolean findAndRemoveMigratedTo(List<? extends Migration> migratedTo, String identifier) {
        if (migratedTo != null && !migratedTo.isEmpty()) {
            for (int i = 0; i < migratedTo.size(); i++) {
                if (isMigratedToFound(migratedTo.get(i), identifier)) {
                    Migration migration = migratedTo.get(i);
                    DigitalObject object = migration.getMigrationResult();
                    switch (object.getType()) {
                        case CONVERTED:
                            ((ConvertedObject) object).setConvertedFrom(null);
                            break;
                        case MASTER:
                            ((MasterObject) object).setTransformedFrom(null);
                            break;
                        case OPTIMIZED:
                            ((OptimizedObject) object).setOptimizedFrom(null);
                            break;
                        default:
                            throw new WrdzRuntimeException(
                                    "Unsupported migration type found while processing migrations, database integrity at risk.");
                    }
                    migrationDaoBean.delete(migration);
                    migratedTo.remove(i);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Finds and modifies the existing migration information concerning the migration that created the derivative
     * object.
     * 
     * @param migratedTo
     *            list of object's migrations to derivative objects.
     * @param update
     *            modification information
     * @return <code>true</code> if migration was found and modified, otherwise <code>false</code>.
     */
    @SuppressWarnings("rawtypes")
    private boolean findAndModifyMigratedTo(List<? extends Migration> migratedTo, MigrationInformationUpdate update) {
        if (migratedTo != null && !migratedTo.isEmpty()) {
            for (int i = 0; i < migratedTo.size(); i++) {
                if (isMigratedToFound(migratedTo.get(i), update.getIdentifier())) {
                    migratedTo.get(i).setResultIdentifierResolver(update.getResolver());
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Checks if given migration to derivative object is a migration to the obejct with the given identifier.
     * 
     * @param migrationTo
     *            bject's migration to derivative object.
     * @param identifier
     *            object's identifier.
     * @return <code>true</code> if one of target obejct's identifiers matches the passed one, otherwise
     *         <code>false</code>.
     */
    @SuppressWarnings({ "rawtypes" })
    private boolean isMigratedToFound(Migration migrationTo, String identifier) {
        if (migrationTo.getResultIdentifier() != null && migrationTo.getResultIdentifier().equals(identifier)) {
            return true;
        } else if (migrationTo.getMigrationResult() != null) {
            List<Identifier> identifiers = migrationTo.getMigrationResult().getIdentifiers();
            if (!identifiers.isEmpty()) {
                for (Identifier identifier2 : identifiers) {
                    if (identifier2.equals(identifier)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Disassociates migration from the source from the passed migration's derivative.
     * 
     * @param migration
     *            migration from source to currently processed object.
     */
    @SuppressWarnings("rawtypes")
    private void disassociateSource(Migration migration) {
        DigitalObject object = migration.getMigrationSource();
        switch (migration.getType()) {
            case CONVERSION:
                object.getConvertedTo().remove(migration);
                break;
            case OPTIMIZATION:
                ((LosslessObject) object).getOptimizedTo().remove(migration);
                break;
            case TRANSFORMATION:
                ((MasterObject) object).getTransformedTo().remove(migration);
                break;
            default:
                throw new WrdzRuntimeException(
                        "Unsupported migration type found while processing migrations, database integrity at risk.");
        }
        digitalObjectDaoBean.merge(object);
    }

}
