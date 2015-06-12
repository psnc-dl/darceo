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
package pl.psnc.synat.wrdz.mdz.format;

import javax.ejb.Local;

import pl.psnc.synat.wrdz.mdz.entity.format.FileFormat;

/**
 * Format verifiers evaluate the loss risk and decide whether formats require migration.
 */
@Local
public interface FileFormatVerifier {

    /**
     * Evaluates the loss risk for the given format and decides whether it requires migration to a more contemporary
     * format.
     * 
     * @param format
     *            format to be evaluated
     * @return <code>true</code> if the format requires migration; <code>false</code> otherwise
     */
    boolean isMigrationRequired(FileFormat format);
}
