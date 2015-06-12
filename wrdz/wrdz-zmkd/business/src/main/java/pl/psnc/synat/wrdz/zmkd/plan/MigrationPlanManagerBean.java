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
package pl.psnc.synat.wrdz.zmkd.plan;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import pl.psnc.darceo.migration.DigitalObjectsCondition;
import pl.psnc.darceo.migration.Service;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.common.user.UserContext;
import pl.psnc.synat.wrdz.ru.composition.ServiceComposer;
import pl.psnc.synat.wrdz.ru.composition.TransformationChain;
import pl.psnc.synat.wrdz.zmd.object.IdentifierBrowser;
import pl.psnc.synat.wrdz.zmd.object.ObjectChecker;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationPathDao;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationPlanDao;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationPlanFilterFactory;
import pl.psnc.synat.wrdz.zmkd.entity.format.FileFormat;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationItemLog;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationItemStatus;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPath;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlanStatus;
import pl.psnc.synat.wrdz.zmkd.format.FileFormatDictionaryBean;
import pl.psnc.synat.wrdz.zmkd.format.UdfrServiceException;
import pl.psnc.synat.wrdz.zmkd.format.UnrecognizedIriException;
import pl.psnc.synat.wrdz.zmkd.format.UnrecognizedPuidException;
import pl.psnc.synat.wrdz.zmkd.plan.MigrationPlanDeletionException.MessageId;
import pl.psnc.synat.wrdz.zmkd.plan.validation.InvalidFormatException;
import pl.psnc.synat.wrdz.zmkd.plan.validation.InvalidObjectException;
import pl.psnc.synat.wrdz.zmkd.plan.validation.InvalidPathException;
import pl.psnc.synat.wrdz.zmkd.plan.validation.NoObjectsException;
import pl.psnc.synat.wrdz.zmkd.plan.validation.NoPathException;
import pl.psnc.synat.wrdz.zu.dto.user.UserDto;
import pl.psnc.synat.wrdz.zu.permission.ObjectPermissionManager;
import pl.psnc.synat.wrdz.zu.types.ObjectPermissionType;
import pl.psnc.synat.wrdz.zu.user.UserBrowser;

