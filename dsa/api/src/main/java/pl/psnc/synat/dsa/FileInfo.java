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
package pl.psnc.synat.dsa;

import java.util.Date;

/**
 * Provides information about one file in a data storage.
 * 
 */
public class FileInfo {

    /**
     * Types of file system objects.
     */
    public enum Type {
        /** File. */
        FILE,
        /** Directory. */
        DIR
    };


    /**
     * Absolute path to the parent directory.
     */
    private String path;

    /**
     * Name of a file or a directory.
     */
    private String name;

    /**
     * Type.
     */
    private Type type;

    /**
     * Size of the file (for directories it's null).
     */
    private Long size;

    /**
     * Last modified date.
     */
    private Date lastModified;


    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public boolean isFile() {
        return type.equals(Type.FILE);
    }


    public boolean isDirectory() {
        return type.equals(Type.DIR);
    }


    public Type getType() {
        return type;
    }


    public void setType(Type type) {
        this.type = type;
    }


    public Long getSize() {
        return size;
    }


    public void setSize(Long size) {
        this.size = size;
    }


    public Date getLastModified() {
        return lastModified;
    }


    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        FileInfo other = (FileInfo) o;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        return true;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("FileInfo ");
        sb.append("[path = ").append(path);
        sb.append(", name = ").append(name);
        sb.append(", type = ").append(type);
        sb.append(", size = ").append(size);
        sb.append(", lastModified = ").append(lastModified);
        sb.append("]");
        return sb.toString();
    }

}
