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

package org.onehippo.forge.konakart.hst.components;

import com.konakart.al.KKAppEng;
import com.konakart.al.ProdOption;
import com.konakart.al.ProdOptionContainer;
import com.konakart.app.Option;
import com.konakart.appif.BasketIf;
import com.konakart.appif.OptionIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormField;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.component.support.forms.FormUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.onehippo.forge.konakart.site.service.impl.KKEventServiceImpl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class KKHstActionComponent extends KKBaseHstComponent {


    @Override
    final public void doAction(HstRequest request, HstResponse response) {
        String type = KKUtil.getEscapedParameter(request, KKCheckoutConstants.ACTION);

        doAction(type, request, response);
    }

    /**
     * Call with the prepopulated action. The value of the action is defined when the hst:url is created.
     * <p/>
     * i.e.
     * <hst:actionURL var="addToBasket">
     * <hst:param name="action" value="addToBasket"/>
     * <hst:param name="prodId" value="${document.productId}"/>
     * </hst:actionURL>
     *
     * @param action   the action value
     * @param request  the Hst Request
     * @param response the Hst Response
     */
    public void doAction(String action, HstRequest request, HstResponse response) {

        KKAppEng kkAppEng = getKKAppEng(request);


        if (StringUtils.equals(action, KKCheckoutConstants.ACTIONS.ADD_TO_BASKET.name())) {
            String productId = KKUtil.getEscapedParameter(request, KKCheckoutConstants.PRODUCT_ID);
            String addToWishList = KKUtil.getEscapedParameter(request, KKCheckoutConstants.ADD_TO_WISH_LIST);

            // Add this product to the basket
            if (StringUtils.isNotEmpty(productId)) {
                // Get the selected options if exists
                OptionIf[] optionIfs = retrieveSelectedProductOptions(kkAppEng, request);

                // Add this product to the wish list
                if (StringUtils.isNotEmpty(addToWishList) && Boolean.valueOf(addToWishList)) {
                    String wishListId = KKUtil.getEscapedParameter(request, KKCheckoutConstants.WISH_LIST_ID);

                    if (StringUtils.isNotEmpty(wishListId)) {
                        boolean added = KKServiceHelper.getKKBasketService().addProductToWishList(kkAppEng, request,
                                Integer.valueOf(wishListId), Integer.valueOf(productId), optionIfs);

                        redirectAfterProductAddedToWishList(added, request, response);
                    }
                } else {
                    boolean added =  KKServiceHelper.getKKBasketService().addProductToBasket(kkAppEng, request,
                            Integer.valueOf(productId), optionIfs);

                    redirectAfterProductAddedToBasket(added, request, response);
                }
            }

        }

        if (StringUtils.equals(action, KKCheckoutConstants.ACTIONS.REMOVE_FROM_BASKET.name())) {
            String basketId = KKUtil.getEscapedParameter(request, KKCheckoutConstants.BASKET_ID);

            // Remove this product fromthe basket
            if (StringUtils.isNotEmpty(basketId)) {

                int basketIdToRemove = Integer.valueOf(basketId);

                // remove the basket item
                try {
                    // basket items
                    BasketIf[] basketItems = kkAppEng.getCustomerMgr().getCurrentCustomer().getBasketItems();


                    for (BasketIf basketItem : basketItems) {
                        if (basketItem.getId() == basketIdToRemove) {
                            kkAppEng.getBasketMgr().removeFromBasket(basketItem, /** refresh **/false);

                            // insert an event
                            KKServiceHelper.getKKEventService().insertCustomerEvent(request, KKEventServiceImpl.ACTION_REMOVE_FROM_CART,
                                    basketItem.getProductId());
                        }
                    }
                } catch (Exception e) {
                    log.error("Unable to remove the basket with the id - " + basketIdToRemove);
                }
            }

        }
    }


    /**
     * Called when the product is added to the cart.
     * <p/>
     * By default no redirection is done
     *
     * @param added    true if the product has been added, false otherwise
     * @param request  the Hst Request
     * @param response the Hst Response
     */
    protected void redirectAfterProductAddedToBasket(boolean added, @Nonnull HstRequest request,
                                                              @Nonnull HstResponse response) {

    }


    /**
     * Called when the product is added to the wish list.
     * <p/>
     * By default no redirection is done
     *
     * @param added    true if the product has been added, false otherwise
     * @param request  the Hst Request
     * @param response the Hst Response
     */
    protected void redirectAfterProductAddedToWishList(boolean added, @Nonnull HstRequest request,
                                                                @Nonnull HstResponse response) {

    }


    /**
     * Used to retrieve for a product the option that has been selected by the customer.
     *
     * @param kkAppEng the Konakart client
     * @param request  the Hst Request
     * @return a list of options.
     */
    protected OptionIf[] retrieveSelectedProductOptions(@Nonnull KKAppEng kkAppEng, @Nonnull HstRequest request) {

        List<String> fieldsName = new ArrayList<String>();

        // Retrieve selected options
        List<ProdOptionContainer> opts = kkAppEng.getProductMgr().getSelectedProductOptions();

        for (ProdOptionContainer opt : opts) {
            fieldsName.add(opt.getId());
        }


        FormMap formMap = new FormMap(request, fieldsName);
        FormUtils.populate(request, formMap);

        OptionIf[] results = new OptionIf[opts.size()];

        int i = 0;

        // Retrieve selected options
        for (ProdOptionContainer opt : opts) {
            FormField field = formMap.getField(opt.getId());

            if (field != null) {
                String value = field.getValue();

                if (StringUtils.isNotEmpty(value)) {
                    int selectedValue = Integer.parseInt(value);

                    List<ProdOption> optValues = opt.getOptValues();

                    for (ProdOption optValue : optValues) {
                        if (optValue.getId() == selectedValue) {
                            OptionIf optionIf = new Option();
                            optionIf.setId(Integer.parseInt(opt.getId()));
                            optionIf.setValueId(optValue.getId());
                            optionIf.setType(Integer.parseInt(opt.getType()));

                            results[i++] = optionIf;
                        }
                    }

                }
            }

        }

        return results;
    }

}
