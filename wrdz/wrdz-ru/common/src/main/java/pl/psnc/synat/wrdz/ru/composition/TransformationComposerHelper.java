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

import java.util.List;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;

import pl.psnc.synat.wrdz.ru.entity.types.ServiceType;

/**
 * Provides helper methods for the service composition functionality.
 */
@Singleton
@Default
public class TransformationComposerHelper {

    /**
     * IRI of the type of parameter denoting the file.
     */
    private static final String FILE_IRI = "http://darceo.psnc.pl/ontologies/dArceoFile.owl#File";

    /**
     * IRI of the type of parameter denoting the file bundle.
     */
    private static final String FILEBUNDLE_IRI = "http://darceo.psnc.pl/ontologies/dArceoFile.owl#FileBundle";


    /**
     * Figures out the type of transformation by the types of parameters and outcomes of the service.
     * 
     * @param parameters
     *            types of service parameters
     * @param outcomes
     *            types of service outcomes
     * @return the type of the transformation
     * @throws UnknownTransformationTypeException
     *             when type is unrecognized
     */
    public TransformationType getTransformationType(List<ServiceParam> parameters, List<ServiceOutcome> outcomes)
            throws UnknownTransformationTypeException {
        String mainParam = getFileParameterType(parameters);
        String mainOutcome = getFileOutcome(outcomes);
        if (mainParam == null || mainOutcome == null) {
            throw new UnknownTransformationTypeException("There are missing file type among parametrs " + mainParam
                    + " or outcomes " + mainOutcome + ".");
        }
        if (mainParam.equals(FILE_IRI) && mainOutcome.equals(FILE_IRI)) {
            return TransformationType.ONE_TO_ONE;
        }
        if (mainParam.equals(FILE_IRI) && mainOutcome.equals(FILEBUNDLE_IRI)) {
            return TransformationType.ONE_TO_MANY;
        }
        if (mainParam.equals(FILEBUNDLE_IRI) && mainOutcome.equals(FILE_IRI)) {
            return TransformationType.MANY_TO_ONE;
        }
        throw new UnknownTransformationTypeException("Many to many type is out of the scope of this system.");
    }


    /**
     * Checks and corrects the type of transformation in chain in order to this chain has the correct type. In case of
     * unknown type (null) - the computed type of transformation is preserved.
     * 
     * @param chain
     *            chain of transformations
     * @param type
     *            target type of chain
     * @return whether the type of chain is correct
     */
    public boolean checkTransformationTypeOfChains(TransformationChain chain, TransformationType type) {
        if (type == null) {
            return true;
        }
        boolean ret = false;
        for (Transformation transformation : chain.getTransformations()) {
            if (TransformationType.ONE_TO_ONE.equals(type)) {
                transformation.setType(type);
                ret = true;
            } else if (TransformationType.ONE_TO_MANY.equals(type)) {
                if (transformation.getType().equals(TransformationType.MANY_TO_ONE)) {
                    transformation.setType(TransformationType.ONE_TO_ONE);
                } else if (transformation.getType().equals(TransformationType.ONE_TO_MANY)) {
                    ret = true;
                }
            } else if (TransformationType.MANY_TO_ONE.equals(type)) {
                if (transformation.getType().equals(TransformationType.ONE_TO_MANY)) {
                    transformation.setType(TransformationType.ONE_TO_ONE);
                } else if (transformation.getType().equals(TransformationType.MANY_TO_ONE)) {
                    ret = true;
                }
            }
        }
        return ret;
    }


    /**
     * Figures out the classification of transformation by the type of the service.
     * 
     * @param serviceType
     *            service type
     * @return the classification of the transformation or null
     */
    public TransformationClassification getTransformationClassification(ServiceType serviceType) {
        switch (serviceType) {
            case DATA_MIGRATION:
                return TransformationClassification.MIGRATION;
            case DATA_CONVERSION:
                return TransformationClassification.CONVERSION;
            default:
                return null;
        }
    }


    /**
     * Finds and returns the main parameter among the set - file or file bundle.
     * 
     * @param parameters
     *            list of parameters
     * @return the main parameter
     */
    private String getFileParameterType(List<ServiceParam> parameters) {
        String ret = null;
        for (ServiceParam param : parameters) {
            if (FILEBUNDLE_IRI.equals(param.getType())) {
                return param.getType();
            } else if (FILE_IRI.equals(param.getType())) {
                ret = param.getType();
            }
        }
        return ret;
    }


    /**
     * Finds and return the main outcomes among the set - file or file bundle.
     * 
     * @param outcomes
     *            set of outcomes
     * @return the main outcomes
     */
    private String getFileOutcome(List<ServiceOutcome> outcomes) {
        for (ServiceOutcome outcome : outcomes) {
            if (FILEBUNDLE_IRI.equals(outcome.getType()) || FILE_IRI.equals(outcome.getType())) {
                return outcome.getType();
            }
        }
        return null;
    }

}
