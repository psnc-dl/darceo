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
package pl.psnc.synat.wrdz.zmd.entity.types;

/**
 * File format validation status.
 */
public enum ValidationStatus {

    /**
     * File format recognized and the file is well formed according to the definition of this format.
     */
    WELL_FORMED,
    /**
     * File format recognized but the file has some dissonances with the definition of this format.
     */
    MALFORMED,
    /**
     * File format unrecognized.
     */
    UNKNOWN;

}
