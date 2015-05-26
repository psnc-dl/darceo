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
package pl.psnc.synat.wrdz.zmd.download.adapters;

import java.io.File;

import pl.psnc.synat.wrdz.zmd.download.ConnectionInformation;
import pl.psnc.synat.wrdz.zmd.exception.DownloadAdapterException;

/**
 * A base class for all download adapters.
 */
public abstract class AbstractDownloadAdapter {

    /**
     * Connection parameters including repository address and authentication data.
     */
    protected final ConnectionInformation connectionInfo;

    /**
     * Absolute path to the cache root folder.
     */
    protected final String cacheHome;


    /**
     * Creates new abstract download adapter initializing it with the values of the parameters passed.
     * 
     * @param connectionInfo
     *            connection parameters including repository address and authentication data.
     * @param cacheHome
     *            absolute path to the cache root folder.
     */
    public AbstractDownloadAdapter(ConnectionInformation connectionInfo, String cacheHome) {
        this.connectionInfo = connectionInfo;
        this.cacheHome = cacheHome;
    }


    /**
     * Provides absolute path to the resource in cache.
     * 
     * @param relativePath
     *            object-relative path to the resource.
     * @return absolute path to the resource in cache.
     */
    protected String getResourceCachePath(String relativePath) {
        File file = new File(relativePath);
        if (file.isAbsolute()) {
            throw new IllegalArgumentException("Absolute paths cannot be used! Use relative path!");
        } else {
            return cacheHome + "/" + file.getPath();
        }
    }


    /**
     * Checks if destination folder exists, if not, creates it and all nonexistent folders on the path.
     * 
     * @param path
     *            path for the resource.
     * @throws DownloadAdapterException
     *             if problems with path creation occur.
     */
    protected void checkDestinationExistence(String path)
            throws DownloadAdapterException {
        File f = new File(path).getParentFile();
        if (!f.exists()) {
            try {
                f.mkdirs();
            } catch (SecurityException e) {
                throw new DownloadAdapterException("Could not create directory structure for " + path, e);
            }
        }
    }

}
