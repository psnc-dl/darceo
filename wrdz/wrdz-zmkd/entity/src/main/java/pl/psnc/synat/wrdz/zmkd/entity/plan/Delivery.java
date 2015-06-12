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
package pl.psnc.synat.wrdz.zmkd.entity.plan;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import pl.psnc.synat.wrdz.zmkd.entity.format.FileFormat;

/**
 * Delivery (a service call that allows the client to view the submitted files).
 */
@Entity
@DiscriminatorValue("DELIVERY")
public class Delivery extends Service {

    /** Serial version UID. */
    private static final long serialVersionUID = 1389335477646298788L;

    /** Delivery plan this delivery is a part of. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "D_DELIVERY_PLAN_ID", nullable = false)
    private DeliveryPlan deliveryPlan;

    /** Execution cost. */
    @Column(name = "D_EXECUTION_COST")
    private Integer executionCost;

    /**
     * Input file formats.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ZMKD_DELIVERIES_INPUT_FILE_FORMATS", schema = "darceo", joinColumns = { @JoinColumn(
            name = "D_ID", referencedColumnName = "S_ID") }, inverseJoinColumns = { @JoinColumn(name = "IFF_ID",
            referencedColumnName = "FF_ID") })
    private List<FileFormat> inputFileFormats = new ArrayList<FileFormat>();

    /** Client location. */
    @Column(name = "D_CLIENT_LOCATION", unique = false, nullable = true, length = 255)
    private String clientLocation;


    public DeliveryPlan getDeliveryPlan() {
        return deliveryPlan;
    }


    public void setDeliveryPlan(DeliveryPlan deliveryPlan) {
        this.deliveryPlan = deliveryPlan;
    }


    public Integer getExecutionCost() {
        return executionCost;
    }


    public void setExecutionCost(Integer executionCost) {
        this.executionCost = executionCost;
    }


    public List<FileFormat> getInputFileFormats() {
        return inputFileFormats;
    }


    public void setInputFileFormats(List<FileFormat> inputFileFormats) {
        this.inputFileFormats = inputFileFormats;
    }


    public String getClientLocation() {
        return clientLocation;
    }


    public void setClientLocation(String clientLocation) {
        this.clientLocation = clientLocation;
    }

}
