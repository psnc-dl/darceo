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
package pl.psnc.synat.wrdz.zu.dto.user;

import java.io.Serializable;

/**
 * Dto for transferring organization information.
 */
public class OrganizationDto implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 5463180168822955121L;

    /** Organization identifier. */
    private long id;

    /** Organization name. */
    private String name;

    /** Organization root path. */
    private String rootPath;


    /**
     * Default constructor.
     */
    public OrganizationDto() {
        // default constructor
    }


    /**
     * Convenience constructor.
     * 
     * @param id
     *            organization identifier
     * @param name
     *            organization name
     * @param rootPath
     *            organization root path
     */
    public OrganizationDto(long id, String name, String rootPath) {
        this.id = id;
        this.name = name;
        this.rootPath = rootPath;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getRootPath() {
        return rootPath;
    }


    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }
}
