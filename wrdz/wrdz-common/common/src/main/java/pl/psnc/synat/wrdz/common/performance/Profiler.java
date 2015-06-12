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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Application-wide profiling mechanism that can be used to measure time spent on particular tasks.
 * <p>
 * A profiling task starts with a call to the {@link #profile(String)} method and ends with a call to {@link #dump()},
 * which forwards the gathered information to a logger. While the profiling task is active, the {@link #start(String)}
 * and {@link #stop(String)} methods can be used to measure time spent on specific subtasks. Profiling subtasks follow a
 * hierarchical structure, and the times measured for subtasks with the same name on the same level are aggregated.
 * <p>
 * IMPORTANT: every call to {@link #start(String)} must be paired with a call to {@link #stop(String)} with the same
 * value, preferably using a try finally block.
 * <p>
 * The profiling mechanism must be explicitly turned on using the {@link #enable()} method, as it might cause a
 * performance hit. When not turned on, all the profiling methods exit immediately and no data is gathered at any point.
 * 
 */
public final class Profiler {

    /** Performance logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger("PERFORMANCE");

    /** Whether the profiling mechanism has been enabled. */
    private static boolean enabled;

    /** Currently open section, per thread. */
    private static ThreadLocal<Section> currentSection;

    /** Whether a profiling task is currently active, per thread. */
    private static ThreadLocal<Boolean> active;


    /**
     * No instances.
     */
    private Profiler() {
        throw new UnsupportedOperationException("No instances");
    }


    /**
     * Enables profiling.
     */
    public static synchronized void enable() {
        LOGGER.debug("enable()");
        enabled = true;
        currentSection = new ThreadLocal<Section>();
        active = new ThreadLocal<Boolean>();
    }


    /**
     * Disables profiling.
     */
    public static synchronized void disable() {
        LOGGER.debug("disable()");
        enabled = false;
        currentSection = null;
        active = null;
    }


    /**
     * Starts profiling for the current thread.
     * 
     * @param label
     *            label of the profiling task (for output purposes)
     */
    public static void profile(String label) {
        if (enabled) {
            LOGGER.debug("profile(\"{}\")", label);
            Section root = new Section(label, null);
            currentSection.set(root);
            active.set(Boolean.TRUE);
            root.start();
        }
    }


    /**
     * Ends profiling for the current thread and dumps the results to log.
     */
    public static void dump() {
        if (enabled && Boolean.TRUE.equals(active.get())) {
            LOGGER.debug("dump()");
            Section root = currentSection.get();
            if (root.getParent() != null) {
                LOGGER.warn("invalid state - open sections encountered during dump");
                while (root.getParent() != null) {
                    root = root.getParent();
                }
            }
            root.stop();

            StringBuilder builder = new StringBuilder();
            dumpSection(root, 0, builder);
            LOGGER.info(builder.toString());

            reset();
        }
    }


    /**
     * Dumps the section information (label, time and subsections) to the given builder.
     * 
     * @param section
     *            section to write to the builder
     * @param level
     *            how deep the section is in the hierarchy (controls indentation)
     * @param builder
     *            builder to write the information to
     */
    private static void dumpSection(Section section, int level, StringBuilder builder) {
        if (level > 0) {
            builder.append('\n');
            for (int i = 0; i < level; i++) {
                builder.append("  ");
            }
            builder.append("- ");
        }
        builder.append(section.getLabel()).append(":  ").append(section.getTime() / 1000.0).append(" seconds");
        for (Section subsection : section.getSubsections().values()) {
            dumpSection(subsection, level + 1, builder);
        }
    }


    /**
     * Resets the state of the profiler for the current thread, canceling the current profiling task if it exists.
     */
    public static void reset() {
        if (enabled) {
            LOGGER.debug("reset()");
            currentSection.remove();
            active.remove();
        }
    }


    /**
     * Starts measuring the elapsed time for the given section.
     * 
     * @param section
     *            name of the section
     */
    public static void start(String section) {
        if (enabled && Boolean.TRUE.equals(active.get())) {
            LOGGER.debug("start(\"{}\")", section);
            Section current = currentSection.get();
            if (current != null) {
                if (!current.getSubsections().containsKey(section)) {
                    current.getSubsections().put(section, new Section(section, current));
                }
                current = current.getSubsections().get(section);
                current.start();
                currentSection.set(current);
            } else {
                LOGGER.warn("start called, but no parent section exists");
            }
        }
    }


    /**
     * Stops measuring the elapsed time for the given section.
     * 
     * @param section
     *            name of the section
     */
    public static void stop(String section) {
        if (enabled && Boolean.TRUE.equals(active.get())) {
            LOGGER.debug("stop(\"{}\")", section);
            Section current = currentSection.get();
            if (current != null && current.getLabel().equals(section)) {
                current.stop();
                currentSection.set(current.getParent());
            } else {
                LOGGER.warn("stop called for a section that isn't open (called: \"{}\"; current: \"{}\")", section,
                    current != null ? current.getLabel() : null);
            }
        }
    }
}
