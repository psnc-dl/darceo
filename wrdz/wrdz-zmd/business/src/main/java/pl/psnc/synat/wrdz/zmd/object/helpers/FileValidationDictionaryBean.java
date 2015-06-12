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
package pl.psnc.synat.wrdz.zmd.object.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.psnc.synat.wrdz.zmd.dao.object.validation.ValidationWarningDao;
import pl.psnc.synat.wrdz.zmd.dao.object.validation.ValidationWarningFilterFactory;
import pl.psnc.synat.wrdz.zmd.entity.object.validation.ValidationWarning;

/**
 * Provides access and basic operation on file status dictionary.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class FileValidationDictionaryBean {

    /** Logger. */
    private static final Logger logger = LoggerFactory.getLogger(FileValidationDictionaryBean.class);

    /**
     * Max length of warning.
     */
    private static final int MAX_WARNING_LENGTH = 512;
    /**
     * Validation warnings DAO.
     */
    @EJB
    private ValidationWarningDao validationWarningDao;


    /**
     * Checks if there is in the dictionary file validation message corresponding to the messages of validation
     * performed in the technical metadata extraction process and if not it creates it. At the end it returns these
     * corresponding messages from dictionary.
     * 
     * @param messages
     *            messages of validation performed in the technical metadata extraction process
     * @return corresponding validation warnings from the dictionary or null
     */
    public List<ValidationWarning> getFileValidationMessages(Set<String> messages) {
        if (messages == null || messages.size() == 0) {
            return null;
        }
        List<ValidationWarning> warnings = new ArrayList<ValidationWarning>();
        for (String message : messages) {
            message = message.substring(0,
                message.length() > MAX_WARNING_LENGTH ? MAX_WARNING_LENGTH : message.length());
            ValidationWarningFilterFactory queryFilterFactory = validationWarningDao.createQueryModifier()
                    .getQueryFilterFactory();
            ValidationWarning existingWalidationWarning = validationWarningDao.findFirstResultBy(queryFilterFactory
                    .byWarning(message));
            if (existingWalidationWarning != null) {
                logger.debug("warning " + message + " exists in the dictionary - id: "
                        + existingWalidationWarning.getId());
                warnings.add(existingWalidationWarning);
            } else {
                logger.debug("warning " + message + " does not exist in the dictionary");
                ValidationWarning newWalidationWarning = new ValidationWarning(message);
                validationWarningDao.persist(newWalidationWarning);
                warnings.add(newWalidationWarning);
            }
        }
        return warnings;
    }

}
