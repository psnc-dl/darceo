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

import java.util.Set;

import javax.ejb.Remote;

import pl.psnc.synat.wrdz.zmd.dto.format.DataFileFormatDto;

/**
 * Allows access to the data file format information.
 */
@Remote
public interface DataFileFormatBrowser {

    /**
     * Finds active formats for the given types of digital objects and returns their PRONOM database identifiers.
     * 
     * A format is considered active if it is used by a file belonging to the current version of a digital object that
     * has not been migrated from. The type of migration checked depends on the type of the object:
     * <ul>
     * <li>MasterObject - transformation
     * <li>OptimizedObject - optimization
     * <li>ConvertedObject - conversion
     * </ul>
     * 
     * @param checkMasterObjects
     *            whether to check MasterObjects for active formats
     * @param checkOptimizedObjects
     *            whether to check OptimizedObjects for active formats
     * @param checkConvertedObjects
     *            whether to check ConvertedObjects for active formats
     * @return a set of active format PRONOM identifiers
     */
    Set<String> getActiveFormatPuids(boolean checkMasterObjects, boolean checkOptimizedObjects,
            boolean checkConvertedObjects);


    /**
     * Returns a set of format puids used by the data files from the current version of the object with the given
     * identifier. Formats with <code>null</code> puids are ignored.
     * 
     * @param objectIdentifier
     *            object identifier
     * @return a set of format PRONOM identifiers
     */
    Set<String> getFormatPuids(String objectIdentifier);


    /**
     * Returns a set of format DTOs containing basic format data.
     * 
     * @return format information
     */
    Set<DataFileFormatDto> getFormats();
}
