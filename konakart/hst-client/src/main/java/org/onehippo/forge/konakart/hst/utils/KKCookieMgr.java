package org.onehippo.forge.konakart.hst.utils;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.app.KKCookie;
import com.konakart.app.KKException;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.KKCookieIf;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.util.UUID;

public class KKCookieMgr {

    /**
     * The <code>Log</code> instance for this application.
     */
    protected Logger log = LoggerFactory.getLogger(KKCookieMgr.class);

    /**
     * Customer tags
     */
    public static final String TAG_PROD_PAGE_SIZE = "PROD_PAGE_SIZE";

    public static final String TAG_ORDER_PAGE_SIZE = "ORDER_PAGE_SIZE";

    public static final String TAG_REVIEW_PAGE_SIZE = "REVIEW_PAGE_SIZE";

    public static final String TAG_PRODUCTS_VIEWED = "PRODUCTS_VIEWED";

    public static final String TAG_CATEGORIES_VIEWED = "CATEGORIES_VIEWED";

    public static final String TAG_MANUFACTURERS_VIEWED = "MANUFACTURERS_VIEWED";

    public static final String TAG_SEARCH_STRING = "SEARCH_STRING";

    public static final String TAG_COUNTRY_CODE = "COUNTRY_CODE";

    public static final String TAG_BIRTH_DATE = "BIRTH_DATE";

    public static final String TAG_IS_MALE = "IS_MALE";


    public static final String GUEST_CUSTOMER_ID = "GUEST_CUSTOMER_ID";

    public static final String CUSTOMER_LOCALE = "CUSTOMER_LOCALE";

    public static final String CUSTOMER_NAME = "CUSTOMER_NAME";

    public static final String CUSTOMER_UUID = "CUSTOMER_UUID";

    public static final int COOKIE_MAX_AGE_IN_SECS = 365 * 24 * 60 * 60;


