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
package pl.psnc.synat.wrdz.ru.composition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.sra.exception.SemanticRepositoryResourceException;
import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.ru.dao.services.DataManipulationServiceDao;
import pl.psnc.synat.wrdz.ru.dao.services.DataManipulationServiceFilterFactory;
import pl.psnc.synat.wrdz.ru.entity.services.DataManipulationService;
import pl.psnc.synat.wrdz.ru.owl.SemanticRepositoryAccess;

/**
 * Bean responsible for orchestrating services.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class ServiceComposerBean implements ServiceComposer {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ServiceComposerBean.class);

    /**
     * Provides an access to a semantic repository.
     */
    @EJB
    private SemanticRepositoryAccess semanticRepositoryAccessBean;

    /**
     * Data manipulation service DAO for persistence operations.
     */
    @EJB
    private DataManipulationServiceDao dataManipulationServiceDaoBean;

    /**
     * Helper method for the composition of transformations.
     */
    @Inject
    private TransformationComposerHelper transformationComposerHelper;


    @Override
    public List<TransformationChain> composeServiceChain(String inputFormatIri, String outputFormatIri) {
        return composeServiceChain(inputFormatIri, outputFormatIri, null, new ArrayList<ServiceParam>());
    }


    @Override
    public List<TransformationChain> composeServiceChain(String inputFormatIri, String outputFormatIri,
            TransformationType transformationType) {
        return composeServiceChain(inputFormatIri, outputFormatIri, transformationType, new ArrayList<ServiceParam>());
    }


    @Override
    public List<TransformationChain> composeServiceChain(String inputFormatIri, String outputFormatIri,
            List<ServiceParam> parameters) {
        return composeServiceChain(inputFormatIri, outputFormatIri, null, parameters);
    }


    @Override
    public List<TransformationChain> composeServiceChain(String inputFormatIri, String outputFormatIri,
            TransformationType transformationType, List<ServiceParam> parameters) {
        List<TransformationChain> chains = null;
        try {
            chains = semanticRepositoryAccessBean.getTransfromationChainsByFormats(inputFormatIri, outputFormatIri);
        } catch (SemanticRepositoryResourceException e) {
            throw new WrdzRuntimeException("Error while querying the semantic repository.", e);
        }
        List<TransformationChain> chainsToRemove = new ArrayList<TransformationChain>();
        List<ServiceParam> transformationParameters = new ArrayList<ServiceParam>();
        for (TransformationChain chain : chains) {
            transformationParameters.clear();
            transformationParameters.addAll(parameters);
            for (Transformation transformation : chain.getTransformations()) {
                for (ServiceParam param : parameters) {
                    for (ServiceParam transformationParam : transformation.getParameters()) {
                        if (param.getType().equals(transformationParam.getType())) {
                            transformationParam.setValue(param.getValue());
                            transformationParameters.remove(param);
                            break;
                        }
                    }
                }
            }
            if (!transformationParameters.isEmpty()) {
                chainsToRemove.add(chain);
            }
        }
        chains.removeAll(chainsToRemove);
        chainsToRemove.clear();
        for (TransformationChain chain : chains) {
            for (Transformation transformation : chain.getTransformations()) {
                try {
                    transformation.setType(transformationComposerHelper.getTransformationType(
                        transformation.getParameters(), transformation.getOutcomes()));
                    DataManipulationService dataManipulationService = fillServiceWithDetails(transformation);
                    transformation.setClassification(transformationComposerHelper
                            .getTransformationClassification(dataManipulationService.getType()));
                } catch (UnknownTransformationTypeException e) {
                    logger.warn(
                        "Unrecognized type of transformation for the service: " + transformation.getServiceIri(), e);
                    chainsToRemove.add(chain);
                    break;
                }
            }
            if (!transformationComposerHelper.checkTransformationTypeOfChains(chain, transformationType)) {
                chainsToRemove.add(chain);
            }
        }
        chains.removeAll(chainsToRemove);
        Collections.sort(chains, new TransformationChainComparator());
        return chains;
    }


    @Override
    public TransformationChain verifyServiceChain(TransformationChain chain, String inputFormatIri,
            String outputFormatIri) {

        DataManipulationServiceFilterFactory factory = dataManipulationServiceDaoBean.createQueryModifier()
                .getQueryFilterFactory();

        // check service existence
        for (Transformation transformation : chain.getTransformations()) {
            QueryFilter<DataManipulationService> filter;
            if (transformation.getServiceIri() != null) {
                filter = factory.byIri(transformation.getServiceIri());
            } else {
                filter = factory.byName(transformation.getServiceName());
            }

            DataManipulationService service = dataManipulationServiceDaoBean.findFirstResultBy(factory.and(filter,
                factory.byContext(transformation.getOntologyIri())));

            if (service == null) {
                // service does not exist
                return null;
            }

            transformation.setServiceIri(service.getIri());
            transformation.setServiceName(service.getName());
        }

        try {
            if (semanticRepositoryAccessBean.verifyServiceChainParameters(chain, inputFormatIri, outputFormatIri)) {
                return chain;
            } else {
                return null;
            }
        } catch (SemanticRepositoryResourceException e) {
            throw new WrdzRuntimeException("Error while querying the semantic repository.", e);
        }
    }


    @Override
    public List<AdvancedDelivery> findAllAdvancedDeliveryServices() {
        List<AdvancedDelivery> advancedDeliveryServices = null;
        try {
            advancedDeliveryServices = semanticRepositoryAccessBean.getAdvancedDeliverySerices();
        } catch (SemanticRepositoryResourceException e) {
            throw new WrdzRuntimeException("Error while querying the semantic repository.", e);
        }
        for (AdvancedDelivery advancedDelivery : advancedDeliveryServices) {
            fillServiceWithDetails(advancedDelivery);
        }
        return advancedDeliveryServices;
    }


    @Override
    public List<AdvancedDelivery> findAllAdvancedDeliveryServices(List<String> formatIris) {
        if (formatIris == null || formatIris.isEmpty()) {
            return findAllAdvancedDeliveryServices();
        } else {
            List<AdvancedDelivery> advancedDeliveryServices = null;
            try {
                advancedDeliveryServices = semanticRepositoryAccessBean.getAdvancedDeliverySerices();
            } catch (SemanticRepositoryResourceException e) {
                throw new WrdzRuntimeException("Error while querying the semantic repository.", e);
            }
            List<AdvancedDelivery> servicesToRemove = new ArrayList<AdvancedDelivery>();
            for (AdvancedDelivery advancedDelivery : advancedDeliveryServices) {
                if (advancedDelivery.getInputFormatIris().containsAll(formatIris)) {
                    fillServiceWithDetails(advancedDelivery);
                } else {
                    servicesToRemove.remove(advancedDelivery);
                }
            }
            advancedDeliveryServices.removeAll(servicesToRemove);
            return advancedDeliveryServices;
        }
    }


    /**
     * Fill the service with details.
     * 
     * @param service
     *            service to fill
     * @return service entity from the details come.
     */
    private DataManipulationService fillServiceWithDetails(AbstractService service) {
        DataManipulationServiceFilterFactory filterFactory = dataManipulationServiceDaoBean.createQueryModifier()
                .getQueryFilterFactory();
        DataManipulationService dataManipulationService = dataManipulationServiceDaoBean
                .findSingleResultBy(filterFactory.byIri(service.getServiceIri()));
        service.setDescriptorId(dataManipulationService.getSemanticDescriptor().getId());
        service.setOntologyIri(dataManipulationService.getSemanticDescriptor().getContext());
        service.setServiceId(dataManipulationService.getId());
        service.setServiceName(dataManipulationService.getName());
        return dataManipulationService;
    }

}
