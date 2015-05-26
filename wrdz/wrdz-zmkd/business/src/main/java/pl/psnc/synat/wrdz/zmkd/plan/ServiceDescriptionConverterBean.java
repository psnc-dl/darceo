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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import pl.psnc.darceo.migration.MigrationParameter;
import pl.psnc.darceo.migration.MigrationType;
import pl.psnc.darceo.migration.Service;
import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.ru.composition.AbstractService;
import pl.psnc.synat.wrdz.ru.composition.AdvancedDelivery;
import pl.psnc.synat.wrdz.ru.composition.ServiceParam;
import pl.psnc.synat.wrdz.ru.composition.TransformationChain;
import pl.psnc.synat.wrdz.ru.composition.TransformationType;
import pl.psnc.synat.wrdz.zmkd.ddr.ClientCapabilities;
import pl.psnc.synat.wrdz.zmkd.entity.format.FileFormat;
import pl.psnc.synat.wrdz.zmkd.entity.plan.Delivery;
import pl.psnc.synat.wrdz.zmkd.entity.plan.ServiceOutcome;
import pl.psnc.synat.wrdz.zmkd.entity.plan.ServiceParameter;
import pl.psnc.synat.wrdz.zmkd.entity.plan.Transformation;
import pl.psnc.synat.wrdz.zmkd.entity.plan.TransformationPath;
import pl.psnc.synat.wrdz.zmkd.format.FileFormatDictionaryBean;
import pl.psnc.synat.wrdz.zmkd.format.UdfrServiceException;
import pl.psnc.synat.wrdz.zmkd.format.UnrecognizedIriException;
import pl.psnc.synat.wrdz.zmkd.format.UnrecognizedPuidException;

/**
 * Default implementation of {@link ServiceDescriptionConverter}.
 */
@Stateless
public class ServiceDescriptionConverterBean implements ServiceDescriptionConverter {

    /** File format dictionary. */
    @EJB
    private FileFormatDictionaryBean formatDictionary;


    @Override
    public <T extends TransformationPath> List<T> convertChains(List<TransformationChain> chains, Class<T> clazz)
            throws UnrecognizedIriException, UdfrServiceException {

        List<T> paths = new ArrayList<T>();
        for (pl.psnc.synat.wrdz.ru.composition.TransformationChain chain : chains) {
            paths.add(convertChain(chain, clazz));
        }

        return paths;
    }


    @Override
    public <T extends TransformationPath> T convertChain(TransformationChain chain, Class<T> clazz)
            throws UnrecognizedIriException, UdfrServiceException {
        T path;
        try {
            path = clazz.newInstance();
        } catch (IllegalAccessException e) {
            throw new WrdzRuntimeException(e.getMessage(), e);
        } catch (InstantiationException e) {
            throw new WrdzRuntimeException(e.getMessage(), e);
        }

        path.setExecutionCost(chain.getExecutionCost());

        List<Transformation> transformations = new ArrayList<Transformation>();
        for (pl.psnc.synat.wrdz.ru.composition.Transformation chainTransformation : chain.getTransformations()) {
            Transformation transformation = new Transformation();
            transformation.setTransformationPath(path);
            transformation.setInputFileFormat(formatDictionary.findByIri(chainTransformation.getInputFormatIri()));
            transformation.setOutputFileFormat(formatDictionary.findByIri(chainTransformation.getOutputFormatIri()));
            fillServiceDetails(transformation, chainTransformation);
            transformation.setType(chainTransformation.getType());
            transformation.setClassification(chainTransformation.getClassification());
            transformations.add(transformation);
        }
        path.setTransformations(transformations);

        return path;
    }


    @Override
    public Delivery convertDelivery(AdvancedDelivery service, ClientCapabilities capabilities)
            throws UnrecognizedIriException, UdfrServiceException {
        Delivery delivery = new Delivery();
        delivery.setExecutionCost(service.getExecutionCost());
        List<FileFormat> inputFileFormats = new ArrayList<FileFormat>();
        for (String inputIri : service.getInputFormatIris()) {
            inputFileFormats.add(formatDictionary.findByIri(inputIri));
        }
        delivery.setInputFileFormats(inputFileFormats);
        fillServiceDetails(delivery, service);
        completeClientCapabilities(delivery, capabilities);
        return delivery;
    }


