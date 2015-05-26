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

import pl.psnc.synat.wrdz.common.metadata.Metadata;

/**
 * Intreface for all technical metadata in a standard schema.
 */
public abstract class TechMetadata extends Metadata {

    /** Serial version UID. */
    private static final long serialVersionUID = 8490217414884374099L;

    /**
     * Type of technical metadata.
     */
    private final TechMetadataType type;


    /**
     * Constructs technical metadata object.
     * 
     * @param metadata
     *            technical metadata
     * @param type
     *            type of technical metadata
     */
    public TechMetadata(String metadata, TechMetadataType type) {
        super(metadata);
        this.type = type;
    }


    @Override
    public TechMetadataType getType() {
        return this.type;
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("TechMetadata ");
        sb.append("[type = ").append(type);
        sb.append(", xml = ").append(xml);
        sb.append("]");
        return sb.toString();
    }

}
