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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Conversion path.
 */
@Entity
@DiscriminatorValue("CONVERSION")
public class ConversionPath extends TransformationPath {

    /** Serial version UID. */
    private static final long serialVersionUID = -8673437709729558553L;

    /** Delivery plan this path is a part of. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CP_DELIVERY_PLAN_ID", nullable = false)
    private DeliveryPlan deliveryPlan;


    public DeliveryPlan getDeliveryPlan() {
        return deliveryPlan;
    }


    public void setDeliveryPlan(DeliveryPlan deliveryPlan) {
        this.deliveryPlan = deliveryPlan;
    }
}
