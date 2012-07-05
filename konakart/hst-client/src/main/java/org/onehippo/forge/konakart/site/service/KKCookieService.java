package org.onehippo.forge.konakart.site.service;

import com.konakart.al.KKAppEng;
import org.hippoecm.hst.core.component.HstComponentException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface KKCookieService {

    /**
     * Method used to create a browser cookie when a customer first accesses the application. If the
     * cookie already exists then we retrieve the guest customer id from the cookie which will be
     * used to retrieve and cart items that the customer added to the cart on his last visit.
     *
     * @param request  the http request
     * @param response the http response
     * @return Returns the Customer UUID
     * @throws org.hippoecm.hst.core.component.HstComponentException
     *          .
     */
    String manageCookies(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) throws HstComponentException;

    /**
     * Method used to create a browser cookie when a customer first accesses the application. If the
     * cookie already exists then we retrieve the guest customer id from the cookie which will be
     * used to retrieve and cart items that the customer added to the cart on his last visit.
     *
     * @param request  the http request
     * @param response the http response
     * @return Returns the Customer UUID
     * @throws org.hippoecm.hst.core.component.HstComponentException
     *          .
     */
    @Nullable
    String manageCookies(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
                         @Nonnull KKAppEng kkAppEng) throws HstComponentException;

    /**
     * Save the customer name in a cookie so that we can greet him when he next accesses the
     * application.
     *
     * @param request  the http request
     * @param response the http response
     * @throws org.hippoecm.hst.core.component.HstComponentException
     *          .
     */
    @Nullable
    void manageCookiesLogin(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) throws HstComponentException;

    /**
     * Save the customer name in a cookie so that we can greet him when he next accesses the
     * application.
     *
     * @param request  the http request
     * @param response the http response
     * @param kkAppEng the Konakart client
     * @throws org.hippoecm.hst.core.component.HstComponentException
     *          .
     */
    void manageCookiesLogin(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
                            @Nonnull KKAppEng kkAppEng) throws HstComponentException;


    /**
     * When we log out, ensure that the new guest customer that is created has the id saved in the
     * browser cookie.
     *
     * @param request  the http request
     * @param response the http response
     * @throws org.hippoecm.hst.core.component.HstComponentException
     *          .
     */
    void manageCookieLogout(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response) throws HstComponentException;

    /**
     * When we log out, ensure that the new guest customer that is created has the id saved in the
     * browser cookie.
     *
     * @param request  the http request
     * @param response the http response
     * @param kkAppEng the Konakart client
     * @throws org.hippoecm.hst.core.component.HstComponentException
     *          .
     */
    void manageCookieLogout(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
                            @Nonnull KKAppEng kkAppEng) throws HstComponentException;

}
