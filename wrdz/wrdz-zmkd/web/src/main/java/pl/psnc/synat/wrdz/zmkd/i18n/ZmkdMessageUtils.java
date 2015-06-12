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
package pl.psnc.synat.wrdz.zmkd.i18n;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

/**
 * Utility class for i18n of ZMKD.
 */
public final class ZmkdMessageUtils {

    /**
     * Private constructor.
     */
    private ZmkdMessageUtils() {
    }


    /**
     * Gets a message from the resource bundle for the specified key.
     * 
     * @param key
     *            message key
     * @return message
     */
    public static String getMessage(String key) {
        String text = null;
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("pl.psnc.synat.wrdz.zmkd.i18n.ZmkdResourceBundle",
                FacesContext.getCurrentInstance().getViewRoot().getLocale());
            text = bundle.getString(key);
        } catch (MissingResourceException e) {
            text = "???" + key + "???";
        }
        return text;
    }


    /**
     * Gets a message with arguments from the resource bundle for the specified key.
     * 
     * @param key
     *            message key
     * @param arguments
     *            array with message's arguments
     * @return message
     */
    public static String getMessage(String key, Object... arguments) {
        String pattern = getMessage(key);
        String text = null;
        try {
            MessageFormat formatter = new MessageFormat(pattern, FacesContext.getCurrentInstance().getViewRoot()
                    .getLocale());
            text = formatter.format(arguments);
        } catch (IllegalArgumentException e) {
            text = "???" + pattern + "???";
        }
        return text;
    }

}
