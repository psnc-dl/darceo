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
package pl.psnc.synat.wrdz.common.metadata.xmlns;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class providing default schema location for basic namespaces concerning administrative and technical metadata of
 * digital objects.
 */
public final class DefaultNamespaceDictionary {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(DefaultNamespaceDictionary.class);

    /** Configuration file with default namaspaces. */
    private static final String CONFIG_FILE = "wrdz-namespaces.xml";

    /**
     * The only one instance of the dictionary.
     */
    private static DefaultNamespaceDictionary instance;

    /**
     * Map of namespaces and their standard schema locations and type.
     */
    private Map<String, Map<String, Object>> namespaces;


    /**
     * Private constructor. Loads namespaces from the config file.
     */
    private DefaultNamespaceDictionary() {
        try {
            Configuration config = new XMLConfiguration(CONFIG_FILE);
            namespaces = new HashMap<String, Map<String, Object>>();
            int size = config.getList("namespace.uri").size();
            for (int i = 0; i < size; i++) {
                String uri = config.getString("namespace(" + i + ").uri");
                Map<String, Object> values = new HashMap<String, Object>();
                String schemaLocation = config.getString("namespace(" + i + ").schemaLocation");
                if (schemaLocation.length() > 0) {
                    values.put("schemaLocation", schemaLocation);
                } else {
                    values.put("schemaLocation", null);
                }
                values.put("type", NamespaceType.valueOf(config.getString("namespace(" + i + ").type")));
                namespaces.put(uri, values);
            }
        } catch (ConfigurationException e) {
            logger.error("There was a problem with loading the configuration with namespaces.", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Return the only one instance of this class.
     * 
     * @return instance
     */
    public static DefaultNamespaceDictionary getInstance() {
        if (instance == null) {
            instance = new DefaultNamespaceDictionary();
        }
        return instance;
    }


    /**
     * Returns default schema location for specified URI.
     * 
     * @param uri
     *            URI
     * @return schema location
     */
    public String getDefaultSchemaLocation(String uri) {
        Map<String, Object> values = getValuesForUri(uri);
        if (values != null) {
            return (String) values.get("schemaLocation");
        } else {
            return null;
        }
    }


    /**
     * Returns the type of namespace for specified URI.
     * 
     * @param uri
     *            URI
     * @return type
     */
    public NamespaceType getTypeOfNamespace(String uri) {
        Map<String, Object> values = getValuesForUri(uri);
        if (values != null) {
            return (NamespaceType) values.get("type");
        } else {
            return NamespaceType.UNKNOWN;
        }
    }


    /**
     * Gets values for URI. It takes into account some omitting character at the end of URI.
     * 
     * @param uri
     *            URI
     * @return internal values or null
     */
    private Map<String, Object> getValuesForUri(String uri) {
        Map<String, Object> values = namespaces.get(uri);
        if (values == null) {
            values = namespaces.get(uri + "/");
            if (values == null) {
                values = namespaces.get(uri + "#");
            }
        }
        return values;
    }

}
