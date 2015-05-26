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

import pl.psnc.synat.wrdz.zmd.entity.object.migration.Conversion;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Migration;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Optimization;
import pl.psnc.synat.wrdz.zmd.entity.object.migration.Transformation;

/**
 * Enum representing the types of digital object migrations.
 */
public enum MigrationType {
    /**
     * An operation performed on master object causing no loss of any data and resulting in creation of new master
     * object, e.g. when we migrate object's contents to newer formats.
     */
    TRANSFORMATION {

        @SuppressWarnings("rawtypes")
        @Override
        public Migration createNew() {
            return new Transformation();
        }


        @Override
        public ObjectType getOutputType() {
            return ObjectType.MASTER;
        }


        @Override
        public ObjectType[] getInputTypes() {
            return TRANSFORMATION_INPUT;
        }
    },
    /**
     * An operation performed on any type of digital object resulting in production of another converted digital object.
     */
    CONVERSION {

        @SuppressWarnings("rawtypes")
        @Override
        public Migration createNew() {
            return new Conversion();
        }


        @Override
        public ObjectType getOutputType() {
            return ObjectType.CONVERTED;
        }


        @Override
        public ObjectType[] getInputTypes() {
            return CONVERSION_INPUT;
        }
    },
    /**
     * An operation performed on master or optimized object that results in production of new optimized object.
     */
    OPTIMIZATION {

        @SuppressWarnings("rawtypes")
        @Override
        public Migration createNew() {
            return new Optimization();
        }


        @Override
        public ObjectType getOutputType() {
            return ObjectType.OPTIMIZED;
        }


        @Override
        public ObjectType[] getInputTypes() {
            return OPTIMIZATION_INPUT;
        }
    };

    /**
     * Input formats accepted by conversion.
     */
    private static final ObjectType[] CONVERSION_INPUT = { ObjectType.CONVERTED, ObjectType.OPTIMIZED,
            ObjectType.MASTER };

    /**
     * Input formats accepted by transformation.
     */
    private static final ObjectType[] TRANSFORMATION_INPUT = { ObjectType.MASTER };

    /**
     * Input formats accepted by optimization.
     */
    private static final ObjectType[] OPTIMIZATION_INPUT = { ObjectType.OPTIMIZED, ObjectType.MASTER };


    /**
     * Returns new instance of the migration type.
     * 
     * @return new isntance of the appropriate migration type.
     */
    @SuppressWarnings("rawtypes")
    public Migration createNew() {
        return null;
    }


    /**
     * Gets the output format of migration type.
     * 
     * @return type of objects produced as output of the migration type.
     */
    public ObjectType getOutputType() {
        return null;
    }


    /**
     * Gets the input format of migration type.
     * 
     * @return array of types of objects accepted as input to the migration type.
     */
    public ObjectType[] getInputTypes() {
        return null;
    }

}
