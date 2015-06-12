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
package pl.psnc.synat.fits.tech;

import java.io.File;
import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.metadata.tech.ExtractedMetadata;
import pl.psnc.synat.wrdz.common.metadata.tech.rmi.FitsTechMetadataExtractor;
import edu.harvard.hul.ois.fits.Fits;
import edu.harvard.hul.ois.fits.FitsOutput;
import edu.harvard.hul.ois.fits.exceptions.FitsException;

/**
 * RMI Service implementing a remote interface - it allow calling the FITS tool in the separate JVM sandbox.
 * 
 */
public class FitsTechMetadataExtractorService implements FitsTechMetadataExtractor {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FitsTechMetadataExtractor.class);


    /** FITS instance. */
    //private final Fits fits;

    /**
     * Constructs this technical metadata extractor.
     * 
     * @throws FitsException
     *             when FITS instance could not be created
     */
    public FitsTechMetadataExtractorService()
            throws FitsException {
        //fits = new Fits();
    }


    @Override
    public ExtractedMetadata extractTechMetadata(String path)
            throws RemoteException {
        FitsOutput fitsOutput = null;
        try {
            fitsOutput = new Fits().examine(new File(path));
        } catch (FitsException e) {
            logger.error("Problem with FITS", e);
            throw new RemoteException(e.getMessage());
        }
        try {
            ExtractedMetadataBuilder builder = new ExtractedMetadataBuilder(fitsOutput);
            return builder.build();
        } catch (FitsException e) {
            logger.error("Problem with building metadata XML", e);
            throw new RemoteException(e.getMessage());
        }
    }

}
