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
package pl.psnc.synat.wrdz.mdz.format;

import java.util.concurrent.Future;

import javax.ejb.Local;

/**
 * Responsible for the processing part of the work cycle.
 */
@Local
public interface FileFormatProcessor {

    /**
     * Iteratively processes all file formats stored in the database.
     * 
     * This method runs asynchronously and is cancellable without losing progress (ie. calling it again after it has
     * been cancelled resumes the processing instead of restarting it).
     * 
     * @see Future#cancel(boolean)
     * @return empty Future object that allows the processing to be stopped if needed
     */
    Future<Void> processAll();


    /**
     * Processes a single ("randomly" chosen) format.
     * 
     * Processing includes retrieving and removing the format from the database, evaluating the format's loss risk and
     * sending a message if migration to a more contemporary format is required.
     * 
     * <p>
     * If this method returns <code>false</code>, it means there are no more formats to be processed and the work cycle
     * has ended.
     * 
     * @return <code>true</code> if a format was found and processed; <code>false</code> otherwise
     */
    boolean processOne();
}
