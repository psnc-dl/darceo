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
package pl.psnc.synat.wrdz.zu.converters;

import javax.faces.convert.EnumConverter;
import javax.faces.convert.FacesConverter;

import pl.psnc.synat.wrdz.zu.types.ManagementPermissionType;

/**
 * Handles ManagementPermissionType <-> String conversion.
 */
@FacesConverter(value = "managementPermissionTypeConverter")
public class ManagementPermissionTypeConverter extends EnumConverter {

    /**
     * Default constructor.
     */
    public ManagementPermissionTypeConverter() {
        super(ManagementPermissionType.class);
    }
}