    /**
     * Method used to create a browser cookie when a customer first accesses the application. If the
     * cookie already exists then we retrieve the guest customer id from the cookie which will be
     * used to retrieve and cart items that the customer added to the cart on his last visit.
     *
     * @param request  the hst request
     * @param response the hst response
     * @param kkAppEng the konakart engine
     * @return Returns the Customer UUID
     * @throws KKException    .
     * @throws KKAppException .
     */
    public String manageCookies(HstRequest request, HstResponse response,
                                 KKAppEng kkAppEng) throws KKException, KKAppException {
        if (!kkAppEng.isKkCookieEnabled()) {
            return null;
        }

        /*
         * The current customer should at this point be a guest customer with a negative customer id
         */
        CustomerIf currentCustomer = kkAppEng.getCustomerMgr().getCurrentCustomer();
        if (currentCustomer == null) {
            log.warn("Current customer is set to null in the manageCookies method. This should never happen");
            return null;
        }

        /*
         * Get the customerUuid from the browser cookie. A new cookie is created if it doesn't exist
         */
        String customerUuid = getCustomerUuidFromBrowserCookie(request, response);

        /*
         * Get the guestCustomerId from the KK database.
         */
        String guestCustomerIdStr = getKKCookie(customerUuid, GUEST_CUSTOMER_ID, kkAppEng);

        if (guestCustomerIdStr == null) {
            /*
             * If it doesn't exist, then we create it
             */
            setKKCookie(customerUuid, GUEST_CUSTOMER_ID, Integer.toString(currentCustomer.getId()),
                    kkAppEng);

        } else {
            /*
             * Set the current customer id with the one retrieved from the cookie and fetch any cart
             * items that he may have.
             */
            currentCustomer.setId(Integer.parseInt(guestCustomerIdStr));
            kkAppEng.getBasketMgr().getBasketItemsPerCustomer();
            if (kkAppEng.getWishListMgr().allowWishListWhenNotLoggedIn()) {
                kkAppEng.getWishListMgr().fetchCustomersWishLists();
            }

            // Get the product page size
            String prodPageSizeStr = getKKCookie(customerUuid, TAG_PROD_PAGE_SIZE, kkAppEng);
            if (prodPageSizeStr != null && prodPageSizeStr.length() > 0) {
                try {
                    int prodPageSize = Integer.parseInt(prodPageSizeStr);
                    kkAppEng.getProductMgr().setMaxDisplaySearchResults(prodPageSize);
                } catch (NumberFormatException e) {
                    log
                            .warn("The product page size value stored in the cookie for customer with guest id "
                                    + guestCustomerIdStr
                                    + " is not a numeric value: "
                                    + prodPageSizeStr);
                }
            }

            // Get the order page size
            String orderPageSizeStr = getKKCookie(customerUuid, TAG_ORDER_PAGE_SIZE, kkAppEng);
            if (orderPageSizeStr != null && orderPageSizeStr.length() > 0) {
                try {
                    int orderPageSize = Integer.parseInt(orderPageSizeStr);
                    kkAppEng.getOrderMgr().setPageSize(orderPageSize);
                } catch (NumberFormatException e) {
                    log
                            .warn("The order page size value stored in the cookie for customer with guest id "
                                    + guestCustomerIdStr
                                    + " is not a numeric value: "
                                    + orderPageSizeStr);
                }
            }

            // Get the review page size
            String reviewPageSizeStr = getKKCookie(customerUuid, TAG_REVIEW_PAGE_SIZE, kkAppEng);
            if (reviewPageSizeStr != null && reviewPageSizeStr.length() > 0) {
                try {
                    int reviewPageSize = Integer.parseInt(reviewPageSizeStr);
                    kkAppEng.getReviewMgr().setPageSize(reviewPageSize);
                } catch (NumberFormatException e) {
                    log
                            .warn("The review page size value stored in the cookie for customer with guest id "
                                    + guestCustomerIdStr
                                    + " is not a numeric value: "
                                    + reviewPageSizeStr);
                }
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("GUEST_CUSTOMER_ID cookie value = "
                    + getKKCookie(customerUuid, GUEST_CUSTOMER_ID, kkAppEng));
            log.debug("CUSTOMER_NAME cookie value = "
                    + getKKCookie(customerUuid, CUSTOMER_NAME, kkAppEng));
            log.debug("CUSTOMER_LOCALE cookie value = "
                    + getKKCookie(customerUuid, CUSTOMER_LOCALE, kkAppEng));
            log.debug("PROD_PAGE_SIZE cookie value = "
                    + getKKCookie(customerUuid, TAG_PROD_PAGE_SIZE, kkAppEng));
            log.debug("ORDER_PAGE_SIZE cookie value = "
                    + getKKCookie(customerUuid, TAG_ORDER_PAGE_SIZE, kkAppEng));
            log.debug("REVIEW_PAGE_SIZE cookie value = "
                    + getKKCookie(customerUuid, TAG_REVIEW_PAGE_SIZE, kkAppEng));
        }

        return customerUuid;

    }

    /**
     * Utility method that can be used to set a KKCookie. It attempts to get the UUID from the
     * browser cookie and creates a new browser cookie if it doesn't find one.
     *
     * @param attrId    id of the attribute to save
     * @param attrValue value of the attribute
     * @param request   the hst request
     * @param response  the hst response
     * @param kkAppEng  the konakart engine
     * @throws KKException .
     */
    protected void setKKCookie(String attrId, String attrValue, HstRequest request,
                               HstResponse response, KKAppEng kkAppEng) throws KKException {
        /*
         * Get the CustomerUuid from the browser cookie and create the cookie if it doesn't exist.
         */
        String uuid = getCustomerUuidFromBrowserCookie(request, response);

        /*
         * Now we can save the KKCookie
         */
        setKKCookie(uuid, attrId, attrValue, kkAppEng);

    }

    /**
     * Utility method to set a KKCookie when we have the customerUuid
     *
     * @param customerUuid customer uuid
     * @param attrId       the id of the attribute
     * @param attrValue    the value to save
     * @param kkAppEng     the konakart engine
     * @throws KKException .
     */
    protected void setKKCookie(String customerUuid, String attrId, String attrValue,
                               KKAppEng kkAppEng) throws KKException {
        KKCookieIf kkCookie = new KKCookie();
        kkCookie.setCustomerUuid(customerUuid);
        kkCookie.setAttributeId(attrId);
        kkCookie.setAttributeValue(attrValue);
        kkAppEng.getEng().setCookie(kkCookie);
    }

    /**
     * Utility method to read a KKCookie. It attempts to get the UUID from the browser cookie and
     * creates a new browser cookie if it doesn't find one.
     *
     * @param attrId   the attribute to read
     * @param request  the hst request
     * @param response the hst response
     * @param kkAppEng the konakart engine
     * @return the value of the cookie
     * @throws KKException .
     */
    protected String getKKCookie(String attrId, HstRequest request,
                                 HstResponse response, KKAppEng kkAppEng) throws KKException {
        /*
         * Get the CustomerUuid from the browser cookie and create the cookie if it doesn't exist.
         */
        String uuid = getCustomerUuidFromBrowserCookie(request, response);

        /*
         * Now get the KKCookie
         */
        return getKKCookie(uuid, attrId, kkAppEng);
    }

    /**
     * Utility method to read a KKCookie when we have the CustomerUuid
     *
     * @param customerUuid the customer UUID
     * @param attrId       the attribute to save
     * @param kkAppEng     the konakart engine.
     * @return the value of the cookie
     * @throws KKException .
     */
    public String getKKCookie(String customerUuid, String attrId, KKAppEng kkAppEng)
            throws KKException {
        KKCookieIf kkCookie = kkAppEng.getEng().getCookie(customerUuid, attrId);
        if (kkCookie != null) {
            return kkCookie.getAttributeValue();
        }
        return null;
    }

    /**
     * Utility method to get the CustomerUuid from the browser cookie and create the cookie if it
     * doesn't exist.
     *
     * @param request  the hst request
     * @param response the hst response
     * @return Returns the CustomerUuid
     */
    private String getCustomerUuidFromBrowserCookie(HstRequest request,
                                                    HstResponse response) {
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

    /**
     * When we log out, ensure that the new guest customer that is created has the id saved in the
     * browser cookie.
     *
     * @param request  the hst request
     * @param response the hst response
     * @param kkAppEng konakart engine
     * @throws KKException    .
     * @throws KKAppException .
     */
    public void manageCookieLogout(HstRequest request, HstResponse response,
                                      KKAppEng kkAppEng) throws KKException, KKAppException {
        if (!kkAppEng.isKkCookieEnabled()) {
            return;
        }

        CustomerIf currentCustomer = kkAppEng.getCustomerMgr().getCurrentCustomer();
        if (currentCustomer != null) {
            String guestCustomerIdStr = getKKCookie(GUEST_CUSTOMER_ID, request, response, kkAppEng);
            // Only get the basket items if we can retrieve a temporary customer from the cookie
            if (guestCustomerIdStr != null) {
                try {
                    currentCustomer.setId(Integer.parseInt(guestCustomerIdStr));
                    kkAppEng.getBasketMgr().getBasketItemsPerCustomer();
                } catch (NumberFormatException e) {
                    // do nothing
                }
            }
        }
    }

    /**
     * Save the customer name in a cookie so that we can greet him when he next accesses the
     * application.
     *
     * @param request the hst request
     * @param response the hst response
     * @param kkAppEng the konakart engine
     * @throws KKException .
     * @throws KKAppException .
     */
    public void manageCookiesLogin(HstRequest request, HstResponse response, KKAppEng kkAppEng)
            throws KKException, KKAppException {
        if (!kkAppEng.isKkCookieEnabled()) {
            return;
        }

        CustomerIf currentCustomer = kkAppEng.getCustomerMgr().getCurrentCustomer();
        if (currentCustomer != null) {
            setKKCookie(CUSTOMER_NAME, currentCustomer.getFirstName() + " "
                    + currentCustomer.getLastName(), request, response, kkAppEng);
        }

        /*
         * Get customer preferences from customer tags. If the tag value exists, then set the
         * preference in the manager and set the cookie.
         */
        String prodPageSizeStr = kkAppEng.getCustomerTagMgr().getCustomerTagValue(TAG_PROD_PAGE_SIZE);
        if (prodPageSizeStr != null && prodPageSizeStr.length() > 0) {
            int prodPageSize = Integer.parseInt(prodPageSizeStr);
            kkAppEng.getProductMgr().setMaxDisplaySearchResults(prodPageSize);
            setKKCookie(TAG_PROD_PAGE_SIZE, prodPageSizeStr, request, response, kkAppEng);
        }
        String orderPageSizeStr = kkAppEng.getCustomerTagMgr().getCustomerTagValue(TAG_ORDER_PAGE_SIZE);
        if (orderPageSizeStr != null && orderPageSizeStr.length() > 0) {
            int orderPageSize = Integer.parseInt(orderPageSizeStr);
            kkAppEng.getOrderMgr().setPageSize(orderPageSize);
            setKKCookie(TAG_ORDER_PAGE_SIZE, orderPageSizeStr, request, response, kkAppEng);
        }
        String reviewPageSizeStr = kkAppEng.getCustomerTagMgr().getCustomerTagValue(
                TAG_REVIEW_PAGE_SIZE);
        if (reviewPageSizeStr != null && reviewPageSizeStr.length() > 0) {
            int reviewPageSize = Integer.parseInt(reviewPageSizeStr);
            kkAppEng.getReviewMgr().setPageSize(reviewPageSize);
            setKKCookie(TAG_REVIEW_PAGE_SIZE, reviewPageSizeStr, request, response, kkAppEng);
        }
    }
}
