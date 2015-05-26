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
package pl.psnc.synat.wrdz.zmd.object.async;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Helper methods for asynchronous request processing of read operations concerning digital object.
 */
public final class ObjectManagerAsyncHelper {

    /**
     * Private constructor.
     */
    private ObjectManagerAsyncHelper() {
    }


    /**
     * Constructs an unique URL for the request of a digital object.
     * 
     * @param identifier
     *            identifier of an object
     * @param version
     *            version of an object
     * @param provided
     *            whether to get provided metadata of an object
     * @param extracted
     *            whether to get extracted metadata of an object
     * @return unique URL
     */
    public static String getObjectUniqueContextUrl(String identifier, Integer version, Boolean provided,
            Boolean extracted) {
        StringBuffer result = new StringBuffer("object/");
        result.append(identifier).append("?");
        result.append("version=").append(version).append("&");
        result.append("provided=").append(provided).append("&");
        result.append("extracted=").append(extracted);
        return result.toString();
    }


    /**
     * Transforms a string containing an unsorted list of files into a sorted {@link List} of files.
     * 
     * @param filesList
     *            semicolon separated list of files paths.
     * @return Sorted list of paths.
     */
    public static List<String> getSortedFilesList(String filesList) {
        List<String> files = new ArrayList<String>(Arrays.asList(filesList.split(";")));
        Collections.sort(files);
        return files;
    }


    /**
     * Constructs an unique URL for the request of a digital object's files.
     * 
     * @param identifier
     *            identifier of an object
     * @param version
     *            version of an object
     * @param provided
     *            whether to get provided metadata of an object
     * @param extracted
     *            whether to get extracted metadata of an object
     * @param filesList
     *            pre-sorted list of file paths.
     * @return unique URL
     */
    public static String getDataFilesUniqueContextUrl(String identifier, Integer version, Boolean provided,
            Boolean extracted, List<String> filesList) {
        StringBuffer result = new StringBuffer("object/");
        result.append(identifier).append("/files?");
        result.append("version=").append(version).append("&");
        result.append("provided=").append(provided).append("&");
        result.append("extracted=").append(extracted).append("&");
        result.append("filelist=");
        for (int i = 0; i < filesList.size(); i++) {
            if (i > 0) {
                result.append(";");
            }
            result.append(filesList.get(i));
        }
        return result.toString();
    }


    /**
     * Constructs an unique URL for the request of a digital object's main file.
     * 
     * @param identifier
     *            identifier of an object
     * @param version
     *            version of an object
     * @param provided
     *            whether to get provided metadata of an object
     * @param extracted
     *            whether to get extracted metadata of an object
     * @return unique URL
     */
    public static String getMainFileUniqueContextUrl(String identifier, Integer version, Boolean provided,
            Boolean extracted) {
        StringBuffer result = new StringBuffer("object/");
        result.append(identifier).append("/mainfile?");
        result.append("version=").append(version).append("&");
        result.append("provided=").append(provided).append("&");
        result.append("extracted=").append(extracted);
        return result.toString();
    }


    /**
     * Constructs an unique URL for the request of a digital object's metadata.
     * 
     * @param identifier
     *            identifier of an object
     * @param version
     *            version of an object
     * @param provided
     *            whether to get provided metadata of an object
     * @return unique URL
     */
    public static String getMetadataUniqueContextUrl(String identifier, Integer version, Boolean provided) {
        StringBuffer result = new StringBuffer("object/");
        result.append(identifier).append("/metadata?");
        result.append("version=").append(version).append("&");
        result.append("provided=").append(provided);
        return result.toString();
    }

}
