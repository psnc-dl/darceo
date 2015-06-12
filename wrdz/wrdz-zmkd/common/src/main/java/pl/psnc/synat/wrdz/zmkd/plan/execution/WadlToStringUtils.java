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
package pl.psnc.synat.wrdz.zmkd.plan.execution;

import net.java.dev.wadl._2009._02.Param;
import net.java.dev.wadl._2009._02.Representation;

/**
 * Set of methods that return a string representation of WADL nodes.
 */
public final class WadlToStringUtils {

    /**
     * Private constructor.
     */
    private WadlToStringUtils() {
    }


    /**
     * ToString method for (@link net.java.dev.wadl._2009._02.Representation) object.
     * 
     * @param representation
     *            representation
     * @return string representation of representation
     */
    public static String toString(Representation representation) {
        StringBuffer sb = new StringBuffer("Representation ");
        sb.append("[id = ").append(representation.getId());
        sb.append(", mediaType = ").append(representation.getMediaType());
        sb.append("]");
        return sb.toString();
    }


    /**
     * ToString method for (@link net.java.dev.wadl._2009._02.Param) object.
     * 
     * @param param
     *            parameter
     * @return string representation of parameter
     */
    public static String toString(Param param) {
        StringBuffer sb = new StringBuffer("Param ");
        sb.append("[id = ").append(param.getId());
        sb.append(", name = ").append(param.getName());
        sb.append("]");
        return sb.toString();
    }

}
