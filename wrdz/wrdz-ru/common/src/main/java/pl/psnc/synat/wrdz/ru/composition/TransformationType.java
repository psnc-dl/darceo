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

import java.io.Serializable;

/**
 * Transformation types.
 */
public enum TransformationType implements Serializable {

    /**
     * One file into one file.
     */
    ONE_TO_ONE,

    /**
     * Many files (all files of a given format) into one file.
     */
    MANY_TO_ONE,

    /**
     * One file into many files.
     */
    ONE_TO_MANY;

}
