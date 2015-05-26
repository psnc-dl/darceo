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
package pl.psnc.synat.wrdz.zmd.object;

/**
 * Interface containing constants with names or parts of names of folders in physical structure of the digital object.
 */
public final class ObjectStructure {

    /**
     * Hides default public constructor.
     */
    private ObjectStructure() {
    }


    /**
     * Prefix in the name of folder containing version of file, suffix being the number of the version.
     */
    public static final String VERSION = "version";

    /**
     * Name of the folder containing object's contents.
     */
    public static final String CONTENT = "content";

    /**
     * Name of the folder containing object's metadata and object's contents metadata.
     */
    public static final String METADATA = "metadata";

    /**
     * Name of the subfolder containing metadata extracted from files by the system.
     */
    public static final String EXTRACTED = "extracted";

    /**
     * Name of the subfolder containing metadata provided by the user.
     */
    public static final String PROVIDED = "provided";

    /**
     * Filesystem dependent separator.
     */
    public static final String SEPARATOR = "/";


    /**
     * Constructs full object basedir related path for content.
     * 
     * @param contentRelativePath
     *            content basedir relative path.
     * @return object basedir related path.
     */
    public static String getPathForContent(String contentRelativePath) {
        if (contentRelativePath.substring(0, 1).equals(SEPARATOR)) {
            return CONTENT + contentRelativePath;
        } else {
            return CONTENT + SEPARATOR + contentRelativePath;
        }
    }


    /**
     * Constructs filename for content.
     * 
     * @param contentRelativePath
     *            content basedir relative path.
     * @return filename for content.
     */
    public static String getFilenameForContent(String contentRelativePath) {
        if (contentRelativePath.substring(0, 1).equals(SEPARATOR)) {
            return contentRelativePath.substring(1);
        } else {
            return contentRelativePath;
        }
    }


    /**
     * Constructs full object basedir related path for content's metadata.
     * 
     * @param contentRelativePath
     *            content basedir relative path.
     * @param name
     *            metadata file name.
     * @return object basedir related path.
     */
    public static String getPathForProvidedMetadata(String contentRelativePath, String name) {
        StringBuilder builder = new StringBuilder(METADATA);
        if (contentRelativePath != null && !contentRelativePath.isEmpty()) {
            if (contentRelativePath.substring(0, 1).equals(SEPARATOR)) {
                builder.append(contentRelativePath);
            } else {
                builder.append(SEPARATOR + contentRelativePath);
            }

        }
        builder.append(SEPARATOR + PROVIDED + SEPARATOR + name);
        return builder.toString();
    }


    /**
     * Constructs full object basedir related path for extrected metadata.
     * 
     * @param contentRelativePath
     *            content basedir relative path.
     * @param name
     *            metadata file name.
     * @return object basedir related path.
     */
    public static String getPathForExtractedMetadata(String contentRelativePath, String name) {
        StringBuilder builder = new StringBuilder(METADATA);
        if (contentRelativePath != null && !contentRelativePath.isEmpty()) {
            if (contentRelativePath.substring(0, 1).equals(SEPARATOR)) {
                builder.append(contentRelativePath);
            } else {
                builder.append(SEPARATOR + contentRelativePath);
            }

        }
        builder.append(SEPARATOR + EXTRACTED + SEPARATOR + name);
        return builder.toString();
    }

}
