/*
 * =========================================================
 * Hippo CMS - Konakart
 * https://bitbucket.org/jmirc/hippo-cms-konakart
 * =========================================================
 * Copyright 2012
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================================================
 */

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
     * @throws HstComponentException .
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
     * @throws HstComponentException .
     */
    @Nullable
    String manageCookies(@Nonnull HttpServletRequest  request, @Nonnull HttpServletResponse response,
                         @Nonnull KKAppEng kkAppEng) throws HstComponentException;

    /**
     * Save the customer name in a cookie so that we can greet him when he next accesses the
     * application.
     *
     * @param request the http request
     * @param response the http response
     * @throws HstComponentException .
     */
    @Nullable
    void manageCookiesLogin(@Nonnull HttpServletRequest  request, @Nonnull HttpServletResponse response) throws HstComponentException;

    /**
     * Save the customer name in a cookie so that we can greet him when he next accesses the
     * application.
     *
     * @param request the http request
     * @param response the http response
     * @param kkAppEng the Konakart client
     * @throws HstComponentException .
     */
    void manageCookiesLogin(@Nonnull HttpServletRequest  request, @Nonnull HttpServletResponse response,
                            @Nonnull KKAppEng kkAppEng) throws HstComponentException;


    /**
     * When we log out, ensure that the new guest customer that is created has the id saved in the
     * browser cookie.
     *
     * @param request  the http request
     * @param response the http response
     * @throws HstComponentException .
     */
    void manageCookieLogout(@Nonnull HttpServletRequest  request, @Nonnull HttpServletResponse response) throws HstComponentException;

    /**
     * When we log out, ensure that the new guest customer that is created has the id saved in the
     * browser cookie.
     *
     * @param request  the http request
     * @param response the http response
     * @param kkAppEng the Konakart client
     *
     * @throws HstComponentException .
     */
    void manageCookieLogout(@Nonnull HttpServletRequest  request, @Nonnull HttpServletResponse response,
                            @Nonnull KKAppEng kkAppEng) throws HstComponentException;

}
