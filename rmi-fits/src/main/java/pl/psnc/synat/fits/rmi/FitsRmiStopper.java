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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;

/**
 * Interface to stop the FITS service.
 * 
 */
public interface FitsRmiStopper extends Remote {

    /**
     * Stops the RMI service and registry.
     * 
     * 
     * @param registry
     *            RMI registry referene
     * @throws RemoteException
     *             when something went wrong
     */
    void stop(Registry registry)
            throws RemoteException;

}
