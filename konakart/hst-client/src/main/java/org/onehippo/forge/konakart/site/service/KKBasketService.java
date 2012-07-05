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

import com.konakart.al.BasketMgr;
import com.konakart.al.KKAppEng;
import com.konakart.appif.OptionIf;
import org.hippoecm.hst.core.component.HstRequest;

import javax.annotation.Nonnull;

public interface KKBasketService {

    /**
     * Called to determine whether to display prices with tax.
     *
     * @return Return true if we should display prices with tax
     */
    boolean displayPriceWithTax(@Nonnull HstRequest request);

    /**
     * Returns the total price of the basket as a formatted string
     *
     * @param request the Hst request
     * @return Return total value of basket already formatted
     */
    String getBasketTotal(@Nonnull HstRequest request);

    /**
     * Used to get an instance of the BasketMgr.
     *
     * @param request the hst request
     * @return Return the basketMgr.
     */
    BasketMgr getBasketMgr(@Nonnull HstRequest request);

    /**
     * Add the product to the basket
     *
     * @param kkAppEng  the konakart engine
     * @param request   the hst request
     * @param prodId    id of the product to add
     * @param optionIfs list of selected options associated with the product
     * @return true if the product has been added, false otherwise
     */
    boolean addProductToBasket(@Nonnull KKAppEng kkAppEng, @Nonnull HstRequest request, int prodId,
                               @Nonnull OptionIf[] optionIfs);


    /**
     * Add a product to a wish list
     *
     * @param kkAppEng   the konakart engine
     * @param request    the hst request
     * @param wishListId if of the wishList on which the product will be added
     * @param productId  id of the product to add
     * @param optionIfs  list of selected options associated with the product
     * @return true if the product has been added, false otherwise
     */
    public boolean addProductToWishList(@Nonnull KKAppEng kkAppEng, @Nonnull HstRequest request, int wishListId,
                                        int productId, @Nonnull OptionIf[] optionIfs);

}
