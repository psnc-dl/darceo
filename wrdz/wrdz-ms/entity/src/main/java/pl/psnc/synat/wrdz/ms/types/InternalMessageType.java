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
package pl.psnc.synat.wrdz.ms.types;

/**
 * Represents the type of the internal message.
 */
public enum InternalMessageType {

    /** Notification about a format that is at risk of becoming outdated and requires migration. */
    FORMAT_AT_RISK("Format at risk"),

    /** Notification about an object that didn't pass the integrity check. */
    CORRUPTED_OBJECT("Corrupted object"),

    /** Notification about a certificate that is close to expiration or has already expired. */
    CERTIFICATE_EXPIRATION_WARNING("Certificate expiration warning");

    /** Type label. */
    private String label;


    /**
     * Constructor.
     * 
     * @param label
     *            type label
     */
    private InternalMessageType(String label) {
        this.label = label;
    }


    public String getLabel() {
        return label;
    }
}
