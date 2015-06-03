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
package pl.psnc.synat.dsa;

import java.util.Comparator;

/**
 * Descending comparator of clients based on free space left on connected data storages.
 * 
 */
public class DataStorageClientComparator implements Comparator<DataStorageClient> {

    @Override
    public int compare(DataStorageClient dsc1, DataStorageClient dsc2) {
        return new Long(dsc2.getFreeSpace()).compareTo(dsc1.getFreeSpace());
    }

}
