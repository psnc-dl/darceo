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

import pl.psnc.synat.wrdz.zmd.entity.object.ConvertedObject;
import pl.psnc.synat.wrdz.zmd.entity.object.DigitalObject;
import pl.psnc.synat.wrdz.zmd.entity.object.MasterObject;
import pl.psnc.synat.wrdz.zmd.entity.object.OptimizedObject;

/**
 * Enum representing the types of digital objects.
 */
public enum ObjectType {
    /**
     * Object that is a result of digitalization or transformation performed on it's parent master object and is stored
     * in a lossless format.
     */
    MASTER {

        @Override
        public DigitalObject createNew() {
            return new MasterObject();
        }
    },
    /**
     * Object that is a result of optimization of master object and is stored in a lossless format. Optimized version of
     * the object is basically a master version with slight modifications of meaningless data e.g. excessive image
     * borders from the scanner or rotation of the image etc.
     */
    OPTIMIZED {

        @Override
        public DigitalObject createNew() {
            return new OptimizedObject();
        }
    },
    /**
     * Object that is a presentation version and uses lossy compression of data.
     */
    CONVERTED {

        @Override
        public DigitalObject createNew() {
            return new ConvertedObject();
        }
    };

    /**
     * Returns new instance of the object type.
     * 
     * @return new instance of the appropriate object type.
     */
    public DigitalObject createNew() {
        return null;
    }
}
