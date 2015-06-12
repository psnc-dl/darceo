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
package pl.psnc.synat.wrdz.zmkd.plan.execution;

import pl.psnc.synat.wrdz.ru.composition.TransformationType;
import pl.psnc.synat.wrdz.zmkd.entity.plan.Transformation;
import pl.psnc.synat.wrdz.zmkd.service.ServiceInfoBuilder;

/**
 * Builder of the (@link TransformationInfo).
 */
public class TransformationInfoBuilder extends ServiceInfoBuilder<TransformationInfo> {

    /**
     * Seed for building.
     */
    private final Transformation transformation;


    /**
     * Constructor accepting transformation as a seed.
     * 
     * @param transformation
     *            transformation
     */
    public TransformationInfoBuilder(Transformation transformation) {
        super(TransformationInfo.class);
        this.transformation = transformation;
    }


    /**
     * Builds a transformation.
     * 
     * @return transformation
     */
    public TransformationInfo build() {
        TransformationInfo transformationInfo = super.build();
        transformationInfo.setType(TransformationType.valueOf(transformation.getType().name()));
        transformationInfo.setInputFileFormat(transformation.getInputFileFormat());
        transformationInfo.setOutputFileFormat(transformation.getOutputFileFormat());
        return transformationInfo;
    }

}
