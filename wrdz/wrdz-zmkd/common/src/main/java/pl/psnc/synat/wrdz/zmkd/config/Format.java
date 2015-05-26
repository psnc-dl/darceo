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
package pl.psnc.synat.wrdz.zmkd.config;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import pl.psnc.synat.wrdz.zmkd.ddr.Plugin;

/**
 * File format. This class is immutable.
 */
public class Format {

    /** Format name. */
    private final String name;

    /** Format type. */
    private final FormatType type;

    /** Format UDFR IRIs. */
    private final List<String> iris;

    /** Plugins this format is supported by. */
    private final Set<Plugin> supportedBy;


    /**
     * Constructor.
     * 
     * @param name
     *            format name
     * @param type
     *            format type
     * @param iris
     *            format UDFR IRIs (at least one)
     * @param supportedBy
     *            plugins that support this format (can be <code>null</code>)
     */
    public Format(String name, FormatType type, List<String> iris, Set<Plugin> supportedBy) {
        if (name == null || type == null || iris == null || iris.isEmpty()) {
            throw new InvalidParameterException("name: " + name + ", type: " + type + ", iris: " + iris);
        }

        this.name = name;
        this.type = type;
        this.iris = Collections.unmodifiableList(new ArrayList<String>(iris));
        this.supportedBy = supportedBy != null ? Collections.unmodifiableSet(EnumSet.copyOf(supportedBy)) : Collections
                .<Plugin> emptySet();
    }


    public String getName() {
        return name;
    }


    public FormatType getType() {
        return type;
    }


    public List<String> getIris() {
        return iris;
    }


    public Set<Plugin> getSupportedBy() {
        return supportedBy;
    }


    /**
     * Format type.
     */
    public enum FormatType {

        /** Image format. */
        IMAGE,

        /** Video format. */
        VIDEO,

        /** Audio format. */
        AUDIO,

        /** Document format. */
        DOC;
    }
}
