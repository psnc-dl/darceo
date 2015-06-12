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
import java.util.Set;

/**
 * Information about an access device retrieved from OpenDDR. This class is immutable.
 * <p>
 * All properties, <strong>including collections</strong>, can be <code>null</code>, which means that no data for that
 * particular aspect is available in the OpenDDR and the actual value of such property is unknown.
 */
public class DeviceInfo {

    /** Device vendor. */
    private final String vendor;

    /** Device model. */
    private final String model;

    /** Display width (in pixels). */
    private final Integer displayWidth;

    /** Display height (in pixels). */
    private final Integer displayHeight;

    /** Display color depth (in pixels). */
    private final Integer displayColorDepth;

    /** Whether the device's browser supports cookies. */
    private final Boolean cookieSupport;

    /** Whether the device's browser supports ajax calls. */
    private final Boolean ajaxSupport;

    /** Supported image formats. */
    private final Set<String> imageFormatSupport;


    /**
     * Constructor.
     * 
     * @param builder
     *            populated builder instance
     */
    protected DeviceInfo(DeviceInfoBuilder builder) {
        this.vendor = builder.vendor;
        this.model = builder.model;
        this.displayWidth = builder.displayWidth;
        this.displayHeight = builder.displayHeight;
        this.displayColorDepth = builder.displayColorDepth;
        this.cookieSupport = builder.cookieSupport;
        this.ajaxSupport = builder.ajaxSupport;

        if (builder.imageFormatSupport != null) {
            this.imageFormatSupport = Collections.unmodifiableSet(builder.imageFormatSupport);
        } else {
            this.imageFormatSupport = null;
        }
    }


    public String getVendor() {
        return vendor;
    }


    public String getModel() {
        return model;
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


    public Boolean getAjaxSupport() {
        return ajaxSupport;
    }


    public Set<String> getImageFormatSupport() {
        return imageFormatSupport;
    }


    /**
     * Builder for DeviceInfo.
     */
    public static class DeviceInfoBuilder {

        /** Device vendor. */
        private String vendor;

        /** Device model. */
        private String model;

        /** Display width (in pixels). */
        private Integer displayWidth;

        /** Display height (in pixels). */
        private Integer displayHeight;

        /** Display color depth (in pixels). */
        private Integer displayColorDepth;

        /** Whether the device's browser supports cookies. */
        private Boolean cookieSupport;

        /** Whether the device's browser supports ajax calls. */
        private Boolean ajaxSupport;

        /** Supported image formats. */
        private Set<String> imageFormatSupport;


        /**
         * Constructs the DeviceInfo instance.
         * 
         * @return device info
         */
        public DeviceInfo build() {
            return new DeviceInfo(this);
        }


        /**
         * Sets the device vendor.
         * 
         * @param vendor
         *            device vendor
         * @return this builder
         */
        public DeviceInfoBuilder vendor(String vendor) {
            this.vendor = vendor;
            return this;
        }


        /**
         * Sets the device model.
         * 
         * @param model
         *            device model
         * @return this builder
         */
        public DeviceInfoBuilder model(String model) {
            this.model = model;
            return this;
        }


        /**
         * Sets the display width.
         * 
         * @param displayWidth
         *            display width
         * @return this builder
         */
        public DeviceInfoBuilder displayWidth(Integer displayWidth) {
            this.displayWidth = displayWidth;
            return this;
        }


        /**
         * Sets the display height.
         * 
         * @param displayHeight
         *            display height
         * @return this builder
         */
        public DeviceInfoBuilder displayHeight(Integer displayHeight) {
            this.displayHeight = displayHeight;
            return this;
        }


        /**
         * Sets the display color depth.
         * 
         * @param displayColorDepth
         *            display color depth
         * @return this builder
         */
        public DeviceInfoBuilder displayColorDepth(Integer displayColorDepth) {
            this.displayColorDepth = displayColorDepth;
            return this;
        }


        /**
         * Sets the cookie support.
         * 
         * @param cookieSupport
         *            cookie support
         * @return this builder
         */
        public DeviceInfoBuilder cookieSupport(Boolean cookieSupport) {
            this.cookieSupport = cookieSupport;
            return this;
        }


        /**
         * Sets the ajax support.
         * 
         * @param ajaxSupport
         *            ajax support
         * @return this builder
         */
        public DeviceInfoBuilder ajaxSupport(Boolean ajaxSupport) {
            this.ajaxSupport = ajaxSupport;
            return this;
        }


        /**
         * Sets the image format support.
         * 
         * @param imageFormats
         *            image format support
         * @return this builder
         */
        public DeviceInfoBuilder imageFormatSupport(Set<String> imageFormats) {
            this.imageFormatSupport = imageFormats;
            return this;
        }

    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("DeviceInfo ");
        builder.append("[vendor=").append(vendor);
        builder.append(", model=").append(model);
        builder.append(", displayWidth=").append(displayWidth);
        builder.append(", displayHeight=").append(displayHeight);
        builder.append(", displayColorDepth=").append(displayColorDepth);
        builder.append(", cookieSupport=").append(cookieSupport);
        builder.append(", ajaxSupport=").append(ajaxSupport);
        builder.append(", imageFormatSupport=").append(imageFormatSupport);
        builder.append("]");
        return builder.toString();
    }
}
