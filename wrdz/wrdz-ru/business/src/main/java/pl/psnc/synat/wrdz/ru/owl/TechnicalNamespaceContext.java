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
package pl.psnc.synat.wrdz.ru.owl;

import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

/**
 * Namespace context for extracting information form technical descriptors. Due to it's incomplete implementation
 * violating some contracts of the interface in unneeded methods it is package-scoped and potentially dangerous
 * unimplemented methods are marked as deprecated.
 */
class TechnicalNamespaceContext implements NamespaceContext {

    /**
     * WSDL SOAP namespace prefix.
     */
    public static final String WSDL_SOAP_PREFIX = "soap";

    /**
     * WSDL namespace prefix.
     */
    public static final String WSDL_PREFIX = "wsdl";

    /**
     * WADL namespace prefix.
     */
    public static final String WADL_PREFIX = "wadl";

    /**
     * WSDL SOAP namespace.
     */
    private static final String WSDL_SOAP_NS = "http://schemas.xmlsoap.org/wsdl/soap/";

    /**
     * WSDL namespace.
     */
    private static final String WSDL_NS = "http://schemas.xmlsoap.org/wsdl/";

    /**
     * WADL namespace.
     */
    private static final String WADL_NS = "http://wadl.dev.java.net/2009/02";


    @Override
    public String getNamespaceURI(String prefix) {
        if (WSDL_SOAP_PREFIX.equals(prefix)) {
            return WSDL_SOAP_NS;
        } else if (WSDL_PREFIX.equals(prefix)) {
            return WSDL_NS;
        } else if (WADL_PREFIX.equals(prefix)) {
            return WADL_NS;
        }
        return null;
    }


    @Override
    public String getPrefix(String namespaceURI) {
        if (WSDL_SOAP_NS.equals(namespaceURI)) {
            return WSDL_SOAP_PREFIX;
        } else if (WSDL_NS.equals(namespaceURI)) {
            return WSDL_PREFIX;
        } else if (WADL_NS.equals(namespaceURI)) {
            return WADL_PREFIX;
        }
        return null;
    }


    /**
     * Unimplemented, unused, do not use this method, it will always return <code>null</code>.
     * 
     * @param namespaceURI
     *            unused bun necessary for overrding param.
     * @return <code>null</code>
     */
    @SuppressWarnings("rawtypes")
    @Deprecated
    @Override
    public Iterator getPrefixes(String namespaceURI) {
        // unimplemented
        return null;
    }

}
