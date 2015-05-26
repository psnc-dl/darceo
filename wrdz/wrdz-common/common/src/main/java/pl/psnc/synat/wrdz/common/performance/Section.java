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
package pl.psnc.synat.wrdz.common.performance;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Contains section information (name, parent, subsections and measured time). Used internally by the profiler.
 */
class Section {

    /** Section name. */
    private final String label;

    /** Section subsections. */
    private final Map<String, Section> subsections;

    /** Section parent. */
    private final Section parent;

    /** Total measured time. */
    private long time;

    /** The moment of time when the last measurement started. */
    private long lastStart;


    /**
     * Constructor.
     * 
     * @param label
     *            section name
     * @param parent
     *            section parent
     */
    public Section(String label, Section parent) {
        this.label = label;
        this.subsections = new LinkedHashMap<String, Section>();
        this.parent = parent;
    }


    /**
     * Starts measuring time for this section.
     */
    public void start() {
        lastStart = System.currentTimeMillis();
    }


    /**
     * Stops measuring time and stores the measured value.
     */
    public void stop() {
        time += System.currentTimeMillis() - lastStart;
    }


    public String getLabel() {
        return label;
    }


    public Map<String, Section> getSubsections() {
        return subsections;
    }


    public Section getParent() {
        return parent;
    }


    /**
     * Returns the total measured time for this section.
     * 
     * @return the total measured time, in milliseconds
     */
    public long getTime() {
        return time;
    }
}
