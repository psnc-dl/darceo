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
package pl.psnc.synat.wrdz.ms.services;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import pl.psnc.synat.wrdz.ru.dto.services.DataManipulationServiceDto;
import pl.psnc.synat.wrdz.ru.services.DataManipulationServiceBrowser;

/**
 * Bean that handles data manipulation services.
 */
@ManagedBean
@ViewScoped
public class DataManipulationServiceBean implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -5503721687481289287L;

    /** Services. */
    private List<DataManipulationServiceDto> services;

    /** Service browser. */
    @EJB(name = "DataManipulationServiceBrowser")
    private transient DataManipulationServiceBrowser serviceBrowser;


    /**
     * Refreshes the currently cached data manipulation services.
     */
    public void refresh() {
        services = serviceBrowser.getActiveServices();
    }


    /**
     * Returns the list of data manipulation services.
     * 
     * @return a list of data manipulation services
     */
    public List<DataManipulationServiceDto> getServices() {
        return services;
    }
}
