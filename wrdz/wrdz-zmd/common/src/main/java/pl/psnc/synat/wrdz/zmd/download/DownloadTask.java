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
package pl.psnc.synat.wrdz.zmd.download;

import java.net.URI;

import pl.psnc.synat.wrdz.zmd.output.OutputTask;

/**
 * A class representing download task, i.e. singular file to download from the given URL.
 */
public class DownloadTask extends OutputTask {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 3036992950630341124L;

    /**
     * URI of the resource to be downloaded.
     */
    private final URI uri;


    /**
     * Construct new instance of this class using given parameters.
     * 
     * @param uri
     *            an URL compliant URI of the resource to download.
     * @param innerPath
     *            the object relative path where the selected resource is to be stored.
     * @param filename
     *            filename of the file - the path seen by a user
     */
    public DownloadTask(URI uri, String innerPath, String filename) {
        super(innerPath, filename);
        this.uri = uri;
    }


    public URI getUri() {
        return uri;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cachePath == null) ? 0 : cachePath.hashCode());
        result = prime * result + ((innerPath == null) ? 0 : innerPath.hashCode());
        result = prime * result + ((uri == null) ? 0 : uri.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DownloadTask)) {
            return false;
        }
        DownloadTask other = (DownloadTask) obj;
        if (cachePath == null) {
            if (other.cachePath != null) {
                return false;
            }
        } else if (!cachePath.equals(other.cachePath)) {
            return false;
        }
        if (innerPath == null) {
            if (other.innerPath != null) {
                return false;
            }
        } else if (!innerPath.equals(other.innerPath)) {
            return false;
        }
        if (uri == null) {
            if (other.uri != null) {
                return false;
            }
        } else if (!uri.equals(other.uri)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        return "DownloadTask [uri=" + uri + ", innerPath=" + innerPath + ", cachePath=" + cachePath + "]";
    }

}
