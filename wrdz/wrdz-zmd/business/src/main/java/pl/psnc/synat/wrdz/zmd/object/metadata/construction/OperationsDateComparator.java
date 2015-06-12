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
package pl.psnc.synat.wrdz.zmd.object.metadata.construction;

import java.util.Comparator;

import pl.psnc.synat.wrdz.common.metadata.xmlns.NamespaceType;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;

/**
 * Comparator class for operations comparison.
 */
public class OperationsDateComparator implements Comparator<Operation> {

    @Override
    public int compare(Operation o1, Operation o2) {
        if (o1.getMetadataType().equals(o2.getMetadataType())) {
            return o1.getDate().compareTo(o2.getDate());
        } else {
            if (o1.getMetadataType().equals(NamespaceType.METS)) {
                return 1;
            }
            if (o2.getMetadataType().equals(NamespaceType.METS)) {
                return -1;
            }
            return o1.getDate().compareTo(o2.getDate());
        }
    }

}
