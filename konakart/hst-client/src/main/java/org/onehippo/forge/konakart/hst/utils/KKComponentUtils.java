package org.onehippo.forge.konakart.hst.utils;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryManager;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.query.filter.Filter;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.components.KKCheckout;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Enumeration;

/**
 * This class is used to offer methods used to interact with Konakart
 * if you decided to not extend the @see KKBaseHstComponent class.
 */
public class KKComponentUtils {

    public static final Logger log = LoggerFactory.getLogger(KKComponentUtils.class);

    /**
     * Set the global konakart attributes to the Hst request
     *
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
     * Set the checkout attributes to the Hst request
     *
     * @param request the hst request to set
     */
    public static void setCheckoutAttributes(@Nonnull HstRequest request) {

        // Set the checkout order
        request.setAttribute(KKCheckout.CHECKOUT_ORDER, request.getRequestContext().getAttribute(KKCheckout.CHECKOUT_ORDER));

        // Set the
        Enumeration<String> attributes = request.getRequestContext().getAttributeNames();

        while (attributes.hasMoreElements()) {
            String attributeName = attributes.nextElement();

            if (StringUtils.contains(attributeName, "_EDIT")) {
                request.setAttribute(attributeName, request.getRequestContext().getAttribute(attributeName));
            }
        }
    }

    /**
     * Get the current Konakart Product Document
     *
     * @param request the HST request
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

        return (KKProductDocument) currentBean;
    }

    /**
     * Find and retrieve the associated KKProductDoucment from a product id.
     *
     * @param request   the Hst Request
     * @param productId id of the Konakart product to find
     * @return the Hippo Bean
     */
    public static KKProductDocument getProductDocumentById(HstRequest request, int productId) {

        HippoBean scope = KKUtil.getSiteContentBaseBean(request);


        try {
            HstQueryManager queryManager = KKUtil.getQueryManager(request.getRequestContext());

            HstQuery hstQuery = queryManager.createQuery(scope, KKProductDocument.class);
            Filter filter = hstQuery.createFilter();
            filter.addEqualTo(KKCndConstants.PRODUCT_ID, (long) productId);

            hstQuery.setFilter(filter);

            HstQueryResult queryResult = hstQuery.execute();

            // No result
            if (queryResult.getTotalSize() == 0) {
                return null;
            }

            return (KKProductDocument) queryResult.getHippoBeans().nextHippoBean();

        } catch (QueryException e) {
            log.error("Failed to find the Hippo product document for the productId {} - {}", productId, e.toString());
        }

        return null;
    }
}
