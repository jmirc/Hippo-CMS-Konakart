package org.onehippo.forge.konakart.site.service;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.app.KKException;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface KKCookieService {

    /**
     * Method used to create a browser cookie when a customer first accesses the application. If the
     * cookie already exists then we retrieve the guest customer id from the cookie which will be
     * used to retrieve and cart items that the customer added to the cart on his last visit.
     *
     * @param request  the hst request
     * @param response the hst response
     * @return Returns the Customer UUID
     * @throws HstComponentException .
     */
    String manageCookies(@Nonnull HstRequest request, @Nonnull HstResponse response) throws HstComponentException;

    /**
     * Method used to create a browser cookie when a customer first accesses the application. If the
     * cookie already exists then we retrieve the guest customer id from the cookie which will be
     * used to retrieve and cart items that the customer added to the cart on his last visit.
     *
     * @param request  the hst request
     * @param response the hst response
     * @return Returns the Customer UUID
     * @throws HstComponentException .
     */
    @Nullable
    String manageCookies(@Nonnull HstRequest request, @Nonnull HstResponse response, @Nonnull KKAppEng kkAppEng) throws HstComponentException;

    /**
     * Save the customer name in a cookie so that we can greet him when he next accesses the
     * application.
     *
     * @param request the hst request
     * @param response the hst response
     * @throws HstComponentException .
     */
    @Nullable
    void manageCookiesLogin(@Nonnull HstRequest request, @Nonnull HstResponse response) throws HstComponentException;

    /**
     * Save the customer name in a cookie so that we can greet him when he next accesses the
     * application.
     *
     * @param request the hst request
     * @param response the hst response
     * @param kkAppEng the Konakart client
     * @throws HstComponentException .
     */
    void manageCookiesLogin(@Nonnull HstRequest request, @Nonnull HstResponse response, @Nonnull KKAppEng kkAppEng) throws HstComponentException;


    /**
     * When we log out, ensure that the new guest customer that is created has the id saved in the
     * browser cookie.
     *
     * @param request  the hst request
     * @param response the hst response
     * @throws HstComponentException .
     */
    void manageCookieLogout(@Nonnull HstRequest request, @Nonnull HstResponse response) throws HstComponentException;

    /**
     * When we log out, ensure that the new guest customer that is created has the id saved in the
     * browser cookie.
     *
     * @param request  the hst request
     * @param response the hst response
     * @param kkAppEng the Konakart client
     *
     * @throws HstComponentException .
     */
    void manageCookieLogout(@Nonnull HstRequest request, @Nonnull HstResponse response, @Nonnull KKAppEng kkAppEng) throws HstComponentException;

}
