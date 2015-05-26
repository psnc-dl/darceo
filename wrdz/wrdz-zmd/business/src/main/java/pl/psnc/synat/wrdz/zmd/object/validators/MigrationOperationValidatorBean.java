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
package pl.psnc.synat.wrdz.zmd.object.validators;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.utility.CompareUtility;
import pl.psnc.synat.wrdz.zmd.dao.object.DigitalObjectDao;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.MigrationDao;
import pl.psnc.synat.wrdz.zmd.dao.object.migration.MigrationFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.Identifier;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;
import pl.psnc.synat.wrdz.zmd.entity.types.MigrationType;
import pl.psnc.synat.wrdz.zmd.entity.types.ObjectType;
import pl.psnc.synat.wrdz.zmd.input.MigrationInformation;
import pl.psnc.synat.wrdz.zmd.input.MigrationInformationUpdate;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectCreationRequest;
import pl.psnc.synat.wrdz.zmd.input.object.ObjectModificationRequest;
import pl.psnc.synat.wrdz.zmd.object.ObjectBrowser;
import pl.psnc.synat.wrdz.zmd.object.ObjectCreationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectModificationException;
import pl.psnc.synat.wrdz.zmd.object.ObjectNotFoundException;

/**
 * Bean validating the migration specifying request.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MigrationOperationValidatorBean implements MigrationOperationValidator {

    /**
     * Migration DAO.
     */
    @EJB
    private MigrationDao migrationDao;

    /**
     * Digital object Dao.
     */
    @EJB
    private DigitalObjectDao digitalObjectDaoBean;

    /**
     * Digital object finder.
     */
    @EJB
    private ObjectBrowser objectBrowser;


    @Override
    public void validateMigrationOperations(ObjectModificationRequest request)
            throws ObjectModificationException {
        DigitalObject object = null;
        try {
            object = objectBrowser.getDigitalObject(request.getIdentifier());
        } catch (ObjectNotFoundException e) {
            throw new ObjectModificationException("Object specified for modification does not exist.");
        }
        if (!checkIfMigrationOperationsAreExclusive(request.getMigratedFrom(), request.getMigratedToToAdd(),
            request.getMigratedToToModify(), request.getMigratedToToRemove())) {
            throw new ObjectModificationException(
                    "Invalid modification request, multiple migration operations reference the same object.");
        }
        checkIfMigrationOperationsAreCorrectlySpecified(object, request.getMigratedFrom(),
            request.getMigratedToToAdd(), request.getMigratedToToModify(), request.getMigratedToToRemove());
    }


    @Override
    public void validateMigrationOperations(ObjectCreationRequest request)
            throws ObjectCreationException {
        ObjectType type = request.getType();
        if (!checkIfMigrationOperationsAreExclusive(request.getMigratedFrom(), request.getMigratedTo(), null, null)) {
            throw new ObjectCreationException(
                    "Invalid creation request, multiple migration operations reference the same object.");
        } else {
            try {
                validateMigratedFromInformation(type, request.getMigratedFrom());
                if (request.getMigratedTo() != null) {
                    for (MigrationInformation migrationInformation : request.getMigratedTo()) {
                        validateMigratedToInformation(type, migrationInformation);
                    }
                }
            } catch (ObjectModificationException e) {
                throw new ObjectCreationException(e.getMessage(), e);
            }
        }
    }


    /**
     * Checks if specified changesets of object's migrations are exclusive, i.e. no object's identifier mentioned in
     * those migrations can be found twice in the changesets.
     * 
     * @param migratedFrom
     *            specified object's origin.
     * @param migratedToToAdd
     *            set of outgoing migrations added the object.
     * @param migratedToToModify
     *            set of outgoing migrations modified in object.
     * @param migratedToToRemove
     *            set of outgoing migrations deleted from the object.
     * @return <code>true</code> if all operations are exclusive, <code>false</code> otherwise.
     */
    private boolean checkIfMigrationOperationsAreExclusive(MigrationInformation migratedFrom,
            Set<MigrationInformation> migratedToToAdd, Set<MigrationInformationUpdate> migratedToToModify,
            Set<String> migratedToToRemove) {
        Set<String> identifiers = new HashSet<String>();
        if (migratedFrom != null) {
            identifiers.add(migratedFrom.getIdentifier());
        }
        if (migratedToToAdd != null && migratedToToAdd.size() > 0) {
            for (MigrationInformation added : migratedToToAdd) {
                if (!identifiers.add(added.getIdentifier())) {
                    return false;
                }
            }
        }
        if (migratedToToModify != null && migratedToToModify.size() > 0) {
            for (MigrationInformationUpdate modified : migratedToToModify) {
                if (!identifiers.add(modified.getIdentifier())) {
                    return false;
                }
            }
        }
        if (migratedToToRemove != null && migratedToToRemove.size() > 0) {
            for (String id : migratedToToRemove) {
                if (!identifiers.add(id)) {
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * Checks if specified changesets of object's migrations are correctly specified, i.e. if all information in the
     * carrier object is consistent and if there is no overlapping (i.e. trying to add already added file or remove
     * nonexisting one etc.).
     * 
     * @param object
     *            checked digital object.
     * @param migratedFrom
     *            specified object's origin.
     * @param migratedToToAdd
     *            set of outgoing migrations added the object.
     * @param migratedToToModify
     *            set of outgoing migrations modified in object.
     * @param migratedToToRemove
     *            set of outgoing migrations deleted from the object.
     * @throws ObjectModificationException
     *             should performed check fail.
     */
    private void checkIfMigrationOperationsAreCorrectlySpecified(DigitalObject object,
            MigrationInformation migratedFrom, Set<MigrationInformation> migratedToToAdd,
            Set<MigrationInformationUpdate> migratedToToModify, Set<String> migratedToToRemove)
            throws ObjectModificationException {
        ObjectType type = object.getType();
        long objectId = object.getId();
        validateMigratedFromInformation(object, migratedFrom);
        if (migratedToToAdd != null) {
            for (MigrationInformation migrationInformation : migratedToToAdd) {
                if (findMigrationTo(objectId, migrationInformation.getIdentifier()) != null) {
                    throw new ObjectModificationException("Trying to add already existing migration!");
                }
                validateMigratedToInformation(type, migrationInformation);
            }
        }
        if (migratedToToModify != null) {
            for (MigrationInformationUpdate migrationInformationUpdate : migratedToToModify) {
                validateMigratedToInformation(type,
                    findMigrationTo(objectId, migrationInformationUpdate.getIdentifier()));
            }
        }
        if (migratedToToRemove != null) {
            for (String derivativeIdentifier : migratedToToRemove) {
                if (findMigrationTo(objectId, derivativeIdentifier) == null) {
                    throw new ObjectModificationException("Trying to delete nonexistent migration!");
                }
            }
        }
    }


    /**
     * Fetches migration between specified source object and derivative with specified identifier.
     * 
     * @param sourceId
     *            object's source database id (primary key value).
     * @param derivativeIdentifier
     *            identifier of the object's derivative.
     * @return migration between source and derivative if found, otherwise <code>null</code>.
     */
    @SuppressWarnings({ "rawtypes" })
    private Migration findMigrationTo(long sourceId, String derivativeIdentifier) {
        MigrationFilterFactory filterFactory = migrationDao.createQueryModifier().getQueryFilterFactory();
        QueryFilter<Migration> predicate = filterFactory.bySource(sourceId);
        DigitalObject derivative = digitalObjectDaoBean.getDigitalObject(derivativeIdentifier);
        if (derivative != null) {
            predicate = filterFactory.and(predicate, filterFactory.byResult(derivative.getId()));
        } else {
            predicate = filterFactory.and(predicate, filterFactory.byResultIdentifier(derivativeIdentifier));
        }
        return migrationDao.findFirstResultBy(predicate);
    }


    /**
     * Validates information about object's migration to its derivative.
     * 
     * @param sourceType
     *            type of source object.
     * @param migration
     *            migration object.
     * @throws ObjectModificationException
     *             should migration from the specified source type to the derivative object described by the migration
     *             object be impossible to perform (violate migration workflow).
     */
    @SuppressWarnings("rawtypes")
    private void validateMigratedToInformation(ObjectType sourceType, Migration migration)
            throws ObjectModificationException {
        if (migration == null) {
            throw new ObjectModificationException("Migration to be modified not found in the database!");
        }
        if (migration.getMigrationResult() != null) {
            validateMigrationInformation(sourceType, migration.getMigrationResult().getType(), migration.getType());
        } else {
            validateMigrationInformation(sourceType, null, migration.getType());
        }
    }


    /**
     * Validates information about object's migration to its derivative.
     * 
     * @param sourceType
     *            type of source object.
     * @param information
     *            migration information object.
     * @throws ObjectModificationException
     *             should migration from the specified source type to the derivative object described by the migration
     *             object be impossible to perform (violate migration workflow).
     */
    private void validateMigratedToInformation(ObjectType sourceType, MigrationInformation information)
            throws ObjectModificationException {
        validateMigrationInformation(sourceType, getObjectType(information.getIdentifier()), information.getType());
    }


    /**
     * Validates information about object's migration from its source.
     * 
     * @param derivativeType
     *            type of derivative object.
     * @param information
     *            migration information object.
     * @throws ObjectModificationException
     *             should migration to the specified derivative type from the source object described by the migration
     *             object be impossible to perform (violate migration workflow).
     */
    private void validateMigratedFromInformation(ObjectType derivativeType, MigrationInformation information)
            throws ObjectModificationException {
        if (information != null) {
            validateMigrationInformation(getObjectType(information.getIdentifier()), derivativeType,
                information.getType());
        }
    }


    /**
     * Validates information about object's migration from its source and changes it proposes.
     * 
     * @param derivativeObject
     *            derivative object.
     * @param information
     *            migration information object.
     * @throws ObjectModificationException
     *             should migration to the specified derivative type from the source object described by the migration
     *             object be impossible to perform (violate migration workflow) or migration information bears no
     *             changes.
     */
    @SuppressWarnings("rawtypes")
    private void validateMigratedFromInformation(DigitalObject derivativeObject, MigrationInformation information)
            throws ObjectModificationException {
        if (information != null) {
            validateMigrationInformation(getObjectType(information.getIdentifier()), derivativeObject.getType(),
                information.getType());
            Migration origin = derivativeObject.getOrigin();
            if (origin != null) {
                validateChangeset(information, origin);
            }
        }
    }


    /**
     * Validates changes proposed by the modification object against current state of migration.
     * 
     * @param information
     *            migration modification object
     * @param origin
     *            migration that created currently processed object.
     * @throws ObjectModificationException
     *             should no changes be found in the migration modification object.
     */
    @SuppressWarnings({ "rawtypes" })
    private void validateChangeset(MigrationInformation information, Migration origin)
            throws ObjectModificationException {
        String identifier = information.getIdentifier();
        DigitalObject source = origin.getMigrationSource();
        boolean unchanged = isTargetObjectUnchanged(identifier, source, origin);
        if (information.getDate() != null) {
            unchanged = unchanged || CompareUtility.areEqual(information.getDate(), origin.getDate());
        }
        if (information.getInfo() != null) {
            unchanged = unchanged || CompareUtility.areEqual(information.getInfo(), origin.getInfo());
        }
        if (information.getResolver() != null) {
            unchanged = unchanged
                    || CompareUtility.areEqual(information.getResolver(), origin.getSourceIdentifierResolver());
        }
        if (unchanged) {
            throw new ObjectModificationException("Cannot modify migratedFrom to the same values.");
        }
    }


    /**
     * Checks whether or not any changes are made to the target objects.
     * 
     * @param identifier
     *            value of the new identifier.
     * @param source
     *            source object.
     * @param origin
     *            migration that created currently processed object.
     * @return <code>true</code> if no modifications had been found, <code>false</code> otherwise.
     */
    @SuppressWarnings({ "rawtypes" })
    private boolean isTargetObjectUnchanged(String identifier, DigitalObject source, Migration origin) {
        if (source != null) {
            List<Identifier> identifiers = source.getIdentifiers();
            for (Identifier id : identifiers) {
                if (identifier.equals(id.getIdentifier())) {
                    return true;
                }
            }
        } else {
            String id = origin.getSourceIdentifier();
            origin.getResultIdentifierResolver();
            if (id.equals(identifier)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Validates whether or not the specified migration path is compliant with the migration workflow.
     * 
     * @param sourceType
     *            type of source object.
     * @param derivativeType
     *            type of derivative object.
     * @param migrationType
     *            type of the migration.
     * @throws ObjectModificationException
     *             should migration of a specified type from the specified source type to the specified derivative type
     *             be impossible to perform (violate migration workflow).
     */
    private void validateMigrationInformation(ObjectType sourceType, ObjectType derivativeType,
            MigrationType migrationType)
            throws ObjectModificationException {
        if (derivativeType != null) {
            if (derivativeType != migrationType.getOutputType()) {
                throw new ObjectModificationException("Cannot create " + migrationType + " operation from "
                        + sourceType + " to " + derivativeType);
            }
        }
        if (sourceType != null) {
            ObjectType[] inputTypes = migrationType.getInputTypes();
            for (int i = 0; i < inputTypes.length; i++) {
                if (sourceType == inputTypes[i]) {
                    return;
                }
            }
            throw new ObjectModificationException("Cannot create " + migrationType + " operation from " + sourceType
                    + " to " + derivativeType);
        }
    }


    /**
     * Fetches the type of the digital object described by the specified identifier.
     * 
     * @param identifier
     *            object's identifier.
     * @return type of the object described by the identifier or <code>null</code> if none had been found.
     */
    private ObjectType getObjectType(String identifier) {
        DigitalObject digitalObject = digitalObjectDaoBean.getDigitalObject(identifier);
        if (digitalObject != null) {
            return digitalObject.getType();
        }
        return null;
    }

}
