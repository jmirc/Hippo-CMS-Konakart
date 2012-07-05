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
