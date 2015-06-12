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
package pl.psnc.synat.wrdz.common.jaxb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

/**
 * Implementation of the {@link NamespaceContext} interface that allows user-defined prefix <-> uri mappings to be added
 * to it.
 * 
 * The contract-required mappings for {@link XMLConstants#XML_NS_PREFIX} and {@link XMLConstants#XMLNS_ATTRIBUTE}
 * prefixes are included by default.
 * <p>
 * This class is not thread-safe.
 */
public class NamespaceContextImpl implements NamespaceContext {

    /** Maps namespace prefixes to their respective uris. */
    private Map<String, String> prefixToUri;

    /** Maps namespace uris to lists of their defined prefixes. */
    private Map<String, List<String>> uriToPrefix;


    /**
     * Default constructor.
     */
    public NamespaceContextImpl() {
        prefixToUri = new HashMap<String, String>();
        uriToPrefix = new HashMap<String, List<String>>();
        add(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
        add(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
    }


    /**
     * Convenience constructor that automatically adds the given prefix <-> uri mapping.
     * 
     * @param prefix
     *            namespace prefix
     * @param uri
     *            namespace uri
     */
    public NamespaceContextImpl(String prefix, String uri) {
        this();
        add(prefix, uri);
    }


    /**
     * Adds the given prefix <-> uri mapping to this namespace context.
     * 
     * {@link IllegalArgumentException} is thrown if a mapping for the given prefix is already defined, or if either the
     * given prefix or uri is null.
     * 
     * @param prefix
     *            namespace prefix
     * @param uri
     *            namespace uri
     */
    public void add(String prefix, String uri) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }
        if (uri == null) {
            throw new IllegalArgumentException("Uri cannot be null");
        }
        if (prefixToUri.containsKey(prefix)) {
            throw new IllegalArgumentException("Mapping for prefix " + prefix + " is already defined");
        }

        prefixToUri.put(prefix, uri);

        List<String> prefixes = uriToPrefix.get(uri);
        if (prefixes == null) {
            prefixes = new ArrayList<String>();
            uriToPrefix.put(uri, prefixes);
        }
        prefixes.add(prefix);
    }


    @Override
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix cannot be null");
        }

        String ns = prefixToUri.get(prefix);
        if (ns == null) {
            ns = XMLConstants.NULL_NS_URI;
        }
        return ns;
    }


    @Override
    public String getPrefix(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Uri cannot be null");
        }

        List<String> prefixes = uriToPrefix.get(uri);
        if (prefixes != null && !prefixes.isEmpty()) {
            return prefixes.get(0);
        }
        return null;
    }


    @Override
    public Iterator<String> getPrefixes(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("Uri cannot be null");
        }

        List<String> prefixes = uriToPrefix.get(uri);
        if (prefixes == null) {
            prefixes = new ArrayList<String>();
        }
        return Collections.unmodifiableList(prefixes).iterator();
    }
}
