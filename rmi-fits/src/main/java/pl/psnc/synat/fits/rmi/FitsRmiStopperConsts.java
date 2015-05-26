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
package pl.psnc.synat.fits.rmi;

/**
 * Constants used by the RMI stopping service.
 * 
 */
public class FitsRmiStopperConsts {

    /**
     * private constructor.
     */
    private FitsRmiStopperConsts() {
    }


    /**
     * Port of the RMI registry.
     */
    public static final int RMI_REGISTRY_PORT = 2001;

    /**
     * Port of the FITS RMI service.
     */
    public static final int FITS_STOP_RMI_SERVICE_PORT = 2003;

    /**
     * Name of the FITS RMI service.
     */
    public static final String FITS_STOP_RMI_SERVICE_NAME = "StopFitsRmiService";

}
