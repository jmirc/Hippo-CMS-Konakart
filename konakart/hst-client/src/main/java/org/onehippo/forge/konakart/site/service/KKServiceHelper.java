/*
 * =========================================================
 * Hippo CMS - Konakart
 * https://bitbucket.org/jmirc/hippo-cms-konakart
 * =========================================================
 * Copyright 2012
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================================================
 */

package org.onehippo.forge.konakart.site.service;

import org.hippoecm.hst.site.HstServices;

public class KKServiceHelper {

    public static Boolean customersShared = false;
    public static Boolean productsShared = false;
    public static Integer engineMode = 0;
    public static Boolean useExternalPrice = false;
    public static Boolean useExternalQuantity = false;

    public static Boolean getCustomersShared() {
        return customersShared;
    }

    public static Boolean getProductsShared() {
        return productsShared;
    }

    public static Integer getEngineMode() {
        return engineMode;
    }

    public static Boolean getUseExternalPrice() {
        return useExternalPrice;
    }

    public static Boolean getUseExternalQuantity() {
        return useExternalQuantity;
    }

    public static KKBasketService getKKBasketService() {
        return HstServices.getComponentManager().getComponent(KKBasketService.class.getName());
    }

    public static KKCartService getKKCartService() {
        return HstServices.getComponentManager().getComponent(KKCartService.class.getName());
    }

    public static KKCookieService getKKCookieService() {
        return HstServices.getComponentManager().getComponent(KKCookieService.class.getName());
    }

    public static KKCustomerService getKKCustomerService() {
        return HstServices.getComponentManager().getComponent(KKCustomerService.class.getName());
    }

    public static KKEngineService getKKEngineService() {
        return HstServices.getComponentManager().getComponent(KKEngineService.class.getName());
    }

    public static KKEventService getKKEventService() {
        return HstServices.getComponentManager().getComponent(KKEventService.class.getName());
    }

    public static KKOrderService getKKOrderService() {
        return HstServices.getComponentManager().getComponent(KKOrderService.class.getName());
    }

}
