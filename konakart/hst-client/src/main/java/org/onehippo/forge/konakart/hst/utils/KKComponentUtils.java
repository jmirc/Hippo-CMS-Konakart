package org.onehippo.forge.konakart.hst.utils;

import com.konakart.al.CategoryMgr;
import com.konakart.al.KKAppEng;
import com.konakart.appif.CategoryIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.security.servlet.LoginServlet;
import org.onehippo.forge.konakart.hst.components.KKCheckout;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Enumeration;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class is used to offer methods used to interact with Konakart
 * if you decided to not extend the @see KKBaseHstComponent class.
 */
public class KKComponentUtils {

    public static final Logger log = LoggerFactory.getLogger(KKComponentUtils.class);
    public static final String CATEGORIES_FACET = "categoriesFacet";
    public static final String NEEDAUTH = "needauth";
    public static final String LOGIN_ERROR = "loginError";
    public static final String USERNAME = "username";
    public static final String DESTINATION = "destination";
    public static final String DEFAULT_WISH_LIST = "defaultWishList";
    public static final String CURRENT_CUSTOMER = "currentCustomer";
    public static final String BASKET_TOTAL = "basketTotal";
    public static final String WISH_LIST_ENABLED = "wishListEnabled";
    public static final String DISPLAY_PRICE_WITH_TAX = "displayPriceWithTax";
    public static final String IS_LOGGED = "isLogged";

    /**
     * Set the global konakart attributes to the Hst request
     *
     * @param request the hst request to set
     */
    public static void setGlobalKonakartAttributes(@Nonnull HstRequest request) {
        try {
            // Set the attribut isLogged if the user is a logged user
            request.setAttribute(IS_LOGGED, !KKServiceHelper.getKKCustomerService().isGuestCustomer(request));

            // Set the attribut displayPriceWithTax used to display or not the price with or without tax
            request.setAttribute(DISPLAY_PRICE_WITH_TAX, KKServiceHelper.getKKBasketService().displayPriceWithTax(request));

            // Set the attibute wishListEnabled. Set to true if the wish list functionality is allowed, false otherwise
            request.setAttribute(WISH_LIST_ENABLED, KKServiceHelper.getKKCustomerService().wishListEnabled(request));

            // Set the default wish list if exists
            request.setAttribute(DEFAULT_WISH_LIST, KKServiceHelper.getKKCustomerService().getDefaultWishList(request));

            // Set the current customer
            request.setAttribute(CURRENT_CUSTOMER, KKServiceHelper.getKKCustomerService().getCurrentCustomer(request));
            request.setAttribute(BASKET_TOTAL, KKServiceHelper.getKKBasketService().getBasketTotal(request));
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
     * Set the login attributes to the Hst request
     *
     * @param request the hst request to set
     */
    public static void setLoginAttributes(@Nonnull HstRequest request) {
        final String destination = (String) request.getSession().getAttribute(LoginServlet.DESTINATION_ATTR_NAME);
        if (destination != null) {
            request.setAttribute(DESTINATION, destination);
        }

        final String username = (String) request.getSession().getAttribute(LoginServlet.USERNAME_ATTR_NAME);
        if (username != null) {
            request.setAttribute(USERNAME, username);
        }

        if (KKUtil.getPublicRequestParameter(request, LOGIN_ERROR) != null) {
            request.setAttribute(LOGIN_ERROR, true);
        } else {
            request.setAttribute(LOGIN_ERROR, false);
        }

        if (KKUtil.getPublicRequestParameter(request, NEEDAUTH) != null) {
            request.setAttribute(NEEDAUTH, true);
        } else {
            request.setAttribute(NEEDAUTH, false);
        }
    }

    /**
     * Set the search categories facet for the category whose id is passed in as a parameter.
     *
     * @param request the hst request to set
     */
    public static void setCategoriesFacet(HstRequest request) {

        KKAppEng kkAppEng = getKKAppEng(request);

        CategoryMgr categoryMgr = kkAppEng.getCategoryMgr();


        List<CategoryIf> categoryList = categoryMgr.getCatMenuList();

        // Load the categoryTree
        if (categoryMgr.getCatMenuList().size() == 0) {
            getKKAppEng(request).getCategoryMgr().reset();
            categoryList = categoryMgr.getCatMenuList();
        }

        request.setAttribute(CATEGORIES_FACET, categoryList);
    }

    /**
     * Retrieve the Konakart client from the HstRequest.
     * The client has been set by the Konakart Valve.
     *
     * @param request the hst request
     * @return the Konakart client.
     */
    @Nonnull
    public static KKAppEng getKKAppEng(@Nonnull HstRequest request) {
        KKAppEng kkAppEng = (KKAppEng) request.getAttribute(KKAppEng.KONAKART_KEY);

        return checkNotNull(kkAppEng);
    }
}
