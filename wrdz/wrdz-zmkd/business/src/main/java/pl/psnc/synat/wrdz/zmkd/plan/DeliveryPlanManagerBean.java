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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.ru.composition.AdvancedDelivery;
import pl.psnc.synat.wrdz.ru.composition.ServiceComposer;
import pl.psnc.synat.wrdz.ru.composition.Transformation;
import pl.psnc.synat.wrdz.ru.composition.TransformationChain;
import pl.psnc.synat.wrdz.zmd.format.DataFileFormatBrowser;
import pl.psnc.synat.wrdz.zmkd.config.Format;
import pl.psnc.synat.wrdz.zmkd.dao.plan.DeliveryPlanDao;
import pl.psnc.synat.wrdz.zmkd.ddr.ClientCapabilities;
import pl.psnc.synat.wrdz.zmkd.entity.plan.ConversionPath;
import pl.psnc.synat.wrdz.zmkd.entity.plan.Delivery;
import pl.psnc.synat.wrdz.zmkd.entity.plan.DeliveryPlan;
import pl.psnc.synat.wrdz.zmkd.entity.plan.DeliveryPlanStatus;
import pl.psnc.synat.wrdz.zmkd.format.FileFormatDictionaryBean;
import pl.psnc.synat.wrdz.zmkd.format.UdfrServiceException;
import pl.psnc.synat.wrdz.zmkd.format.UnrecognizedIriException;
import pl.psnc.synat.wrdz.zmkd.format.UnrecognizedPuidException;

/**
 * Default implementation of {@link DeliveryPlanManager}.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DeliveryPlanManagerBean implements DeliveryPlanManager {

    /** Data file format browser. */
    @EJB(name = "DataFileFormatBrowser")
    private DataFileFormatBrowser formatBrowser;

    /** Delivery plan DAO. */
    @EJB
    private DeliveryPlanDao planDao;

    /** Service composer. */
    @EJB(name = "ServiceComposer")
    private ServiceComposer serviceComposer;

    /** File format dictionary. */
    @EJB
    private FileFormatDictionaryBean formatDictionary;

    /** Service converter. */
    @EJB
    private ServiceDescriptionConverter serviceDescriptionConverter;


    @Override
    public List<DeliveryPlan> generateDeliveryPlans(ClientCapabilities capabilities, String objectIdentifier) {

        Set<String> objectFormats = new HashSet<String>();
        for (String puid : formatBrowser.getFormatPuids(objectIdentifier)) {
            try {
                objectFormats.add(formatDictionary.getUdfrIri(puid));
            } catch (UnrecognizedPuidException e) {
                throw new WrdzRuntimeException("Unrecognized PUID: " + puid, e);
            } catch (UdfrServiceException e) {
                throw new WrdzRuntimeException("Could not connect to UDFR", e);
            }
        }

        Set<String> clientFormats = new HashSet<String>();
        for (Format format : capabilities.getAudioFormatSupport()) {
            clientFormats.addAll(format.getIris());
        }
        for (Format format : capabilities.getDocFormatSupport()) {
            clientFormats.addAll(format.getIris());
        }
        for (Format format : capabilities.getImageFormatSupport()) {
            clientFormats.addAll(format.getIris());
        }
        for (Format format : capabilities.getVideoFormatSupport()) {
            clientFormats.addAll(format.getIris());
        }

        List<DeliveryPlan> plans = new ArrayList<DeliveryPlan>();

        List<AdvancedDelivery> deliveryServices = serviceComposer.findAllAdvancedDeliveryServices();
        serviceLoop: for (AdvancedDelivery service : deliveryServices) {

            Set<String> supportedFormats = new HashSet<String>(service.getInputFormatIris());
            supportedFormats.retainAll(clientFormats);

            if (!supportedFormats.isEmpty()) {

                DeliveryPlan plan = new DeliveryPlan();

                for (String objectFormat : objectFormats) {
                    if (!supportedFormats.contains(objectFormat)) {
                        ConversionPath conversion = null;
                        for (String supportedFormat : supportedFormats) {
                            try {
                                List<TransformationChain> chains = serviceComposer.composeServiceChain(objectFormat,
                                    supportedFormat);

                                List<TransformationChain> chainsToRemove = new ArrayList<TransformationChain>();
                                for (TransformationChain chain : chains) {
                                    for (Transformation transformation : chain.getTransformations()) {
                                        if (transformation.getParameters().size() > 1) {
                                            chainsToRemove.add(chain);
                                            break;
                                        }
                                    }
                                }
                                chains.removeAll(chainsToRemove);

                                if (!chains.isEmpty()) {
                                    conversion = serviceDescriptionConverter.convertChain(chains.get(0),
                                        ConversionPath.class);
                                    break;
                                }
                            } catch (UnrecognizedIriException e) {
                                throw new WrdzRuntimeException("Unrecognized IRI", e);
                            } catch (UdfrServiceException e) {
                                throw new WrdzRuntimeException("Could not connect to UDFR", e);
                            }
                        }
                        if (conversion != null) {
                            conversion.setDeliveryPlan(plan);
                            plan.getConversionPaths().add(conversion);
                        } else {
                            continue serviceLoop;
                        }
                    }
                }

                Delivery delivery;
                try {
                    delivery = serviceDescriptionConverter.convertDelivery(service, capabilities);
                } catch (UnrecognizedIriException e) {
                    throw new WrdzRuntimeException("Unrecognized IRI", e);
                } catch (UdfrServiceException e) {
                    throw new WrdzRuntimeException("Could not connect to UDFR", e);
                }
                plan.setDelivery(delivery);
                plan.setObjectIdentifier(objectIdentifier);

                plan.setStatus(DeliveryPlanStatus.NEW);

                plans.add(plan);
            }
        }

        return plans;
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public String saveDeliveryPlan(DeliveryPlan plan) {

        plan.setStatus(DeliveryPlanStatus.NEW);
        planDao.persist(plan);
        planDao.flush();

        return plan.getId();
    }


    @Override
    public DeliveryPlan getDeliveryPlan(String planId) {
        return planDao.findById(planId);
    }


    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void changeStatus(String planId, DeliveryPlanStatus status) {
        DeliveryPlan plan = planDao.findById(planId);
        plan.setStatus(status);
    }


    @Override
    public void completePlan(String planId, String clientLocation) {
        DeliveryPlan plan = planDao.findById(planId);
        plan.getDelivery().setClientLocation(clientLocation);
        plan.setStatus(DeliveryPlanStatus.COMPLETED);
    }

}