/**
 * Migration plan manager bean.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class MigrationPlanManagerBean implements MigrationPlanManager {

    /** Identifier browser. */
    @EJB(name = "IdentifierBrowser")
    private IdentifierBrowser identfierBrowser;

    /** Object checker. */
    @EJB(name = "ObjectChecker")
    private ObjectChecker objectChecker;

    /** Object permission manager. */
    @EJB(name = "ObjectPermissionManager")
    private ObjectPermissionManager objectPermissionManager;

    /** File format dictionary. */
    @EJB
    private FileFormatDictionaryBean formatDictionary;

    /** Service composer. */
    @EJB(name = "ServiceComposer")
    private ServiceComposer serviceComposer;

    /** Transformation converter. */
    @EJB
    private ServiceDescriptionConverter serviceDescriptionConverter;

    /** User browser. */
    @EJB(name = "UserBrowser")
    private UserBrowser userBrowser;

    /** Migration plan DAO. */
    @EJB
    private MigrationPlanDao migrationPlanDao;

    /** Migration path DAO. */
    @EJB
    private MigrationPathDao migrationPathDao;

    /** User context. */
    @EJB
    private UserContext userContext;

    /** JAXB context. */
    private JAXBContext jaxbContext;


    /**
     * Default constructor.
     */
    public MigrationPlanManagerBean() {
        try {
            jaxbContext = JAXBContext.newInstance(pl.psnc.darceo.migration.MigrationPlan.class);
        } catch (JAXBException e) {
            throw new WrdzRuntimeException(e.getMessage(), e);
        }
    }


    @Override
    public MigrationPlan createMigrationPlan(pl.psnc.darceo.migration.MigrationPlan migrationPlan)
            throws InvalidFormatException, InvalidObjectException, InvalidPathException, NoObjectsException,
            NoPathException {

        FileFormat inputFormat;
        FileFormat outputFormat;

        // input/output formats
        try {
            try {
                inputFormat = formatDictionary.findByPuid(migrationPlan.getFileFormats().getInputPUID());
                outputFormat = formatDictionary.findByPuid(migrationPlan.getFileFormats().getOutputPUID());
            } catch (UnrecognizedPuidException e) {
                throw new InvalidFormatException(e.getFormatPuid(), e);
            }
        } catch (UdfrServiceException e) {
            throw new WrdzRuntimeException("Could not connect to UDFR", e);
        }

        // migration paths
        List<TransformationChain> chains = new ArrayList<TransformationChain>();
        if (migrationPlan.getMigrationPath() != null) {
            List<Service> services = new ArrayList<Service>(migrationPlan.getMigrationPath().getService());
            Collections.sort(services, serviceComparator);

            try {
                TransformationChain chain = serviceDescriptionConverter.convertServices(services);
                chain = serviceComposer.verifyServiceChain(chain, inputFormat.getUdfrIri(), outputFormat.getUdfrIri());
                if (chain == null) {
                    throw new InvalidPathException("Invalid migration path");
                }
                chains.add(chain);
            } catch (UnrecognizedPuidException e) {
                throw new InvalidFormatException(e.getFormatPuid(), e);
            } catch (UdfrServiceException e) {
                throw new WrdzRuntimeException("Could not connect to UDFR", e);
            }

        } else {
            if (migrationPlan.getParameters() != null) {
                chains = serviceComposer.composeServiceChain(inputFormat.getUdfrIri(), outputFormat.getUdfrIri(),
                    serviceDescriptionConverter.convertType(migrationPlan.getType()),
                    serviceDescriptionConverter.convertParameters(migrationPlan.getParameters().getParameter()));
            } else {
                chains = serviceComposer.composeServiceChain(inputFormat.getUdfrIri(), outputFormat.getUdfrIri(),
                    serviceDescriptionConverter.convertType(migrationPlan.getType()));
            }
            if (chains.isEmpty()) {
                throw new NoPathException("No migration path found");
            }
        }

        // objects
        DigitalObjectsCondition condition = migrationPlan.getDigitalObjects().getCondition();

        List<Long> objectIds = null;
        if ((condition == DigitalObjectsCondition.ALL_OBJECTS || condition == DigitalObjectsCondition.BY_OWNER)
                && !userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
            objectIds = objectPermissionManager.fetchWithPermission(userContext.getCallerPrincipalName(),
                ObjectPermissionType.METADATA_UPDATE);
        }

        List<String> identifiers;
        switch (condition) {
            case ALL_OBJECTS:
                identifiers = identfierBrowser.getIdentifiersForMigration(inputFormat.getPuid(), null, objectIds);
                break;
            case BY_OWNER:
                String owner = migrationPlan.getDigitalObjects().getOwner();
                if (owner == null) {
                    throw new NoObjectsException("Missing owner");
                }
                identifiers = identfierBrowser.getIdentifiersForMigration(inputFormat.getPuid(), owner, objectIds);
                break;
            case BY_IDENTIFIERS:
                identifiers = migrationPlan.getDigitalObjects().getIdentifier();

                if (!userBrowser.isAdmin(userContext.getCallerPrincipalName())) {
                    for (String identifier : identifiers) {
                        Long objectId = identfierBrowser.getObjectId(identifier);
                        if (objectId == null) {
                            throw new InvalidObjectException("Non-existent object designated for migration", identifier);
                        }
                        if (!objectPermissionManager.hasPermission(userContext.getCallerPrincipalName(), objectId,
                            ObjectPermissionType.METADATA_UPDATE)) {
                            throw new InvalidObjectException("Invalid object designated for migration", identifier);
                        }
                    }
                }

                for (String identifier : identifiers) {
                    if (!objectChecker.containsDataFiles(identifier, inputFormat.getPuid())) {
                        throw new InvalidObjectException("Invalid object designated for migration", identifier);
                    }
                }
                break;
            default:
                throw new WrdzRuntimeException("Unexpected value: " + migrationPlan.getDigitalObjects().getCondition());
        }

        if (identifiers.isEmpty()) {
            throw new NoObjectsException("No objects to migrate");
        }

        // create plan entity
        try {
            String xml = getXml(migrationPlan);
            return createMigrationPlan(inputFormat, outputFormat, identifiers, chains, xml);
        } catch (UnrecognizedIriException e) {
            // this should not happen at this point, IRIs have been validated before (incorrect service description in RU?)
            throw new WrdzRuntimeException("IRI not found", e);
        } catch (UdfrServiceException e) {
            throw new WrdzRuntimeException("Could not connect to UDFR", e);
        }
    }


    /**
     * Returns a String containing the XML representation of the given migration plan.
     * 
     * @param plan
     *            migration plan
     * @return xml representation
     */
    private String getXml(pl.psnc.darceo.migration.MigrationPlan plan) {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(plan, writer);

            return writer.toString();

        } catch (JAXBException e) {
            throw new WrdzRuntimeException("Marshalling failed", e);
        }
    }


    /**
     * Creates a MigrationPlan entity and persists it.
     * 
     * @param inputFormat
     *            migration input format
     * @param outputFormat
     *            migration output format
     * @param objectIdentifiers
     *            identifiers of the objects to be migrated
     * @param chains
     *            transformation chains available for this plan
     * @param xml
     *            XML representation of the created migration plan
     * @return persisted MigrationPlan
     * @throws UnrecognizedIriException
     *             if an IRI used in the transformation chain is invalid
     * @throws UdfrServiceException
     *             if communication with UDFR fails
     */
    private MigrationPlan createMigrationPlan(FileFormat inputFormat, FileFormat outputFormat,
            List<String> objectIdentifiers, List<TransformationChain> chains, String xml)
            throws UnrecognizedIriException, UdfrServiceException {

        MigrationPlan plan = new MigrationPlan();
        plan.setOwnerId(userBrowser.getUserId(userContext.getCallerPrincipalName()));
        plan.setInputFileFormat(inputFormat);
        plan.setOutputFileFormat(outputFormat);
        plan.setStatus(chains.size() == 1 ? MigrationPlanStatus.READY : MigrationPlanStatus.NEW);
        plan.setXmlFile(xml);

        List<MigrationItemLog> items = new ArrayList<MigrationItemLog>();
        for (String identifier : objectIdentifiers) {
            MigrationItemLog item = new MigrationItemLog();
            item.setMigrationPlan(plan);
            item.setObjectIdentifier(identifier);
            item.setStatus(MigrationItemStatus.NOT_YET_STARTED);
            items.add(item);
        }
        plan.setMigrationItems(items);

        List<MigrationPath> paths = serviceDescriptionConverter.convertChains(chains, MigrationPath.class);
        for (MigrationPath path : paths) {
            path.setMigrationPlan(plan);
        }

        migrationPlanDao.persist(plan);
        migrationPlanDao.flush();

        plan.setMigrationPaths(paths);
        if (paths.size() == 1) {
            plan.setActivePath(paths.get(0));
        }

        return plan;
    }


    @Override
    public List<MigrationPlan> getMigrationPlans(MigrationPlanStatus status) {
        MigrationPlanFilterFactory filterFactory = migrationPlanDao.createQueryModifier().getQueryFilterFactory();
        QueryFilter<MigrationPlan> queryFilter = filterFactory.byStatus(status);
        List<MigrationPlan> entities = migrationPlanDao.findBy(queryFilter, false);
        if (entities == null) {
            return new ArrayList<MigrationPlan>();
        }
        return entities;
    }


    @Override
    public List<MigrationPlan> getMigrationPlans() {
        List<MigrationPlan> entities = migrationPlanDao.findAll();
        return entities;
    }


    @Override
    public List<MigrationPlan> getMigrationPlans(String username) {

        UserDto user = userBrowser.getUser(username);
        if (user == null) {
            return new ArrayList<MigrationPlan>();
        }

        MigrationPlanFilterFactory filterFactory = migrationPlanDao.createQueryModifier().getQueryFilterFactory();
        QueryFilter<MigrationPlan> queryFilter = filterFactory.byOwnerId(user.getId());
        List<MigrationPlan> entities = migrationPlanDao.findBy(queryFilter, false);
        return entities;
    }


    @Override
    public MigrationPlan getMigrationPlanById(long migrationPlanId)
            throws MigrationPlanNotFoundException {
        MigrationPlan migrationPlan = migrationPlanDao.findById(migrationPlanId);
        if (migrationPlan == null) {
            throw new MigrationPlanNotFoundException("migration plan not found by specified id: " + migrationPlanId);
        }
        return migrationPlan;
    }


    @Override
    public boolean isStartable(long planId) {
        MigrationPlan plan = migrationPlanDao.findById(planId);
        return (plan.getStatus().equals(MigrationPlanStatus.READY) || plan.getStatus().equals(
            MigrationPlanStatus.PAUSED));
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logStarted(long planId) {
        MigrationPlan plan = migrationPlanDao.findById(planId);
        plan.setStatus(MigrationPlanStatus.RUNNING);
    }


    @Override
    public boolean isPausable(long planId) {
        MigrationPlan plan = migrationPlanDao.findById(planId);
        return plan.getStatus().equals(MigrationPlanStatus.RUNNING);
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logPaused(long planId) {
        MigrationPlan plan = migrationPlanDao.findById(planId);
        plan.setStatus(MigrationPlanStatus.PAUSED);
        migrationPlanDao.merge(plan);
    }


    @Override
    public boolean isFinishable(long planId) {
        MigrationPlan plan = migrationPlanDao.findById(planId);
        return (plan.getStatus().equals(MigrationPlanStatus.RUNNING) || plan.getStatus().equals(
            MigrationPlanStatus.PAUSED));
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logFinished(long planId) {
        MigrationPlan plan = migrationPlanDao.findById(planId);
        plan.setStatus(MigrationPlanStatus.COMPLETED);
        migrationPlanDao.merge(plan);
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void logWaitingForObject(long planId, String objectIdentifier) {
        MigrationPlan plan = migrationPlanDao.findById(planId);
        plan.setObjectIdentifierAwaited(objectIdentifier);
        migrationPlanDao.merge(plan);
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void clearWaitingForObject(long planId) {
        MigrationPlan plan = migrationPlanDao.findById(planId);
        plan.setObjectIdentifierAwaited(null);
        migrationPlanDao.merge(plan);
    }


    @Override
    public List<MigrationPlan> findPlansWaitingForObject(String objectIdentifier) {
        MigrationPlanFilterFactory filterFactory = migrationPlanDao.createQueryModifier().getQueryFilterFactory();
        QueryFilter<MigrationPlan> queryFilter = filterFactory.byObjectIdentifierAwaited(objectIdentifier);
        List<MigrationPlan> plans = migrationPlanDao.findBy(queryFilter, false);
        if (plans == null) {
            return Collections.emptyList();
        }
        return plans;
    }


    @Override
    public void deletePlan(long planId)
            throws MigrationPlanDeletionException {
        MigrationPlan plan = migrationPlanDao.findById(planId);
        if (plan == null) {
            throw new MigrationPlanDeletionException("Migration plan not found!", MessageId.NOT_FOUND);
        }
        MigrationPlanStatus status = plan.getStatus();
        if (status.equals(MigrationPlanStatus.PAUSED) || status.equals(MigrationPlanStatus.RUNNING)) {
            throw new MigrationPlanDeletionException("Unable to delete migration plan in status: " + status,
                    MessageId.STATUS_UNABLE_TO_DELETE);
        }
        migrationPlanDao.delete(plan);
    }


    @Override
    public void setActivePath(long planId, long pathId)
            throws MigrationPlanNotFoundException, MigrationPlanStateException, MigrationPathNotFoundException {

        MigrationPlan plan = migrationPlanDao.findById(planId);
        if (plan == null) {
            throw new MigrationPlanNotFoundException("Migration plan not found");
        }
        if (plan.getStatus() != MigrationPlanStatus.NEW && plan.getStatus() != MigrationPlanStatus.READY) {
            throw new MigrationPlanStateException("Given migration plan is " + plan.getStatus());
        }

        MigrationPath path = migrationPathDao.findById(pathId);
        if (path == null) {
            throw new MigrationPathNotFoundException("Migration path not found");
        }
        if (!path.getMigrationPlan().equals(plan)) {
            throw new MigrationPathNotFoundException("Migration path not found in the given plan");
        }

        plan.setActivePath(path);
        plan.setStatus(MigrationPlanStatus.READY);
    }


    /** Compares services by their no. */
    private final Comparator<Service> serviceComparator = new Comparator<Service>() {

        @Override
        public int compare(Service o1, Service o2) {
            return o1.getNo() - o2.getNo();
        }
    };

}
