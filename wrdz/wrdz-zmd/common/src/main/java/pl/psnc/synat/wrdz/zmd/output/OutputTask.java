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
package pl.psnc.synat.wrdz.zmd.output;

import java.io.Serializable;

/**
 * Class represents a handle to a file in cache - output task to create a file in a digital object.
 */
public class OutputTask implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -5663925964773253667L;

    /**
     * Object-relative path to the resource, including file name.
     */
    protected final String innerPath;

    /**
     * Filename of the resource - the path seen by a user.
     */
    protected final String filename;

    /**
     * Path to the file in the cache.
     */
    protected String cachePath;


    /**
     * Constructs a handle with the object-relative path.
     * 
     * @param innerPath
     *            object-relative path
     * @param filename
     *            filename
     */
    public OutputTask(String innerPath, String filename) {
        this.innerPath = innerPath;
        this.filename = filename;
    }


    public String getInnerPath() {
        return innerPath;
    }


    public String getFilename() {
        return filename;
    }


    public String getCachePath() {
        return cachePath;
    }


    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("OutputTask ");
        sb.append("[innerPath = ").append(innerPath);
        sb.append(", filename = ").append(filename);
        sb.append(", cachePath = ").append(cachePath);
        sb.append("]");
        return sb.toString();
    }

}
