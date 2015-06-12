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
package pl.psnc.synat.wrdz.zmd.object.parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;

/**
 * Helper used by parsers.
 */
public final class ObjectManagerRequestHelper {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ObjectManagerRequestHelper.class);


    /**
     * Private constructor.
     */
    private ObjectManagerRequestHelper() {
    }


    /**
     * Save a part which carries a file as a file in cache directory.
     * 
     * @param part
     *            part
     * @param parent
     *            path to the cache directory
     * @param filename
     *            filename
     */
    public static void savePartAsFile(com.sun.jersey.multipart.FormDataBodyPart part, String parent, String filename) {
        InputStream is = part.getValueAs(InputStream.class);
        File root = new File(parent);
        if (!root.exists()) {
            root.mkdirs();
        }
        File file = new File(parent + "/" + filename);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            logger.error("Saving the file " + filename + " failed!");
            throw new WrdzRuntimeException(e.getMessage());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.error("Closing the saved file " + filename + " failed!");
                    throw new WrdzRuntimeException(e.getMessage());
                }
            }
        }
    }


    /**
     * Makes and return URI of the file.
     * 
     * @param parent
     *            absolute path to the parent directory
     * @param filename
     *            filename
     * @return URI to the file
     */
    public static String makeUri(String parent, String filename) {
        return new File(parent + "/" + filename).toURI().toString();
    }

}
