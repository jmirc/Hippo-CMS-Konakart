package org.onehippo.forge.konakart.hst.utils;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/**
 * This class is used to offer methods used to interact with Konakart
 * if you decided to not extend the @see KKBaseHstComponent class.
 */
public class KKComponentUtils {

    public static final Logger log = LoggerFactory.getLogger(KKComponentUtils.class);

    /**
     * Set the global konakart attributes to the Hst request
     * @param request the hst request to set
     */
    public static void setGlobalKonakartAttributes(@Nonnull HstRequest request) {
        try {
            // Set the attribut isLogged if the user is a logged user
            request.setAttribute("isLogged", !KKServiceHelper.getKKCustomerService().isGuestCustomer(request));

            // Set the attribut displayPriceWithTax used to display or not the price with or without tax
            request.setAttribute("displayPriceWithTax", KKServiceHelper.getKKBasketService().displayPriceWithTax(request));

            // Set the attibute wishListEnabled. Set to true if the wish list functionality is allowed, false otherwise
            request.setAttribute("wishListEnabled", KKServiceHelper.getKKCustomerService().wishListEnabled(request));

            // Set the default wish list if exists
            request.setAttribute("defaultWishList", KKServiceHelper.getKKCustomerService().getDefaultWishList(request));

            // Set the current customer
            request.setAttribute("currentCustomer", KKServiceHelper.getKKCustomerService().getCurrentCustomer(request));
            request.setAttribute("basketTotal", KKServiceHelper.getKKBasketService().getBasketTotal(request));
        } catch (Exception e) {
            log.warn("Failed to render the HST component {}", e.toString());
        }
    }

    /**
     * Get the current Konakart Product Document
     *
     *
     * @param request  the HST request
     * @return the product document
     * @throws org.hippoecm.hst.core.component.HstComponentException
     *          thrown if the document is not a type of KKProductDocument
     */
    public static KKProductDocument getKKProductDocument(@Nonnull BaseHstComponent hstComponent,
                                                         @Nonnull HstRequest request) throws HstComponentException {



        HippoBean currentBean = hstComponent.getContentBean(request);

        if (currentBean == null) {
            throw new HstComponentException("No document has been found");
        }

        // Not an instance of KKProductdocuemnt
        if (!(currentBean instanceof KKProductDocument)) {
            log.error(currentBean.getClass().getName() + " must extend " + KKProductDocument.class.getName());
            throw new HstComponentException(currentBean.getClass().getName() + " must extend " +
                    KKProductDocument.class.getName());
        }

        KKProductDocument document = (KKProductDocument) currentBean;
        //document.setKkEngine(KKServiceHelper.getKKEngineService().getKKAppEng(request));

        return document;
    }



}
