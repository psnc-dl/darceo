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

/**
 * Atomic write (only) operation on data storage.
 * 
 */
public class DataStorageWOOperation {

    /**
     * Types of operations.
     */
    public enum Type {
        /** Creating a directory. */
        CREATE_DIRECTORY,
        /** Removing a directory. */
        DELETE_DIRECTORY,
        /** Putting a file. */
        PUT_FILE,
        /** Removing a file. */
        DELETE_FILE;
    }


    /**
     * Creates a new operation.
     * 
     * @param type
     *            type of operation
     * @param path
     *            path associated with the operation
     */
    public DataStorageWOOperation(Type type, String path) {
        this.type = type;
        this.path = path;
    }


    /**
     * Type.
     */
    private Type type;

    /**
     * Path to a file or a folder.
     */
    private String path;


    public Type getType() {
        return type;
    }


    public void setType(Type type) {
        this.type = type;
    }


    public String getPath() {
        return path;
    }


    public void setPath(String path) {
        this.path = path;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("Operation ");
        sb.append("[type = ").append(type);
        sb.append(", path = ").append(path);
        sb.append("]");
        return sb.toString();
    }

}
