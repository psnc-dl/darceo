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
package pl.psnc.synat.sra.concurrent;

/**
 * Lock for data semantic repository.
 * 
 */
public class SemanticRepositoryLock {

    /**
     * Thread that have this lock.
     */
    private final Thread thread;

    /**
     * Mode of locking.
     */
    private final LockMode mode;


    /**
     * Constructs new lock.
     * 
     * @param thread
     *            thread
     * @param mode
     *            mode of locking
     */
    SemanticRepositoryLock(Thread thread, LockMode mode) {
        this.thread = thread;
        this.mode = mode;
    }


    public Thread getThread() {
        return thread;
    }


    public LockMode getMode() {
        return mode;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("SemanticRepositoryLock ");
        sb.append("[thread = ").append(thread);
        sb.append(", mode = ").append(mode);
        sb.append("]");
        return sb.toString();
    }

}
