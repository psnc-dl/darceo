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
package pl.psnc.synat.wrdz.zmd.input.object.validation.constraints;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import pl.psnc.synat.wrdz.zmd.input.object.validation.constraints.impl.ObjectModificationQueryValidator;

/**
 * Annotation for object modification request validation.
 */
@Target({ ElementType.TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = ObjectModificationQueryValidator.class)
@Documented
public @interface ValidObjectModificationQuery {

    /**
     * Returns constraint violation message.
     */
    String message() default "{pl.psnc.synat.wrdz.zmd.input.object.validation.constraints.ValidObjectModificationQuery}";


    /**
     * Returns constraint's groups.
     */
    Class<?>[] groups() default {};


    /**
     * Returns constraint's payload.
     */
    Class<? extends Payload>[] payload() default {};

}
