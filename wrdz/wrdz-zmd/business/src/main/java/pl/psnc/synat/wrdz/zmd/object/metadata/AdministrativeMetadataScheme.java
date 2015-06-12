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
package pl.psnc.synat.wrdz.zmd.object.metadata;

import pl.psnc.synat.wrdz.zmd.object.metadata.construction.AdministrativeMetadataConstructionStrategy;
import pl.psnc.synat.wrdz.zmd.object.metadata.construction.NullMetadataConstructionStrategy;
import pl.psnc.synat.wrdz.zmd.object.metadata.construction.PremisMetadataConstructionStrategy;

/**
 * Enum representing adminsitrative metadata schemes.
 */
public enum AdministrativeMetadataScheme {

    /**
     * No metadata.
     */
    NONE {

        @Override
        public AdministrativeMetadataConstructionStrategy getStrategy() {
            return new NullMetadataConstructionStrategy();
        }
    },

    /**
     * PREMIS scheme.
     */
    PREMIS {

        @Override
        public AdministrativeMetadataConstructionStrategy getStrategy() {
            return new PremisMetadataConstructionStrategy();
        }
    };

    /**
     * Provides strategy for a concrete administrative matadata scheme.
     * 
     * @return concrete strategy
     */
    public AdministrativeMetadataConstructionStrategy getStrategy() {
        return null;
    }

}
