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
package pl.psnc.synat.wrdz.ru.composition;

import java.util.Comparator;

/**
 * Comparator of service chain that ranks them by rising execution costs.
 */
public class TransformationChainComparator implements Comparator<TransformationChain> {

    @Override
    public int compare(TransformationChain c1, TransformationChain c2) {
        if ((c1.getExecutionCost() == null) && (c2.getExecutionCost() == null)) {
            return 0;
        }
        if ((c1.getExecutionCost() != null) && (c2.getExecutionCost() == null)) {
            return -1;
        }
        if ((c1.getExecutionCost() == null) && (c2.getExecutionCost() != null)) {
            return 1;
        }
        return c1.getExecutionCost().compareTo(c2.getExecutionCost());
    }

}
