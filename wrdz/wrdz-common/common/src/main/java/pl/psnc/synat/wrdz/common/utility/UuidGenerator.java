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

import java.util.UUID;

import javax.enterprise.inject.Default;

/**
 * Delivers a generated UUID values.
 */
@Default
public class UuidGenerator {

    /**
     * Generates UUID for cache folders.
     * 
     * @return random unique cache folder
     */
    public String generateCacheFolderName() {
        return "tmp" + generateUuid();
    }


    /**
     * Generates UUID for filenames.
     * 
     * @return random unique filename
     */
    public String generateRandomFileName() {
        return "file" + generateUuid();
    }


    /**
     * Generates UUID.
     * 
     * @return random UUID
     */
    public String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
