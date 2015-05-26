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

import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

/**
 * Utility methods.
 */
public final class PlanExecutionParserUtils {

    /**
     * Private constructor.
     */
    private PlanExecutionParserUtils() {

    }


    /**
     * XML Schema namespace URI.
     */
    private static final String XS_NAMESPACE_URI = "http://www.w3.org/2001/XMLSchema";


    /**
     * Return XS type of the qualified name or XS String type in case of null.
     * 
     * @param qname
     *            qualified name
     * @return string representation of qualified name
     */
    public static String getXSTypeAsString(QName qname) {
        if (qname == null) {
            return XS_NAMESPACE_URI + "#string";
        } else {
            String namaspace = qname.getNamespaceURI() != null ? qname.getNamespaceURI() : XS_NAMESPACE_URI;
            return namaspace + "#" + qname.getLocalPart();
        }
    }


    /**
     * Gets value of mediaType attribute.
     * 
     * @param attributes
     *            list of attributes
     * @return mediaType value or null
     */
    public static String getMediaTypeFromAttributes(Map<QName, String> attributes) {
        for (Entry<QName, String> attribute : attributes.entrySet()) {
            if (attribute.getKey().getLocalPart().equals("mediaType")) {
                return attribute.getValue();
            }
        }
        return null;
    }

}
