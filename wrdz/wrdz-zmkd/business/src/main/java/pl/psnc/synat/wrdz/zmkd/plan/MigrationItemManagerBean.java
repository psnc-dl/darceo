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
package pl.psnc.synat.wrdz.zmkd.plan;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import pl.psnc.synat.wrdz.common.dao.QueryFilter;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationItemLogDao;
import pl.psnc.synat.wrdz.zmkd.dao.plan.MigrationItemLogFilterFactory;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationItemLog;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationItemStatus;

/**
 * Default implementation of migration item manager.
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class MigrationItemManagerBean implements MigrationItemManager {

    /** Migration item log bean. */
    @EJB
    private MigrationItemLogDao migrationItemLogDao;


    @Override
    public void logMigrationStarted(long planId, String objectIdentifier) {

        MigrationItemLog item = get(planId, objectIdentifier);

        if (item != null) {
            item.setStatus(MigrationItemStatus.IN_PROGRESS);
            item.setStartedOn(new Date());
        }
    }


    @Override
    public void logUploaded(long planId, String objectIdentifier, String requestId) {

        MigrationItemLog item = get(planId, objectIdentifier);

        if (item != null) {
            item.setStatus(MigrationItemStatus.UPLOADED);
            item.setRequestId(requestId);
        }
    }


    @Override
    public void logCreationSuccessful(String requestId) {

        MigrationItemLog item = get(requestId);

        if (item != null) {
            item.setStatus(MigrationItemStatus.DONE);
            item.setEndedOn(new Date());
        }
    }


    @Override
    public void logPermissionError(long planId, String objectIdentifier, String errorMessageParams) {

        MigrationItemLog item = get(planId, objectIdentifier);

        if (item != null) {
            item.setStatus(MigrationItemStatus.ERROR_PERMISSIONS);
            item.setErrorMessageParams(errorMessageParams);
            item.setEndedOn(new Date());
        }
    }


    @Override
    public void logFetchingError(long planId, String objectIdentifier, String errorMessageParams) {

        MigrationItemLog item = get(planId, objectIdentifier);

        if (item != null) {
            item.setStatus(MigrationItemStatus.ERROR_FETCHING);
            item.setErrorMessageParams(errorMessageParams);
            item.setEndedOn(new Date());
        }
    }


    @Override
    public void logServiceError(long planId, String objectIdentifier, String errorMessageParams) {

        MigrationItemLog item = get(planId, objectIdentifier);

        if (item != null) {
            item.setStatus(MigrationItemStatus.ERROR_SERVICE);
            item.setErrorMessageParams(errorMessageParams);
            item.setEndedOn(new Date());
        }
    }


    @Override
    public void logCreationError(long planId, String objectIdentifier, String errorMessageParams) {

        MigrationItemLog item = get(planId, objectIdentifier);

        if (item != null) {
            item.setStatus(MigrationItemStatus.ERROR_CREATION);
            item.setErrorMessageParams(errorMessageParams);
            item.setEndedOn(new Date());
        }
    }


    @Override
    public void logCreationError(String requestId, String errorMessageParams) {

        MigrationItemLog item = get(requestId);

        if (item != null) {
            item.setStatus(MigrationItemStatus.ERROR_CREATION);
            item.setErrorMessageParams(errorMessageParams);
            item.setEndedOn(new Date());
        }
    }


    @Override
    public void logError(long planId, String objectIdentifier, String errorMessageParams) {

        MigrationItemLog item = get(planId, objectIdentifier);

        if (item != null) {
            item.setStatus(MigrationItemStatus.ERROR);
            item.setErrorMessageParams(errorMessageParams);
            item.setEndedOn(new Date());
        }
    }


    /**
     * Retrieves the migration item that is a part of the given plan and concerns the given object.
     * 
     * @param planId
     *            migration plan id
     * @param objectIdentifier
     *            digital object id
     * @return found migration item
     */
    private MigrationItemLog get(long planId, String objectIdentifier) {
        MigrationItemLogFilterFactory filterFactory = migrationItemLogDao.createQueryModifier().getQueryFilterFactory();

        QueryFilter<MigrationItemLog> filter = filterFactory.and(filterFactory.byMigrationPlan(planId),
            filterFactory.byObjectIdentifier(objectIdentifier));

        return migrationItemLogDao.findFirstResultBy(filter);
    }


    /**
     * Retrieves the migration item that sent the given creation request.
     * 
     * @param requestId
     *            creation request identifier
     * @return found migration item
     */
    private MigrationItemLog get(String requestId) {
        MigrationItemLogFilterFactory filterFactory = migrationItemLogDao.createQueryModifier().getQueryFilterFactory();

        return migrationItemLogDao.findFirstResultBy(filterFactory.byRequestId(requestId));
    }
}
