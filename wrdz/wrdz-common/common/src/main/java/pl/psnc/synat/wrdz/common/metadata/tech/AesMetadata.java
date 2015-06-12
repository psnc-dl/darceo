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
package pl.psnc.synat.wrdz.common.metadata.tech;

/**
 * Techincal metadata for audio in the AES schema.
 */
public class AesMetadata extends TechMetadata {

    /** Serial version UID. */
    private static final long serialVersionUID = -3362981893875313810L;


    /**
     * Constructs technical metadata object in the AES schema (Audio Engineering Society AES-X098B).
     * 
     * @param metadata
     *            technical metadata
     */
    public AesMetadata(String metadata) {
        super(metadata, new TechMetadataType(TechMetadataType.Type.AES));
    }

}
