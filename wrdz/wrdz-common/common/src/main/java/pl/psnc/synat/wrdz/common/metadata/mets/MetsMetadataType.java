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
package pl.psnc.synat.wrdz.common.metadata.mets;

import pl.psnc.synat.wrdz.common.metadata.MetadataType;

/**
 * METS metadata type.
 */
public class MetsMetadataType implements MetadataType {

    /** Serial version UID. */
    private static final long serialVersionUID = 8146189157451625651L;


    /**
     * Constructs type METS metadata.
     */
    public MetsMetadataType() {
    }


    @Override
    public String getName() {
        return "mets";
    }

}
