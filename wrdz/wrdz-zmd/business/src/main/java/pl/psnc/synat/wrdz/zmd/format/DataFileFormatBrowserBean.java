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
package pl.psnc.synat.wrdz.zmd.format;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.zmd.dao.object.content.DataFileFormatDao;
import pl.psnc.synat.wrdz.zmd.dto.format.DataFileFormatDto;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileFormat;
import pl.psnc.synat.wrdz.zmd.entity.object.content.DataFileFormatName;

/**
 * Allows access to the data file formats information.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class DataFileFormatBrowserBean implements DataFileFormatBrowser {

    /** Data file format DAO. */
    @EJB
    private DataFileFormatDao dataFileFormatDao;


    @Override
    public Set<String> getActiveFormatPuids(boolean checkMasterObjects, boolean checkOptimizedObjects,
            boolean checkConvertedObjects) {

        Set<DataFileFormat> formats = dataFileFormatDao.findActive(checkMasterObjects, checkOptimizedObjects,
            checkConvertedObjects);

        Set<String> puids = new HashSet<String>();
        for (DataFileFormat format : formats) {
            puids.add(format.getPuid());
        }

        return puids;
    }


    @Override
    public Set<String> getFormatPuids(String objectIdentifier) {

        List<DataFileFormat> formats = dataFileFormatDao.findByObjectId(objectIdentifier);

        Set<String> puids = new HashSet<String>();
        for (DataFileFormat format : formats) {
            if (format.getPuid() != null) {
                puids.add(format.getPuid());
            }
        }

        return puids;
    }


    @Override
    public Set<DataFileFormatDto> getFormats() {
        Set<DataFileFormatDto> results = new HashSet<DataFileFormatDto>();

        for (DataFileFormat format : dataFileFormatDao.findAllWithPuidFetchNames()) {
            List<String> names = new ArrayList<String>();
            for (DataFileFormatName name : format.getNames()) {
                names.add(name.getName());
            }
            results.add(new DataFileFormatDto(format.getPuid(), format.getVersion(), names));
        }

        return results;
    }
}
