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

import java.util.List;

import javax.enterprise.inject.Default;
import javax.inject.Singleton;

import pl.psnc.synat.wrdz.ru.composition.TransformationType;
import pl.psnc.synat.wrdz.zmkd.entity.plan.ServiceOutcome;
import pl.psnc.synat.wrdz.zmkd.entity.plan.ServiceParameter;

/**
 * Helper methods for migration plans functionality.
 */
@Singleton
@Default
public class MigrationPlanHelper {

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
     * @return the type of transformation or null.
     */
    public TransformationType getOriginTransformationType(List<ServiceParameter> parameters,
            List<ServiceOutcome> outcomes) {
        String mainParam = getFileParameterType(parameters);
        String mainOutcome = getFileOutcomeType(outcomes);
        if (mainParam == null || mainOutcome == null) {
            return null;
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
        return null;
    }


    /**
     * Finds and return the main parameter type among the list of parameters - file or file bundle.
     * 
     * @param parameters
     *            list of parameters
     * @return the main parameter
     */
    private String getFileParameterType(List<ServiceParameter> parameters) {
        String ret = null;
        for (ServiceParameter param : parameters) {
            if (FILEBUNDLE_IRI.equals(param.getType())) {
                return param.getType();
            } else if (FILE_IRI.equals(param.getType())) {
                ret = param.getType();
            }
        }
        return ret;
    }


    /**
     * Finds and return the main outcomes type among the list of outcomes - file or file bundle.
     * 
     * @param outcomes
     *            list of outcomes
     * @return the main outcomes
     */
    private String getFileOutcomeType(List<ServiceOutcome> outcomes) {
        for (ServiceOutcome outcome : outcomes) {
            if (FILEBUNDLE_IRI.equals(outcome.getType()) || FILE_IRI.equals(outcome.getType())) {
                return outcome.getName();
            }
        }
        return null;
    }

}
