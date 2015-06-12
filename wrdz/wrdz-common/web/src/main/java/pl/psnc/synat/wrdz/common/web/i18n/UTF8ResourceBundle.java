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
package pl.psnc.synat.wrdz.common.web.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

/**
 * UTF8 resource bundle for WRDZ modules. This class has to be abstract and extended in modules for it to work properly
 * (base class is loaded with a different ClassLoader when deploying the .ear file).
 */
public abstract class UTF8ResourceBundle extends ResourceBundle {

    /**
     * Resource bundle file extension.
     */
    protected static final String BUNDLE_EXTENSION = "properties";

    /**
     * ResourceBundle#Control that creates a bundle from the required property file encoded in UTF-8.
     */
    protected static final Control UTF8_CONTROL = new UTF8Control();


    /**
     * Constructor. Sets the parent resource bundle.
     */
    public UTF8ResourceBundle() {
        setParent(ResourceBundle
                .getBundle(getBundleName(), FacesContext.getCurrentInstance().getViewRoot().getLocale(), this
                        .getClass().getClassLoader(), UTF8_CONTROL));
    }


    @Override
    protected Object handleGetObject(String key) {
        return parent.getObject(key);
    }


    @Override
    public Enumeration<String> getKeys() {
        return parent.getKeys();
    }


    /**
     * The below code is copied from default Control#newBundle() implementation. Only the PropertyResourceBundle line is
     * changed to read the file as UTF-8.
     */
    protected static class UTF8Control extends Control {

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader,
                boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, BUNDLE_EXTENSION);
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, "UTF-8"));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }


    /**
     * Returns the bundle name, used to find the bundle files in the classpath.
     */
    protected abstract String getBundleName();

}
