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
package pl.psnc.synat.wrdz.common.metadata.tech;

import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.metadata.tech.rmi.FitsTechMetadataExtractor;
import pl.psnc.synat.wrdz.common.metadata.tech.rmi.FitsTechMetadataExtractorConsts;

/**
 * Technical matadata extractor. It uses FITS by RMI.
 */
public class TechMetadataExtractor {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(TechMetadataExtractor.class);


    /**
     * Extracts technical metadata of the file specified by the path.
     * 
     * @param path
     *            local path to the file from which metadata have to be extracted
     * 
     * @return extracted metadata
     * @throws TechMetadataExtractionException
     *             when some problem with extraction occurs
     */
    public ExtractedMetadata extract(String path)
            throws TechMetadataExtractionException {
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry("localhost", FitsTechMetadataExtractorConsts.RMI_REGISTRY_PORT);
        } catch (RemoteException e) {
            logger.error("Error getting RMI registry", e);
            throw new TechMetadataExtractionException(e);
        }
        FitsTechMetadataExtractor extractor;
        try {
            extractor = (FitsTechMetadataExtractor) registry
                    .lookup(FitsTechMetadataExtractorConsts.FITS_RMI_SERVICE_NAME);
        } catch (AccessException e) {
            logger.error("Error accessing FITS service in the RMI registry", e);
            throw new TechMetadataExtractionException(e);
        } catch (RemoteException e) {
            logger.error("Error getting FITS service in the RMI registry", e);
            throw new TechMetadataExtractionException(e);
        } catch (NotBoundException e) {
            logger.error("Error getting FITS service in the RMI registry", e);
            throw new TechMetadataExtractionException(e);
        }
        try {
            ExtractedMetadata result = extractor.extractTechMetadata(path);
            return result;
        } catch (RemoteException e) {
            logger.error("Error calling FITS service in the RMI registry", e);
            throw new TechMetadataExtractionException(e);
        }
    }

}
