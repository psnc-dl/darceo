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
package pl.psnc.synat.wrdz.mdz.plugin;

import java.io.Serializable;
import java.util.Map;

/**
 * Information about the outcome of the verification process.
 */
public class VerificationResult implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -2422737955761943591L;

    /** User-friendly message. */
    private String message;

    /** Additional data, if needed. */
    private Map<String, Object> data;


    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public Map<String, Object> getData() {
        return data;
    }


    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
