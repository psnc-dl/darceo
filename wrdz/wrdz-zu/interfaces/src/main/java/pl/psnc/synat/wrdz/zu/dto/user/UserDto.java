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
 * Dto for transferring user information.
 */
public class UserDto implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 8520995300720294261L;

    /** User's identifier. */
    private long id;

    /** User's username in WRDZ. */
    private String username;

    /** Home directory. */
    private String homeDir;


    /**
     * Default constructor.
     */
    public UserDto() {
        // default constructor
    }


    /**
     * Convenience constructor.
     * 
     * @param id
     *            user's identifier
     * @param username
     *            user's username
     * @param homeDir
     *            home directory
     */
    public UserDto(long id, String username, String homeDir) {
        this.id = id;
        this.username = username;
        this.homeDir = homeDir;
    }


    public long getId() {
        return id;
    }


    public void setId(long id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getHomeDir() {
        return homeDir;
    }


    public void setHomeDir(String homeDir) {
        this.homeDir = homeDir;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((homeDir == null) ? 0 : homeDir.hashCode());
        result = prime * result + (int) (id ^ (id >>> 32));
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UserDto other = (UserDto) obj;
        if (homeDir == null) {
            if (other.homeDir != null) {
                return false;
            }
        } else if (!homeDir.equals(other.homeDir)) {
            return false;
        }
        if (id != other.id) {
            return false;
        }
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
        return true;
    }

}
