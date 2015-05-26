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
package pl.psnc.synat.wrdz.ms.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import pl.psnc.synat.wrdz.mdz.config.MdzConfigurationBrowser;

/**
 * Bean that handles MDZ configuration.
 */
@ManagedBean
@ViewScoped
public class MdzConfigurationBean {

    /** Configuration summary. */
    private Map<String, String> summary;

    /** Configuration browser. */
    @EJB(name = "MdzConfigurationBrowser")
    private transient MdzConfigurationBrowser browser;


    /**
     * Refreshes the currently cached configuration summary.
     */
    public void refresh() {
        summary = browser.getSummary();
    }


    /**
     * Returns the configuration summary.
     * 
     * @return a list of data manipulation services
     */
    public Map<String, String> getSummary() {
        if (summary == null) {
            refresh();
        }
        return summary;
    }


    /**
     * Returns a list of summary keys (labels).
     * 
     * @return a list of summary keys
     */
    public List<String> getSummaryKeys() {
        if (summary == null) {
            refresh();
        }
        return new ArrayList<String>(summary.keySet());
    }
}
