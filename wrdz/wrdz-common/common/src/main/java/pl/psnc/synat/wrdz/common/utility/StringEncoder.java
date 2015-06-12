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
 * String encoding utility methods.
 */
public final class StringEncoder {

    /** Private constructor. */
    private StringEncoder() {
    }


    /**
     * Encodes URLs.
     * 
     * @param url
     *            URL to encode
     * @return encoded URL
     */
    public static String encodeUrl(String url) {
        return url.replace("/", "%2F");
    }


    /**
     * Decodes folders.
     * 
     * @param folder
     *            folder path to decode system properties
     * @return decoded path
     */
    public static String decodePath(String folder) {
        if (folder.startsWith("${java.io.tmpdir}")) {
            return folder.replace("${java.io.tmpdir}", System.getProperty("java.io.tmpdir"));
        } else {
            return folder;
        }
    }

}
