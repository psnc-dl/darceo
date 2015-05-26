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
package pl.psnc.synat.wrdz.common.metadata.tech.rmi;

/**
 * Constants used by the RMI service of the FITS tool.
 */
public final class FitsTechMetadataExtractorConsts {

    /**
     * private constructor.
     */
    private FitsTechMetadataExtractorConsts() {
    }


    /**
     * Port of the RMI registry.
     */
    public static final int RMI_REGISTRY_PORT = 2001;

    /**
     * Port of the FITS RMI service.
     */
    public static final int FITS_RMI_SERVICE_PORT = 2002;

    /**
     * Name of the FITS RMI service.
     */
    public static final String FITS_RMI_SERVICE_NAME = "FitsRmiService";

}
