﻿/**
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

import pl.psnc.synat.wrdz.zmkd.entity.plan.Delivery;
import pl.psnc.synat.wrdz.zmkd.service.ServiceInfoBuilder;

/**
 * Builder of the (@link {@link DeliveryInfo}).
 */
public class DeliveryInfoBuilder extends ServiceInfoBuilder<DeliveryInfo> {

    /**
     * Seed for building.
     */
    private final Delivery delivery;


    /**
     * Constructor accepting delivery as a seed.
     * 
     * @param delivery
     *            delivery
     */
    public DeliveryInfoBuilder(Delivery delivery) {
        super(DeliveryInfo.class);
        this.delivery = delivery;
    }


    /**
     * Builds a delivery.
     * 
     * @return delivery
     */
    public DeliveryInfo build() {
        DeliveryInfo deliveryInfo = super.build();
        deliveryInfo.setInputFileFormats(delivery.getInputFileFormats());
        return deliveryInfo;
    }

}