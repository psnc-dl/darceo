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

import pl.psnc.synat.wrdz.common.metadata.MetadataType;

/**
 * Type of administrative metadata.
 */
public class AdmMetadataType implements MetadataType {

    /** Serial version UID. */
    private static final long serialVersionUID = -4849772486458020946L;

    /**
     * Type of metadata.
     */
    private final Type type;


    /**
     * Constructs type of an administrative metadata.
     * 
     * @param type
     *            type
     */
    public AdmMetadataType(Type type) {
        this.type = type;
    }


    /**
     * Enum of metadata types.
     * 
     */
    public enum Type {

        /**
         * Administrative metadata in the Premis schema.
         */
        PREMIS;
    }


    @Override
    public String getName() {
        return type.name().toLowerCase();
    }


    public AdmMetadataType.Type getType() {
        return type;
    }

}
