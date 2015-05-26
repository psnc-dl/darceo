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
package pl.psnc.synat.wrdz.zmd.dto.format;

import java.io.Serializable;
import java.util.List;

/**
 * Dto for transferring data file format information.
 */
public class DataFileFormatDto implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 7598907492558753730L;

    /** PRONOM identifier. */
    private String puid;

    /** Version of the format. */
    private String version;

    /** Format names. */
    private List<String> names;


    /**
     * Default constructor.
     */
    public DataFileFormatDto() {
        // default constructor
    }


    /**
     * Convenience constructor.
     * 
     * @param puid
     *            PRONOM identifier
     * @param version
     *            format version
     * @param names
     *            list of format names
     */
    public DataFileFormatDto(String puid, String version, List<String> names) {
        this.puid = puid;
        this.version = version;
        this.names = names;
    }


    public String getPuid() {
        return puid;
    }


    public void setPuid(String puid) {
        this.puid = puid;
    }


    public String getVersion() {
        return version;
    }


    public void setVersion(String version) {
        this.version = version;
    }


    public List<String> getNames() {
        return names;
    }


    public void setNames(List<String> names) {
        this.names = names;
    }
}
