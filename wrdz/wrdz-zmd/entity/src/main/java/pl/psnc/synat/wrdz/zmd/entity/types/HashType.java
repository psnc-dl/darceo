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
package pl.psnc.synat.wrdz.zmd.entity.types;

import java.math.BigInteger;

/**
 * Represents available hash functions to retrieve and produce file hash.
 */
public enum HashType {
    /**
     * SHA-2 SHA-256 hash algorithm.
     */
    SHA256("SHA-256") {

        @Override
        public String toHexFormat(byte[] hash) {
            return String.format("%064x", new BigInteger(1, hash));
        }

    },
    /**
     * SHA-2 SHA-512 hash algorithm.
     */
    SHA512("SHA-512") {

        @Override
        public String toHexFormat(byte[] hash) {
            return String.format("%0128x", new BigInteger(1, hash));
        }

    };

    /**
     * Stores the algorithm name recognizable by hashing function library.
     */
    private final String algorithmName;


    /**
     * Creates new instance of this class.
     * 
     * @param algorithmName
     *            the algorithm name recognizable by hashing function library.
     */
    HashType(String algorithmName) {
        this.algorithmName = algorithmName;
    }


    public String getAlgorithmName() {
        return algorithmName;
    }


    /**
     * Converts the byte of arrays into more readable hexadecimal format String.
     * 
     * @param hash
     *            array of bytes containing result of hashing
     * @return String hexadecimal representation of the array of bytes.
     */
    public String toHexFormat(byte[] hash) {
        return null;
    }

}
