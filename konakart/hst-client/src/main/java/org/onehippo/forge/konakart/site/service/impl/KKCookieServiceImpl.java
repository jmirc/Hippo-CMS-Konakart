package org.onehippo.forge.konakart.site.service.impl;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.app.KKCookie;
import com.konakart.app.KKException;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.KKCookieIf;
import org.hippoecm.hst.core.component.HstComponentException;
import org.onehippo.forge.konakart.site.service.KKCookieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

import static org.onehippo.forge.konakart.site.service.KKTagsService.*;

public class KKCookieServiceImpl extends KKBaseServiceImpl implements KKCookieService {

    /**
     * The <code>Log</code> instance for this application.
     */
    protected Logger log = LoggerFactory.getLogger(KKCookieServiceImpl.class);



    @Override
    @Nullable
    public String manageCookies(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull KKAppEng kkAppEng) throws HstComponentException {
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
            try {
                kkAppEng.getBasketMgr().getBasketItemsPerCustomer();
            } catch (Exception e) {
                throw new HstComponentException(e);
            }

            if (kkAppEng.getWishListMgr().allowWishListWhenNotLoggedIn()) {
                try {
                    kkAppEng.getWishListMgr().fetchCustomersWishLists();
                } catch (Exception e) {
                    throw new HstComponentException(e);
                }
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

    @Override
    @Nullable
    public String manageCookies(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response)
            throws HstComponentException {

        KKAppEng kkAppEng = getKKAppEng(request);

        return manageCookies(request, response, kkAppEng);

    }

    @Override
    public void manageCookiesLogin(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull KKAppEng kkAppEng) throws HstComponentException {
        if (!kkAppEng.isKkCookieEnabled()) {
            return;
        }

        CustomerIf currentCustomer = kkAppEng.getCustomerMgr().getCurrentCustomer();
        if (currentCustomer != null) {
            setKKCookie(CUSTOMER_NAME, currentCustomer.getFirstName() + " "
                    + currentCustomer.getLastName(), request, response, kkAppEng);
        }

        try {
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
        } catch (KKAppException e) {
            throw new HstComponentException(e);
        } catch (KKException e) {
            throw new HstComponentException(e);
        }

    }

    @Override
    public void manageCookiesLogin(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response)
            throws HstComponentException {

        KKAppEng kkAppEng = getKKAppEng(request);

        manageCookiesLogin(request, response, kkAppEng);
    }


    @Override
    public void manageCookieLogout(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response)
            throws HstComponentException {

        KKAppEng kkAppEng = getKKAppEng(request);

        manageCookieLogout(request, response, kkAppEng);
    }

    @Override
    public void manageCookieLogout(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull KKAppEng kkAppEng) throws HstComponentException {

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
                } catch (KKException e) {
                    throw new HstComponentException(e);
                } catch (KKAppException e) {
                    throw new HstComponentException(e);
                }
            }
        }

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
     * @throws org.hippoecm.hst.core.component.HstComponentException
     *          .
     */
    private void setKKCookie(String attrId, String attrValue, HttpServletRequest request,
                             HttpServletResponse response, KKAppEng kkAppEng) throws HstComponentException {
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
     * @throws org.hippoecm.hst.core.component.HstComponentException
     *          .
     */
    private void setKKCookie(String customerUuid, String attrId, String attrValue,
                             KKAppEng kkAppEng) throws HstComponentException {
        KKCookieIf kkCookie = new KKCookie();
        kkCookie.setCustomerUuid(customerUuid);
        kkCookie.setAttributeId(attrId);
        kkCookie.setAttributeValue(attrValue);
        try {
            kkAppEng.getEng().setCookie(kkCookie);
        } catch (KKException e) {
            throw new HstComponentException(e);
        }
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
     * @throws org.hippoecm.hst.core.component.HstComponentException
     *          .
     */
    private String getKKCookie(String attrId, HttpServletRequest request,
                               HttpServletResponse response, KKAppEng kkAppEng) throws HstComponentException {
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
     * @throws org.hippoecm.hst.core.component.HstComponentException
     *          .
     */
    @Nullable
    private String getKKCookie(String customerUuid, String attrId, KKAppEng kkAppEng)
            throws HstComponentException {
        try {
            KKCookieIf kkCookie = kkAppEng.getEng().getCookie(customerUuid, attrId);
            if (kkCookie != null) {
                return kkCookie.getAttributeValue();
            }

            return null;
        } catch (KKException e) {
            throw new HstComponentException(e);
        }
    }

    /**
     * Utility method to get the CustomerUuid from the browser cookie and create the cookie if it
     * doesn't exist.
     *
     * @param request  the hst request
     * @param response the hst response
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
