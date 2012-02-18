package org.onehippo.forge.konakart.hst.utils;

import com.konakart.app.KKCookie;
import com.konakart.app.KKException;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.KKCookieIf;
import org.onehippo.forge.konakart.common.engine.KKEngine;
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

    protected static final String GUEST_CUSTOMER_ID = "GUEST_CUSTOMER_ID";

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
                                 KKEngine kkEngine) throws KKException {
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
                                 HttpServletResponse response, KKEngine kkEngine) throws KKException {
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
    protected String getKKCookie(String customerUuid, String attrId, KKEngine kkEngine)
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
                               HttpServletResponse response, KKEngine kkEngine) throws KKException {
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
     * @param attrId the cookie's attribute
     * @param attrValue the cookie's value
     * @param kkEngine konakart engine
     * @throws KKException failed to set a cookie
     */
    protected void setKKCookie(String customerUuid, String attrId, String attrValue,
                               KKEngine kkEngine) throws KKException
    {
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
