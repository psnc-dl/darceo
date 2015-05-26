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
import java.util.Date;

/**
 * Contains information about a single verification plugin execution.
 */
public class PluginExecutionReport implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -5476244117100263858L;

    /** Plugin name. */
    private String pluginName;

    /** Object identifier. */
    private String objectIdentifier;

    /** Execution date. */
    private Date date;

    /** Execution result. */
    private VerificationResult result;


    public String getPluginName() {
        return pluginName;
    }


    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }


    public String getObjectIdentifier() {
        return objectIdentifier;
    }


    public void setObjectIdentifier(String objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
    }


    public Date getDate() {
        return date;
    }


    public void setDate(Date date) {
        this.date = date;
    }


    public VerificationResult getResult() {
        return result;
    }


    public void setResult(VerificationResult result) {
        this.result = result;
    }
}
