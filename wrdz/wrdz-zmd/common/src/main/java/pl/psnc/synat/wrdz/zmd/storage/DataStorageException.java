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
package pl.psnc.synat.wrdz.zmd.storage;

import pl.psnc.synat.wrdz.common.exception.WrdzException;

/**
 * Thrown when some error during connection to data storage occurs.
 */
public class DataStorageException extends WrdzException {

    /** Serial version UID. */
    private static final long serialVersionUID = -9121208107549429015L;


    /**
     * Constructs a new <code>DataStorageException</code> with the specified cause.
     * 
     * @param cause
     *            the cause
     */
    public DataStorageException(Throwable cause) {
        super(cause);
    }

}
