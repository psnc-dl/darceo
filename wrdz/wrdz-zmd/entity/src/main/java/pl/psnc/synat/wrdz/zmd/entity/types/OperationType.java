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

import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Creation;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Deletion;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Modification;
import pl.psnc.synat.wrdz.zmd.entity.object.metadata.operation.Operation;

/**
 * Enum representing the types of operations performed on digital objects' metadata.
 */
public enum OperationType {
    /**
     * Operation of digital object's metadata creation.
     */
    CREATION {

        @Override
        public Creation createNew() {
            return new Creation();
        }
    },
    /**
     * Operation of digital object's metadata creation.
     */
    MODIFICATION {

        @Override
        public Modification createNew() {
            return new Modification();
        }
    },
    /**
     * Operation of digital object's metadata deletion.
     */
    DELETION {

        @Override
        public Deletion createNew() {
            return new Deletion();
        }
    };

    /**
     * Returns new instance of the operation type.
     * 
     * @return new instance of the appropriate object's metadata operation type.
     */
    public Operation createNew() {
        return null;
    }

}
