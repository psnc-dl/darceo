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
package pl.psnc.synat.wrdz.zmd.object.helpers;

/**
 * Contains handful of utils used when handling digital object's and their structure.
 */
public final class ObjectUtils {

    /**
     * Hidden constructor.
     */
    private ObjectUtils() {

    }


    /**
     * Prepares a hex-based structured path part for version.
     * 
     * @param version
     *            number of version
     * @return structured version path part
     */
    public static String createVersionPath(int version) {
        StringBuilder sb = new StringBuilder(Integer.toHexString(version));
        for (int i = 0; i < 11; i++) {
            if (i % 3 == 2) {
                sb.insert(sb.length() - i, "/");
            } else if (sb.length() - i <= 0) {
                sb.insert(0, "0");
            }
        }
        return sb.toString();
    }


    /**
     * Prepares a hex-based structured path part for object id.
     * 
     * @param id
     *            object's internal identifier
     * @return structured object id path part
     */
    public static String createObjectIdPath(long id) {
        StringBuilder sb = new StringBuilder(Long.toHexString(id));
        for (int i = 0; i < 23; i++) {
            if (i % 3 == 2) {
                sb.insert(sb.length() - i, "/");
            } else if (sb.length() - i <= 0) {
                sb.insert(0, "0");
            }
        }
        return sb.toString();
    }


    /**
     * Prepares a hex-based structured path part for object id and its version.
     * 
     * @param id
     *            object's internal identifier
     * @param version
     *            number of version
     * 
     * @return structured object id path part and version path part
     */
    public static String createObjectAndVersionPath(long id, int version) {
        StringBuilder sb = new StringBuilder();
        sb.append(createObjectIdPath(id)).append("/");
        sb.append(createVersionPath(version)).append("/");
        return sb.toString();
    }

}
