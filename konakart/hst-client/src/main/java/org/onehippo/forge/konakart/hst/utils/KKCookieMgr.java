package org.onehippo.forge.konakart.hst.utils;

import com.konakart.app.KKCookie;
import com.konakart.app.KKException;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.KKCookieIf;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.common.engine.KKEngineIf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class KKCookieMgr {

    /**
     * The <code>Log</code> instance for this application.
     */
    protected Logger log = LoggerFactory.getLogger(KKCookieMgr.class);

    /**
     * Customer tags
     */
    protected static final String TAG_PROD_PAGE_SIZE = "PROD_PAGE_SIZE";

    protected static final String TAG_ORDER_PAGE_SIZE = "ORDER_PAGE_SIZE";

    protected static final String TAG_REVIEW_PAGE_SIZE = "REVIEW_PAGE_SIZE";

    protected static final String TAG_PRODUCTS_VIEWED = "PRODUCTS_VIEWED";

    protected static final String TAG_CATEGORIES_VIEWED = "CATEGORIES_VIEWED";

    protected static final String TAG_MANUFACTURERS_VIEWED = "MANUFACTURERS_VIEWED";

    protected static final String TAG_SEARCH_STRING = "SEARCH_STRING";

    protected static final String TAG_COUNTRY_CODE = "COUNTRY_CODE";

    protected static final String TAG_BIRTH_DATE = "BIRTH_DATE";

    protected static final String TAG_IS_MALE = "IS_MALE";


    protected static final String GUEST_CUSTOMER_ID = "GUEST_CUSTOMER_ID";

    protected static final String CUSTOMER_NAME = "CUSTOMER_NAME";

    protected static final String CUSTOMER_UUID = "CUSTOMER_UUID";

    protected static final int COOKIE_MAX_AGE_IN_SECS = 365 * 24 * 60 * 60;


    /**
     * Method used to create a browser cookie when a customer first accesses the application. If the
     * cookie already exists then we retrieve the guest customer id from the cookie which will be
     * used to retrieve and cart items that the customer added to the cart on his last visit.
     *
     * @param request  http request
     * @param response http response
     * @param kkEngine the konakart engine
     * @return the customer UUID
     * @throws KKException .
     */
    public String manageCookies(HttpServletRequest request, HttpServletResponse response,
                                KKEngineIf kkEngine) throws KKException {
        if (!kkEngine.isKkCookieEnabled()) {
            return null;
        }

        /*
         * The current customer should at this point be a guest customer with a negative customer id
         */
        CustomerIf currentCustomer = kkEngine.getCustomerMgr().getCurrentCustomer();
        if (currentCustomer == null) {
            if (log.isWarnEnabled()) {
                log.warn("Current customer is set to null in the manageCookies method. This should never happen");
            }

            return null;
        }

        /*
         * Get the customerUuid from the browser cookie. A new cookie is created if it doesn't exist
         */
        String customerUuid = getCustomerUuidFromBrowserCookie(request, response);

        /*
         * Get the guestCustomerId from the KK database.
         */
        String guestCustomerIdStr = getKKCookie(customerUuid, GUEST_CUSTOMER_ID, kkEngine);

        if (guestCustomerIdStr == null) {
            /*
             * If it doesn't exist, then we create it
             */
            setKKCookie(customerUuid, GUEST_CUSTOMER_ID, Integer.toString(currentCustomer.getId()),
                    kkEngine);

        } else {
            /*
             * Set the current customer id with the one retrieved from the cookie and fetch any cart
             * items that he may have.
             */
            currentCustomer.setId(Integer.parseInt(guestCustomerIdStr));
            kkEngine.getBasketMgr().getBasketItemsPerCustomer();

            if (kkEngine.getWishListMgr().allowWishListWhenNotLoggedIn()) {
                kkEngine.getWishListMgr().fetchCustomersWishLists();
            }
        }

        return customerUuid;

    }

    /**
     * Save the customer name in a cookie so that we can greet him when he next accesses the
     * application.
     *
     * @param request the Hst request
     * @param response the Hst response
     * @param kkEngine the konakart engine
     * @throws KKException .
     */
    public void manageCookiesLogin(HttpServletRequest request, HttpServletResponse response, KKEngineIf kkEngine) throws KKException {
        if (!kkEngine.isKkCookieEnabled()) {
            return;
        }

        CustomerIf currentCustomer = kkEngine.getCustomerMgr().getCurrentCustomer();
        if (currentCustomer != null) {
            setKKCookie(CUSTOMER_NAME, currentCustomer.getFirstName() + " " + currentCustomer.getLastName(),
                    request, response, kkEngine);
        }

        // TODO
//        /*
//         * Get customer preferences from customer tags. If the tag value exists, then set the
//         * preference in the manager and set the cookie.
//         */
//        String prodPageSizeStr = kkEngine.getCustomerTagMgr().getCustomerTagValue(TAG_PROD_PAGE_SIZE);
//
//        if (prodPageSizeStr != null && prodPageSizeStr.length() > 0) {
//            int prodPageSize = Integer.parseInt(prodPageSizeStr);
//            kkEngine.getProductMgr().setMaxDisplaySearchResults(prodPageSize);
//            setKKCookie(TAG_PROD_PAGE_SIZE, prodPageSizeStr, request, response, kkEngine);
//        }
//
//        String orderPageSizeStr = kkEngine.getCustomerTagMgr().getCustomerTagValue(TAG_ORDER_PAGE_SIZE);
//        if (orderPageSizeStr != null && orderPageSizeStr.length() > 0) {
//            int orderPageSize = Integer.parseInt(orderPageSizeStr);
//            kkEngine.getOrderMgr().setPageSize(orderPageSize);
//            setKKCookie(TAG_ORDER_PAGE_SIZE, orderPageSizeStr, request, response, kkEngine);
//        }
//
//        String reviewPageSizeStr = kkAppEng.getCustomerTagMgr().getCustomerTagValue(TAG_REVIEW_PAGE_SIZE);
//
//        if (reviewPageSizeStr != null && reviewPageSizeStr.length() > 0) {
//            int reviewPageSize = Integer.parseInt(reviewPageSizeStr);
//            kkEngine.getReviewMgr().setPageSize(reviewPageSize);
//            setKKCookie(TAG_REVIEW_PAGE_SIZE, reviewPageSizeStr, request, response, kkEngine);
//        }
    }


    /**
     * When we log out, ensure that the new guest customer that is created has the id saved in the
     * browser cookie.
     *
     * @param request  the Hst request
     * @param response the Hst response
     * @param kkEngine the Konakart engine
     * @throws KKException .
     */
    public void manageCookieLogout(HstRequest request, HstResponse response, KKEngineIf kkEngine) throws KKException {
        if (!kkEngine.isKkCookieEnabled()) {
            return;
        }

        CustomerIf currentCustomer = kkEngine.getCustomerMgr().getCurrentCustomer();

        if (currentCustomer != null) {
            String guestCustomerIdStr = getKKCookie(GUEST_CUSTOMER_ID, request, response, kkEngine);
            // Only get the basket items if we can retrieve a temporary customer from the cookie
            if (guestCustomerIdStr != null) {
                try {
                    currentCustomer.setId(Integer.parseInt(guestCustomerIdStr));
                    kkEngine.getBasketMgr().getBasketItemsPerCustomer();
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
        }
    }


    /**
     * Utility method to read a KKCookie. It attempts to get the UUID from the browser cookie and
     * creates a new browser cookie if it doesn't find one.
     *
     * @param attrId   the cookie attribute to retrieve
     * @param request  the http request
     * @param response the http response
     * @param kkEngine the engine
     * @return the value of the cookie
     * @throws com.konakart.app.KKException Failed to retrieve the cookie
     */
    protected String getKKCookie(String attrId, HttpServletRequest request,
                                 HttpServletResponse response, KKEngineIf kkEngine) throws KKException {
        /*
         * Get the CustomerUuid from the browser cookie and create the cookie if it doesn't exist.
         */
        String uuid = getCustomerUuidFromBrowserCookie(request, response);

        /*
         * Now get the KKCookie
         */
        return getKKCookie(uuid, attrId, kkEngine);
    }

    /**
     * Utility method to read a KKCookie when we have the CustomerUuid
     *
     * @param customerUuid the customer Uuid
     * @param attrId       the cookie attribute to retrieve
     * @param kkEngine     the engine
     * @return the value of the cookie
     * @throws KKException Failed to retrieve the cookie
     */
    protected String getKKCookie(String customerUuid, String attrId, KKEngineIf kkEngine)
            throws KKException {
        KKCookieIf kkCookie = kkEngine.getEngine().getCookie(customerUuid, attrId);

        if (kkCookie != null) {
            return kkCookie.getAttributeValue();
        }
        return null;
    }

    /**
     * Utility method that can be used to set a KKCookie. It attempts to get the UUID from the
     * browser cookie and creates a new browser cookie if it doesn't find one.
     *
     * @param attrId    the cookie's attribute
     * @param attrValue the cookie's value
     * @param request   the http request
     * @param response  the http response
     * @param kkEngine  the konakart engine
     * @throws KKException Failed to create the cookie
     */
    protected void setKKCookie(String attrId, String attrValue, HttpServletRequest request,
                               HttpServletResponse response, KKEngineIf kkEngine) throws KKException {
        /*
         * Get the CustomerUuid from the browser cookie and create the cookie if it doesn't exist.
         */
        String customerUuid = getCustomerUuidFromBrowserCookie(request, response);

        /*
         * Now we can save the KKCookie
         */
        setKKCookie(customerUuid, attrId, attrValue, kkEngine);

    }

    /**
     * Utility method to set a KKCookie when we have the customerUuid
     *
     * @param customerUuid customer uid to save
     * @param attrId       the cookie's attribute
     * @param attrValue    the cookie's value
     * @param kkEngine     konakart engine
     * @throws KKException failed to set a cookie
     */
    protected void setKKCookie(String customerUuid, String attrId, String attrValue,
                               KKEngineIf kkEngine) throws KKException {
        KKCookieIf kkCookie = new KKCookie();
        kkCookie.setCustomerUuid(customerUuid);
        kkCookie.setAttributeId(attrId);
        kkCookie.setAttributeValue(attrValue);
        kkEngine.getEngine().setCookie(kkCookie);
    }

    /**
     * Utility method to get the CustomerUuid from the browser cookie and create the cookie if it
     * doesn't exist.
     *
     * @param request  the http request
     * @param response the http response
     * @return Returns the CustomerUuid
     */
    private String getCustomerUuidFromBrowserCookie(HttpServletRequest request,
                                                    HttpServletResponse response) {
        /*
         * Try to find the cookie we are looking for
         */
        Cookie[] cookies = request.getCookies();
        String uuid = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if (cookieName.equals(CUSTOMER_UUID)) {
                    /*
                     * If we find the cookie we get the value and update the max age.
                     */
                    uuid = cookie.getValue();
                    cookie.setMaxAge(COOKIE_MAX_AGE_IN_SECS);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }

        /*
         * If the browser cookie doesn't exist then we have to create it and store a newly created
         * UUID string
         */
        if (uuid == null) {
            UUID uuidObject = UUID.randomUUID();
            uuid = uuidObject.toString();
            /*
             * Create a browser cookie with the UUID
             */
            Cookie uuidCookie = new Cookie(CUSTOMER_UUID, uuid);
            uuidCookie.setMaxAge(COOKIE_MAX_AGE_IN_SECS);
            uuidCookie.setPath("/");
            response.addCookie(uuidCookie);
        }

        return uuid;
    }
}
