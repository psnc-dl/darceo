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

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.inject.Default;
import javax.inject.Singleton;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzConfigurationError;
import pl.psnc.synat.wrdz.zmkd.config.Format.FormatType;
import pl.psnc.synat.wrdz.zmkd.ddr.Plugin;

/**
 * Singleton of the ZDT configuration of the WRDZ application.
 */
@Singleton
@Default
public class ZdtConfiguration {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ZdtConfiguration.class);

    /** Configuration file. */
    private static final String CONFIG_FILE = "zdt-wrdz-config.xml";

    /** Configuration object. */
    private XMLConfiguration config;

    /** File formats. */
    private Map<String, Format> formatsByName;

    /** File formats supported by known plugins. */
    private Map<Plugin, Set<Format>> formatsByPlugin;


    /**
     * Creates the configuration object form the {@value #CONFIG_FILE} file.
     */
    @PostConstruct
    @SuppressWarnings("unchecked")
    protected void init() {
        try {
            config = new XMLConfiguration(CONFIG_FILE);

            formatsByName = new HashMap<String, Format>();
            formatsByPlugin = new HashMap<Plugin, Set<Format>>();

            List<HierarchicalConfiguration> formats = config.configurationsAt("formats.format");
            for (HierarchicalConfiguration format : formats) {
                String name = format.getString("name");
                FormatType type = FormatType.valueOf(format.getString("type").toUpperCase());
                List<String> iris = format.getList("iri");

                List<String> plugins = format.getList("supported-by.plugin");
                EnumSet<Plugin> supportedBy = EnumSet.noneOf(Plugin.class);
                for (String plugin : plugins) {
                    supportedBy.add(Plugin.valueOf(plugin));
                }

                formatsByName.put(name, new Format(name, type, iris, supportedBy));
            }

            for (Format format : formatsByName.values()) {
                for (Plugin plugin : format.getSupportedBy()) {
                    if (!formatsByPlugin.containsKey(plugin)) {
                        formatsByPlugin.put(plugin, new HashSet<Format>());
                    }
                    formatsByPlugin.get(plugin).add(format);
                }
            }

            for (Entry<Plugin, Set<Format>> entry : formatsByPlugin.entrySet()) {
                entry.setValue(Collections.unmodifiableSet(entry.getValue()));
            }

        } catch (IllegalArgumentException e) {
            logger.error("Loading the configuration failed.", e);
            throw new WrdzConfigurationError(e);
        } catch (ConfigurationException e) {
            logger.error("Loading the configuration failed.", e);
            throw new WrdzConfigurationError(e);
        }
    }


    /**
     * Retrieves a format by its name.
     * 
     * @param name
     *            the name of the format
     * @return the format, or <code>null</code> if no format was found
     */
    public Format getFormat(String name) {
        return formatsByName.get(name);
    }


    /**
     * Retrieves a set of formats supported by the given plugin.
     * 
     * @param plugin
     *            plugin
     * @return a set of formats, or <code>null</code> if no formats are supported by the given plugin
     */
    public Set<Format> getFormats(Plugin plugin) {
        return formatsByPlugin.get(plugin);
    }
}
