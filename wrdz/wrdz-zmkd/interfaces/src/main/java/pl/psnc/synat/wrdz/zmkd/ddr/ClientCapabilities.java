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
package pl.psnc.synat.wrdz.zmkd.ddr;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.zmkd.config.Format;
import pl.psnc.synat.wrdz.zmkd.config.Format.FormatType;
import pl.psnc.synat.wrdz.zmkd.config.ZdtConfiguration;

/**
 * Representation of client capabilities. This class is immutable.
 * <p>
 * Formats and plugins included in the respective format support and plugin collections are confirmed to be supported by
 * the client; support for those not included is unknown (they still <strong>might</strong> be supported by the client).
 * <p>
 * All other properties from this class can be <code>null</code>, which means that no data for that particular aspect is
 * available and the actual value of such property is unknown.
 */
public class ClientCapabilities {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(ClientCapabilities.class);

    /** Device vendor. */
    private final String deviceVendor;

    /** Device model. */
    private final String deviceModel;

    /** Display width (in pixels). */
    private final Integer displayWidth;

    /** Display height (in pixels). */
    private final Integer displayHeight;

    /** Display color depth. */
    private final Integer displayColorDepth;

    /** Whether the client's browser supports cookies. */
    private final Boolean cookieSupport;

    /** Whether the client's browser supports javascript. */
    private final Boolean scriptSupport;

    /** Whether the client's browser supports ajax calls. */
    private final Boolean ajaxSupport;

    /** Supported image formats. */
    private final Set<Format> imageFormatSupport;

    /** Supported document formats. */
    private final Set<Format> docFormatSupport;

    /** Supported audio formats. */
    private final Set<Format> audioFormatSupport;

    /** Supported video formats. */
    private final Set<Format> videoFormatSupport;

    /** Supported plugins. */
    private final Set<Plugin> plugins;


    /**
     * Creates a new instance.
     * 
     * @param config
     *            module configuration
     * @param deviceInfo
     *            gathered information about the client's device
     * @param scriptSupport
     *            whether the client's browser supports javascript
     * @param plugins
     *            installed and enabled plugins
     */
    public ClientCapabilities(ZdtConfiguration config, DeviceInfo deviceInfo, boolean scriptSupport,
            List<String> plugins) {

        this.deviceVendor = deviceInfo.getVendor();
        this.deviceModel = deviceInfo.getModel();
        this.displayWidth = deviceInfo.getDisplayWidth();
        this.displayHeight = deviceInfo.getDisplayHeight();
        this.displayColorDepth = deviceInfo.getDisplayColorDepth();

        this.cookieSupport = deviceInfo.getCookieSupport();
        this.ajaxSupport = deviceInfo.getAjaxSupport();
        this.scriptSupport = scriptSupport;

        Set<Format> imageFormats = new HashSet<Format>();
        if (deviceInfo.getImageFormatSupport() != null) {
            for (String formatName : deviceInfo.getImageFormatSupport()) {
                Format format = config.getFormat(formatName);
                if (format != null && format.getType() == FormatType.IMAGE) {
                    imageFormats.add(format);
                } else {
                    logger.warn("Unexpected image format: " + formatName);
                }
            }
        }

        if (plugins != null && !plugins.isEmpty()) {
            EnumSet<Plugin> converted = EnumSet.noneOf(Plugin.class);
            for (String plugin : plugins) {
                try {
                    converted.add(Plugin.valueOf(plugin.toUpperCase(Locale.ENGLISH)));
                } catch (IllegalArgumentException e) {
                    // unexpected value, ignore it
                    logger.warn("Unexpected plugin: " + plugin);
                }
            }
            this.plugins = Collections.unmodifiableSet(converted);
        } else {
            this.plugins = Collections.emptySet();
        }

        Set<Format> audioFormats = new HashSet<Format>();
        Set<Format> docFormats = new HashSet<Format>();
        Set<Format> videoFormats = new HashSet<Format>();

        for (Plugin plugin : this.plugins) {
            Set<Format> formats = config.getFormats(plugin);
            if (formats != null) {
                for (Format format : formats) {
                    switch (format.getType()) {
                        case AUDIO:
                            audioFormats.add(format);
                            break;
                        case DOC:
                            docFormats.add(format);
                            break;
                        case IMAGE:
                            imageFormats.add(format);
                            break;
                        case VIDEO:
                            videoFormats.add(format);
                            break;
                        default:
                            throw new WrdzRuntimeException("Unexpected format type: " + format.getType());
                    }
                }
            }
        }

        this.imageFormatSupport = Collections.unmodifiableSet(imageFormats);
        this.docFormatSupport = Collections.unmodifiableSet(docFormats);
        this.audioFormatSupport = Collections.unmodifiableSet(audioFormats);
        this.videoFormatSupport = Collections.unmodifiableSet(videoFormats);
    }


    public String getDeviceVendor() {
        return deviceVendor;
    }


    public String getDeviceModel() {
        return deviceModel;
    }


    public Integer getDisplayWidth() {
        return displayWidth;
    }


    public Integer getDisplayHeight() {
        return displayHeight;
    }


    public Integer getDisplayColorDepth() {
        return displayColorDepth;
    }


    public Boolean getCookieSupport() {
        return cookieSupport;
    }


    public Boolean getScriptSupport() {
        return scriptSupport;
    }


    public Boolean getAjaxSupport() {
        return ajaxSupport;
    }


    public Set<Format> getImageFormatSupport() {
        return imageFormatSupport;
    }


    public Set<Format> getDocFormatSupport() {
        return docFormatSupport;
    }


    public Set<Format> getAudioFormatSupport() {
        return audioFormatSupport;
    }


    public Set<Format> getVideoFormatSupport() {
        return videoFormatSupport;
    }


    public Set<Plugin> getPlugins() {
        return plugins;
    }
}
