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
package pl.psnc.synat.wrdz.zmd.dto.object;

import java.io.Serializable;

import pl.psnc.synat.wrdz.zmd.entity.types.HashType;

/**
 * Dto for transferring data and metadata file hash information.
 */
public class FileHashDto implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = -4945109705959406012L;

    /** Object-relative path of the file. */
    private String objectFilepath;

    /** Type of hash / hashing algorithm. */
    private HashType hashType;

    /** Hash value. */
    private String hashValue;


    /**
     * Default constructor.
     */
    public FileHashDto() {
        // default constructor
    }


    /**
     * Convenience constructor.
     * 
     * @param objectFilePath
     *            object-relative path of the file
     * @param hashType
     *            type of hash / hashing algorithm
     * @param hashValue
     *            hash value
     */
    public FileHashDto(String objectFilePath, HashType hashType, String hashValue) {
        this.objectFilepath = objectFilePath;
        this.hashType = hashType;
        this.hashValue = hashValue;
    }


    public String getObjectFilepath() {
        return objectFilepath;
    }


    public void setObjectFilepath(String objectFilepath) {
        this.objectFilepath = objectFilepath;
    }


    public HashType getHashType() {
        return hashType;
    }


    public void setHashType(HashType hashType) {
        this.hashType = hashType;
    }


    public String getHashValue() {
        return hashValue;
    }


    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }
}
