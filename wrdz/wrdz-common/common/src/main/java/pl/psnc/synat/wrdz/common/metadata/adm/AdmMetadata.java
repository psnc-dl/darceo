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
package pl.psnc.synat.wrdz.common.metadata.adm;

import pl.psnc.synat.wrdz.common.metadata.Metadata;

/**
 * Interface for all administrative metadata in a standard schema.
 */
public abstract class AdmMetadata extends Metadata {

    /** Serial version UID. */
    private static final long serialVersionUID = -1775412773552864604L;

    /**
     * Type of administrative metadata.
     */
    private final AdmMetadataType type;


    /**
     * Constructs administrative metadata object.
     * 
     * @param metadata
     *            administrative metadata
     * @param type
     *            type of administrative metadata
     */
    public AdmMetadata(String metadata, AdmMetadataType type) {
        super(metadata);
        this.type = type;
    }


    @Override
    public AdmMetadataType getType() {
        return this.type;
    }

}
