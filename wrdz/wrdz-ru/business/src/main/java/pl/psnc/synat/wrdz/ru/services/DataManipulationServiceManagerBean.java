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
package pl.psnc.synat.wrdz.ru.services;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import pl.psnc.synat.wrdz.ru.dao.services.DataManipulationServiceDao;
import pl.psnc.synat.wrdz.ru.entity.services.DataManipulationService;

/**
 * Class managing the {@link DataManipulationService} entities basic operations.
 */
@Stateless
public class DataManipulationServiceManagerBean implements DataManipulationServiceManager {

    /**
     * Data manipulation service DAO for persistence operations.
     */
    @EJB
    private DataManipulationServiceDao dataManipulationServiceDao;


    @Override
    public void removeServices(List<DataManipulationService> describedServices) {
        for (int i = 0; i < describedServices.size(); i++) {
            dataManipulationServiceDao.delete(describedServices.get(i));

        }
        describedServices.clear();
        dataManipulationServiceDao.flush();
    }
}
