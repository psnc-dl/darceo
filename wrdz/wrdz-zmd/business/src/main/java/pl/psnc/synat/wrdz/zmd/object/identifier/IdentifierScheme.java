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
package pl.psnc.synat.wrdz.zmd.object.identifier;

import pl.psnc.synat.wrdz.zmd.config.ZmdConfiguration;

/**
 * Enum representing digital objects' identifier schemes.
 */
public enum IdentifierScheme {

    /**
     * OAI identifier scheme.
     */
    OAI {

        @Override
        public IdentifierGenerator getGenerator(ZmdConfiguration configuration) {
            return OaiGenerator.getGenerator(configuration);
        }
    },

    /**
     * DOI identifier scheme.
     */
    DOI {

        @Override
        public IdentifierGenerator getGenerator(ZmdConfiguration configuration) {
            return DoiGenerator.getGenerator(configuration);
        }
    },

    /**
     * URL scheme.
     */
    URL {

        @Override
        public IdentifierGenerator getGenerator(ZmdConfiguration configuration) {
            return UrlGenerator.getGenerator(configuration);
        }
    },

    /**
     * Custom identifier scheme.
     */
    CUSTOM {

        @Override
        public IdentifierGenerator getGenerator(ZmdConfiguration configuration) {
            return CustomGenerator.getGenerator(configuration);
        }
    };

    /**
     * Provides generator for a concrete identifier scheme.
     * 
     * @param configuration
     *            ZMD configuration.
     * @return concrete identifier generator.
     */
    public IdentifierGenerator getGenerator(ZmdConfiguration configuration) {
        return null;
    }
}