    /**
     * Fills common part of details describing the service.
     * 
     * @param targetService
     *            target service - ZMKD description of a service
     * @param sourceService
     *            source service - RU description of a service
     */
    private void fillServiceDetails(pl.psnc.synat.wrdz.zmkd.entity.plan.Service targetService,
            AbstractService sourceService) {
        targetService.setOntologyIri(sourceService.getOntologyIri());
        targetService.setServiceIri(sourceService.getServiceIri());
        targetService.setServiceName(sourceService.getServiceName());
        if (sourceService.getParameters() != null) {
            List<ServiceParameter> parameters = new ArrayList<ServiceParameter>();
            for (ServiceParam servParam : sourceService.getParameters()) {
                ServiceParameter parameter = new ServiceParameter();
                parameter.setService(targetService);
                parameter.setName(servParam.getName());
                parameter.setType(servParam.getType());
                parameter.setBundleType(servParam.getBundleType());
                parameter.setValue(servParam.getValue());
                parameters.add(parameter);
            }
            targetService.setParameters(parameters);
        }
        if (sourceService.getOutcomes() != null) {
            List<ServiceOutcome> outcomes = new ArrayList<ServiceOutcome>();
            for (pl.psnc.synat.wrdz.ru.composition.ServiceOutcome servOutcome : sourceService.getOutcomes()) {
                ServiceOutcome outcome = new ServiceOutcome();
                outcome.setService(targetService);
                outcome.setName(servOutcome.getName());
                outcome.setType(servOutcome.getType());
                outcome.setBundleType(servOutcome.getBundleType());
                outcomes.add(outcome);
            }
            targetService.setOutcomes(outcomes);
        }
    }


    /**
     * Complete the values of delivery parameters according to client capabilities.
     * 
     * @param delivery
     *            delivery service
     * @param capabilities
     *            client capabilities
     */
    private void completeClientCapabilities(Delivery delivery, ClientCapabilities capabilities) {
        for (ServiceParameter servParam : delivery.getParameters()) {
            if (servParam.getValue() == null) {
                if (servParam.getType().equals("http://darceo.psnc.pl/ontologies/dArceoImage.owl#WidthPx")
                        && capabilities.getDisplayWidth() != null) {
                    servParam.setValue(capabilities.getDisplayWidth().toString());
                }
                if (servParam.getType().equals("http://darceo.psnc.pl/ontologies/dArceoImage.owl#HeightPx")
                        && capabilities.getDisplayHeight() != null) {
                    servParam.setValue(capabilities.getDisplayHeight().toString());
                }
            }
        }
    }


    @Override
    public TransformationChain convertServices(List<Service> services)
            throws UnrecognizedPuidException, UdfrServiceException {
        TransformationChain chain = new TransformationChain();

        for (Service service : services) {
            pl.psnc.synat.wrdz.ru.composition.Transformation transformation = new pl.psnc.synat.wrdz.ru.composition.Transformation();
            transformation.setInputFormatIri(formatDictionary.getUdfrIri(service.getInputPUID()));
            transformation.setOutputFormatIri(formatDictionary.getUdfrIri(service.getOutputPUID()));
            transformation.setOntologyIri(service.getOntologyIRI());
            transformation.setServiceIri(service.getServiceIRI());
            transformation.setServiceName(service.getServiceName());
            transformation.setType(convertType(service.getType()));

            if (service.getParameters() != null) {
                transformation.setParameters(convertParameters(service.getParameters().getParameter()));
            }

            chain.addTransformation(transformation, null);
        }

        return chain;
    }


    @Override
    public List<ServiceParam> convertParameters(List<MigrationParameter> parameters) {
        List<ServiceParam> result = new ArrayList<ServiceParam>();
        for (MigrationParameter parameter : parameters) {
            result.add(new ServiceParam(parameter.getName(), parameter.getType(), null, parameter.getValue()));
        }
        return result;
    }


    /**
     * Converts MigrationType values to their respective TransformationType equivalents.
     * 
     * @param type
     *            MigrationType value
     * @return TransformationType value
     */
    @Override
    public TransformationType convertType(MigrationType type) {

        if (type == null) {
            return null;
        }
        switch (type) {
            case ONE_TO_ONE:
                return TransformationType.ONE_TO_ONE;
            case ONE_TO_MANY:
                return TransformationType.ONE_TO_MANY;
            case MANY_TO_ONE:
                return TransformationType.MANY_TO_ONE;
            default:
                throw new WrdzRuntimeException("Unexpected value: " + type);
        }
    }

}
