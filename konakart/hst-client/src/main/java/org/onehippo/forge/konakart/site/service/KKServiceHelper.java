package org.onehippo.forge.konakart.site.service;

import org.hippoecm.hst.site.HstServices;

public class KKServiceHelper {

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

    public static KKReviewService getKKReviewService() {
        return HstServices.getComponentManager().getComponent(KKReviewService.class.getName());
    }

}
