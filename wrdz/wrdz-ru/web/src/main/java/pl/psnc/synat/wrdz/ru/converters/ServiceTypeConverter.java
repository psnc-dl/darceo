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
package pl.psnc.synat.wrdz.ru.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import pl.psnc.synat.wrdz.common.exception.WrdzRuntimeException;
import pl.psnc.synat.wrdz.ru.entity.types.ServiceType;

/**
 * Converter converting {@link ServiceType} from/to String.
 */
public class ServiceTypeConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        if (arg2 == null) {
            return null;
        }
        if (arg2.equals(ServiceType.DATA_CONVERSION.getDescription())) {
            return ServiceType.DATA_CONVERSION;
        } else if (arg2.equals(ServiceType.DATA_MIGRATION.getDescription())) {
            return ServiceType.DATA_MIGRATION;
        } else if (arg2.equals(ServiceType.ADVANCED_DATA_DELIVERY.getDescription())) {
            return ServiceType.ADVANCED_DATA_DELIVERY;
        } else {
            throw new ConverterException("Cannot convert to service type!");
        }
    }


    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
        try {
            ServiceType type = (ServiceType) arg2;
            if (type == null) {
                return null;
            }
            switch (type) {
                case DATA_CONVERSION:
                    return ServiceType.DATA_CONVERSION.getDescription();
                case DATA_MIGRATION:
                    return ServiceType.DATA_MIGRATION.getDescription();
                case ADVANCED_DATA_DELIVERY:
                    return ServiceType.ADVANCED_DATA_DELIVERY.getDescription();
                default:
                    throw new WrdzRuntimeException("Illegal value of service type.");

            }
        } catch (ClassCastException e) {
            throw new ConverterException(e);
        }
    }
}
