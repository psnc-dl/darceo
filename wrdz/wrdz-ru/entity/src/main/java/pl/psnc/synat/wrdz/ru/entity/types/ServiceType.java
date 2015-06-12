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
package pl.psnc.synat.wrdz.ru.entity.types;

/**
 * Data manipulation services types.
 */
public enum ServiceType {

    /**
     * Data Migration DataManipulationService, performing lossless format change.
     */
    DATA_MIGRATION {

        @Override
        public String getDescription() {
            return "data migration";
        }
    },
    /**
     * Data Conversion DataManipulationService, performing lossy format change.
     */
    DATA_CONVERSION {

        @Override
        public String getDescription() {
            return "data conversion";
        }
    },
    /**
     * Advanced Data Delivery DataManipulationService, performing advanced data delivery.
     */
    ADVANCED_DATA_DELIVERY {

        @Override
        public String getDescription() {
            return "advanced data delivery";
        }
    };

    /**
     * Gets a descriptive name of enum constant.
     * 
     * @return descriptive name of enum constant.
     */
    public abstract String getDescription();
}
