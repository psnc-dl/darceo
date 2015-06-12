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
package pl.psnc.synat.wrdz.common.utility;

/**
 * Utility class for objects comparison.
 */
public final class CompareUtility {

    /**
     * Hidden default constructor for utility class.
     */
    private CompareUtility() {
        throw new UnsupportedOperationException("No instances");
    }


    /**
     * Compares two objects.
     * 
     * @param first
     *            first of compared objects.
     * @param second
     *            second of compared objects
     * @return <code>true</code> if objects are equal, <code>false</code> otherwise.
     */
    public static boolean areEqual(Object first, Object second) {
        if (first == second) {
            return true;
        } else {
            if (first != null && second != null) {
                return first.equals(second);
            }
            return false;
        }
    }

}
