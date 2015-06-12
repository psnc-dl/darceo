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

import javax.ejb.Local;

import pl.psnc.darceo.migration.MigrationParameter;
import pl.psnc.darceo.migration.MigrationType;
import pl.psnc.darceo.migration.Service;
import pl.psnc.synat.wrdz.ru.composition.AdvancedDelivery;
import pl.psnc.synat.wrdz.ru.composition.ServiceParam;
import pl.psnc.synat.wrdz.ru.composition.TransformationChain;
import pl.psnc.synat.wrdz.ru.composition.TransformationType;
import pl.psnc.synat.wrdz.zmkd.ddr.ClientCapabilities;
import pl.psnc.synat.wrdz.zmkd.entity.plan.Delivery;
import pl.psnc.synat.wrdz.zmkd.entity.plan.TransformationPath;
import pl.psnc.synat.wrdz.zmkd.format.UdfrServiceException;
import pl.psnc.synat.wrdz.zmkd.format.UnrecognizedIriException;
import pl.psnc.synat.wrdz.zmkd.format.UnrecognizedPuidException;

/**
 * Converts transformations and deliveries between their RU and ZMKD (both entity and XML) representations.
 * <p>
 * Since both representations use the same class names, converting between them requires fully qualified names, which is
 * tedious. This interface was introduced to contain all that ugly code in one place.
 */
@Local
public interface ServiceDescriptionConverter {

    /**
     * Converts RU TransformationChains to ZMKD TransformationPaths.
     * 
     * @param chains
     *            transformation chains to convert
     * @param clazz
     *            which TransformationPath subclass to map to
     * @param <T>
     *            TransformationPath class
     * @return a list of transformation paths
     * @throws UnrecognizedIriException
     *             if a format IRI specified in one of the chains could not be found in UDFR
     * @throws UdfrServiceException
     *             if there was a problem connecting to UDFR
     */
    <T extends TransformationPath> List<T> convertChains(List<TransformationChain> chains, Class<T> clazz)
            throws UnrecognizedIriException, UdfrServiceException;


    /**
     * Converts a single RU TransformationChain to ZMKD TransformationChain.
     * 
     * @param chain
     *            transformation chain to convert
     * @param clazz
     *            which TransformationPath subclass to map to
     * @param <T>
     *            TransformationPath class
     * @return transformation path
     * @throws UnrecognizedIriException
     *             if a format IRI specified in one of the chains could not be found in UDFR
     * @throws UdfrServiceException
     *             if there was a problem connecting to UDFR
     */
    <T extends TransformationPath> T convertChain(TransformationChain chain, Class<T> clazz)
            throws UnrecognizedIriException, UdfrServiceException;


    /**
     * Converts a single RU AdvancedDelivery service to ZMKD Delivery service.
     * 
     * @param service
     *            advanced delivery service to convert
     * @param capabilities
     *            client capabilities to take into account as parameters
     * @return delivery service
     * @throws UnrecognizedIriException
     *             if a format IRI specified in advanced delivery service could not be found in UDFR
     * @throws UdfrServiceException
     *             if there was a problem connecting to UDFR
     */
    Delivery convertDelivery(AdvancedDelivery service, ClientCapabilities capabilities)
            throws UnrecognizedIriException, UdfrServiceException;


    /**
     * Converts a list of services to an RU TransformationChain.
     * 
     * @param services
     *            a list of services to convert
     * @return transformation chain
     * @throws UnrecognizedPuidException
     *             if a format PUID specified in one of the services could not be found in UDFR
     * @throws UdfrServiceException
     *             if there was a problem connecting to UDFR
     */
    TransformationChain convertServices(List<Service> services)
            throws UnrecognizedPuidException, UdfrServiceException;


    /**
     * Convert a list of migration parameters to a list of RU TransformationParams.
     * 
     * @param parameters
     *            parameters to convert
     * @return a list of transformation params
     */
    List<ServiceParam> convertParameters(List<MigrationParameter> parameters);


    /**
     * Converts a ZMKD XML MigrationType to RU TransformationType.
     * 
     * @param type
     *            migration type to convert
     * @return transformation type
     */
    TransformationType convertType(MigrationType type);

}
