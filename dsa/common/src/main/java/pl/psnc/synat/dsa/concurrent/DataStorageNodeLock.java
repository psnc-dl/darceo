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
package pl.psnc.synat.dsa.concurrent;

/**
 * Lock for data storage node (folder).
 * 
 */
public class DataStorageNodeLock {

    /**
     * Thread that have this lock.
     */
    private final Thread thread;

    /**
     * Path of the node which is locked.
     */
    private final String path;

    /**
     * Mode of locking.
     */
    private final LockMode mode;


    /**
     * Constructs new lock.
     * 
     * @param thread
     *            thread
     * @param path
     *            node path
     * @param mode
     *            mode of locking
     */
    DataStorageNodeLock(Thread thread, String path, LockMode mode) {
        this.thread = thread;
        this.path = path;
        this.mode = mode;
    }


    public Thread getThread() {
        return thread;
    }


    public String getPath() {
        return path;
    }


    public LockMode getMode() {
        return mode;
    }


    /**
     * Checks if path is contained in subtree determined by node path of this lock.
     * 
     * @param path
     *            path to the node to check
     * @return if it is contained
     */
    public boolean contains(String path) {
        if (path.startsWith(this.path)) {
            return true;
        }
        return false;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("DataStorageNodeLock ");
        sb.append("[thread = ").append(thread);
        sb.append(", path = ").append(path);
        sb.append(", mode = ").append(mode);
        sb.append("]");
        return sb.toString();
    }

}
