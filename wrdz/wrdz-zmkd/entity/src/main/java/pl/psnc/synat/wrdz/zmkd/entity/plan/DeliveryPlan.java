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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.PrivateOwned;

/**
 * Delivery plan.
 */
@Entity
@Table(name = "ZMKD_DELIVERY_PLANS", schema = "darceo")
public class DeliveryPlan implements Serializable {

    /** Serial version UID. */
    private static final long serialVersionUID = 7851863573210829492L;

    /** Random number generator. */
    private static final Random RANDOM = new Random();

    /** Plan identifier (primary key). */
    @Id
    @Column(name = "DP_ID", length = 63, unique = true, nullable = false)
    private String id;

    /** Digital object this plan applies to. */
    @Column(name = "DP_OBJECT_IDENTIFIER", nullable = false)
    private String objectIdentifier;

    /** Delivery service. */
    @OneToOne(fetch = FetchType.LAZY, mappedBy = "deliveryPlan", cascade = { CascadeType.ALL })
    private Delivery delivery;

    /** Conversion paths. */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "deliveryPlan", cascade = { CascadeType.ALL })
    @PrivateOwned
    private List<ConversionPath> conversionPaths = new ArrayList<ConversionPath>();

    /** Status. */
    @Enumerated(EnumType.STRING)
    @Column(name = "DP_STATUS", nullable = false, length = 20)
    private DeliveryPlanStatus status;


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getObjectIdentifier() {
        return objectIdentifier;
    }


    public void setObjectIdentifier(String objectIdentifier) {
        this.objectIdentifier = objectIdentifier;
    }


    public Delivery getDelivery() {
        return delivery;
    }


    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }


    public List<ConversionPath> getConversionPaths() {
        return conversionPaths;
    }


    public void setConversionPaths(List<ConversionPath> conversionPaths) {
        this.conversionPaths = conversionPaths;
    }


    public DeliveryPlanStatus getStatus() {
        return status;
    }


    public void setStatus(DeliveryPlanStatus status) {
        this.status = status;
    }


    /**
     * Generates the unique identifier if not yet set.
     */
    @PrePersist
    protected void generateId() {
        if (id == null) {
            id = "" + System.currentTimeMillis() + Math.abs(Thread.currentThread().getName().hashCode())
                    + Math.abs(RANDOM.nextLong());
        }
    }
}
