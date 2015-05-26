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
package pl.psnc.synat.wrdz.ru.services.descriptors;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import pl.psnc.synat.wrdz.ru.dao.services.descriptors.TechnicalDescriptorDao;
import pl.psnc.synat.wrdz.ru.entity.services.descriptors.TechnicalDescriptor;

/**
 * Class managing the {@link TechnicalDescriptor} entities basic operations.
 */
@Stateless
public class TechnicalDescriptorManagerBean implements TechnicalDescriptorManager {

    /**
     * Technical descriptor DAO for persistence operations.
     */
    @EJB
    private TechnicalDescriptorDao technicalDescriptorDao;


    @Override
    public void removeTechnicalDescriptors(List<TechnicalDescriptor> technicalDescriptors) {
        for (int i = 0; i < technicalDescriptors.size(); i++) {
            technicalDescriptorDao.delete(technicalDescriptors.get(i));
        }
        technicalDescriptors.clear();
        technicalDescriptorDao.flush();
    }

}
