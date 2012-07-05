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

import com.konakart.appif.CustomerIf;
import com.konakart.appif.WishListIf;
import org.hippoecm.hst.core.component.HstRequest;

import javax.annotation.Nonnull;

public interface KKCustomerService {


    /**
     * Get the current customer (guest or registered)
     *
     * @param request the hst request
     * @return the current customer
     */
    CustomerIf getCurrentCustomer(@Nonnull HstRequest request);


        /**
        * Check if the current customer is a guest or a registered customer
        *
        * @param request the hst request
        * @return true if the customer is a guest, false otherwise.
        */
    boolean isGuestCustomer(HstRequest request);


    /**
     * Check if the wish list is enabled.
     *
     * @param request the hst request
     * @return true if the wish list is enabled, false otherwise
     */
    boolean wishListEnabled(HstRequest request);

    /**
     * @param request the hst request
     * @return the wish list that has the type equals to 0
     */
    WishListIf getDefaultWishList(HstRequest request);


    }
