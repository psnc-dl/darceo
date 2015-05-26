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
package pl.psnc.synat.dsa.util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Provides static utility methods for objects associated with a file system.
 * 
 */
public final class FilenameUtils {

    /**
     * Private constructor.
     */
    private FilenameUtils() {
    }


    /**
     * Splits path to subfolders and a file.
     * 
     * @param path
     *            path
     * @return list of subfolders and a file
     */
    public static List<String> splitPath(String path) {
        List<String> result = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(path, "/");
        while (tokenizer.hasMoreElements()) {
            result.add(tokenizer.nextToken());
        }
        return result;
    }


    /**
     * Gets the parent folder for the file specified by the path.
     * 
     * @param path
     *            path of a file
     * @return parent folder
     */
    public static String getParentFolder(String path) {
        int idx = path.lastIndexOf("/");
        if (idx != -1) {
            return path.substring(0, idx);
        } else {
            return "";
        }
    }

}
