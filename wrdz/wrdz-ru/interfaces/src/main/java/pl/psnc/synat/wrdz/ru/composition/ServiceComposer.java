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

import javax.ejb.Remote;

/**
 * Defines the interface of the bean responsible for orchestrating services.
 */
@Remote
public interface ServiceComposer {

    /**
     * Composes a chain of services, that transforms an input format to an output format.
     * 
     * @param inputFormatIri
     *            input format (UDFR IRI)
     * @param outputFormatIri
     *            output format (UDFR IRI)
     * @return service chain
     */
    List<TransformationChain> composeServiceChain(String inputFormatIri, String outputFormatIri);


    /**
     * Composes a chain of services, that transforms an input format to an output format. It takes into account the
     * passed type of transformation.
     * 
     * @param inputFormatIri
     *            input format (UDFR IRI)
     * @param outputFormatIri
     *            output format (UDFR IRI)
     * @param transformationType
     *            type of transformation
     * @return service chain
     */
    List<TransformationChain> composeServiceChain(String inputFormatIri, String outputFormatIri,
            TransformationType transformationType);


    /**
     * Composes a chain of services, that transforms an input format to an output format. It takes into account the
     * passed parameters of conversion.
     * 
     * @param inputFormatIri
     *            input format (UDFR IRI)
     * @param outputFormatIri
     *            output format (UDFR IRI)
     * @param parameters
     *            list of parameters - all their types must be utilized by service chain
     * @return service chain
     */
    List<TransformationChain> composeServiceChain(String inputFormatIri, String outputFormatIri,
            List<ServiceParam> parameters);


    /**
     * Composes a chain of services, that transforms an input format to an output format. It takes into account the
     * passed type of transformation and parameters of conversion.
     * 
     * @param inputFormatIri
     *            input format (UDFR IRI)
     * @param outputFormatIri
     *            output format (UDFR IRI)
     * @param transformationType
     *            type of transformation
     * @param parameters
     *            list of parameters - all their types must be utilized by service chain
     * @return service chain
     */
    List<TransformationChain> composeServiceChain(String inputFormatIri, String outputFormatIri,
            TransformationType transformationType, List<ServiceParam> parameters);


    /**
     * Checks whether the given transformation chain is valid.
     * 
     * A transformation chain is considered valid if all of the services used in it exist and together (in their
     * specified sequence) allow the input format to be transformed into the output format.
     * 
     * If the chain is valid, an updated, complete version is returned (containing both service IRIs and names if only
     * one of those were provided, for example). Otherwise this method returns <code>null</code>.
     * 
     * @param chain
     *            transformation chain that requires verification
     * @param inputFormatIri
     *            UDFR IRI of the format of the given chain's input file
     * @param outputFormatIri
     *            UDFR IRI of the format of the given chain's output file
     * @return the complete chain if it is valid; <code>null</code> otherwise
     */
    TransformationChain verifyServiceChain(TransformationChain chain, String inputFormatIri, String outputFormatIri);


    /**
     * Finds all advanced delivery services.
     * 
     * @return list of all advanced delivery services
     */
    List<AdvancedDelivery> findAllAdvancedDeliveryServices();


    /**
     * Finds all advanced delivery services that can deliver all passed file formats.
     * 
     * @param formatIris
     *            list of formats that services should be able to deliver
     * @return list of advanced delivery services
     */
    List<AdvancedDelivery> findAllAdvancedDeliveryServices(List<String> formatIris);

}
