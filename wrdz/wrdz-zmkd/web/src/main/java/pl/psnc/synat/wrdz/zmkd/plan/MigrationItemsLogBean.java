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

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.apache.commons.lang.StringUtils;
import org.richfaces.model.Filter;

import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationItemLog;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationItemStatus;
import pl.psnc.synat.wrdz.zmkd.entity.plan.MigrationPlan;
import pl.psnc.synat.wrdz.zmkd.session.SessionBean;

/**
 * Bean responsible for viewing migration items log.
 */
@ManagedBean
@ViewScoped
public class MigrationItemsLogBean {

    /**
     * Migration plan manager EJB.
     */
    @EJB
    private MigrationPlanManager migrationPlanManager;

    /**
     * Session bean.
     */
    @ManagedProperty(value = "#{sessionBean}")
    private SessionBean sessionBean;

    /**
     * Status filter selected value.
     */
    private String selectedStatus = "";

    /**
     * Available status filter values.
     */
    private List<String> statuses = new ArrayList<String>();

    /**
     * Current page number.
     */
    private int page = 1;


    /**
     * Default constructor of bean.
     */
    public MigrationItemsLogBean() {
        statuses.add("");
        for (MigrationItemStatus status : MigrationItemStatus.values()) {
            statuses.add(status.toString());
        }
    }


    /**
     * Returns list of migration items log based on migration plan id stored in user session.
     * 
     * @return list of migration items log
     */
    public List<MigrationItemLog> getMigrationItemsLog() {

        List<MigrationItemLog> items = new ArrayList<MigrationItemLog>();

        if (sessionBean.getCurrentMigrationPlanId() == 0) {
            return items;
        }

        try {
            MigrationPlan plan = migrationPlanManager.getMigrationPlanById(sessionBean.getCurrentMigrationPlanId());
            return plan.getMigrationItems();
        } catch (MigrationPlanNotFoundException ex) {
            // do nothing and return empty list
        }

        return items;

    }


    public void setSessionBean(SessionBean bean) {
        this.sessionBean = bean;
    }


    public SessionBean getSessionBean() {
        return sessionBean;
    }


    public String getSelectedStatus() {
        return selectedStatus;
    }


    public void setSelectedStatus(String status) {
        this.selectedStatus = status;
    }


    public List<String> getStatuses() {
        return statuses;
    }


    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }


    public int getPage() {
        return page;
    }


    public void setPage(int page) {
        this.page = page;
    }


    /**
     * Returns status filter of migration items.
     * 
     * @return status filter object.
     */
    public Filter<MigrationItemLog> getStatusFilter() {
        return new Filter<MigrationItemLog>() {

            @Override
            public boolean accept(MigrationItemLog item) {

                return StringUtils.isEmpty(selectedStatus) ? true : item.getStatus().equals(
                    MigrationItemStatus.valueOf(selectedStatus));
            }
        };
    }
}
