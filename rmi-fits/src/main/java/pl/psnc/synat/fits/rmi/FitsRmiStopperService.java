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

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

import pl.psnc.synat.wrdz.common.metadata.tech.rmi.FitsTechMetadataExtractorConsts;

/**
 * Service which stop the RMI service and registry.
 * 
 */
public class FitsRmiStopperService implements FitsRmiStopper {

    @Override
    public void stop(Registry registry)
            throws RemoteException {
        try {
            registry.unbind(FitsTechMetadataExtractorConsts.FITS_RMI_SERVICE_NAME);
        } catch (NotBoundException e) {
            System.out.println(e.toString());
        }
        new StoppingThread().start();
    }


    private class StoppingThread extends Thread {

        public void run() {
            try {
                sleep(3000);
            } catch (InterruptedException e) {
            }
            System.exit(0);
        }
    }

}
