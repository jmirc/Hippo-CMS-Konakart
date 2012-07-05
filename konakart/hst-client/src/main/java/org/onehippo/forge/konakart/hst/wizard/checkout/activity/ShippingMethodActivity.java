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

package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutSeedData;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import java.util.Arrays;
import java.util.List;

public class ShippingMethodActivity extends BaseCheckoutActivity {

    private static final String SHIPPING_QUOTES = "shippingQuotes";
    private static final String SHIPPING_METHOD = "shipMethod";

    @Override
    public void doBeforeRender() throws ActivityException {

        if (!validateCurrentCart()) {
            return;
        }

        hstRequest.setAttribute(SHIPPING_QUOTES, kkAppEng.getOrderMgr().getShippingQuotes());
        hstRequest.setAttribute(SHIPPING_METHOD, kkAppEng.getOrderMgr().getCheckoutOrder().getShippingMethod());
    }

    @Override
    public void doAction() throws ActivityException {
        super.doAction();

        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        if (seedData.getAction().equals(KKCheckoutConstants.ACTIONS.SELECT.name())) {
            String shippingMethod = KKUtil.getEscapedParameter(seedData.getRequest(), SHIPPING_METHOD);

            if (StringUtils.isEmpty(shippingMethod)) {
                setNextLoggedState(KKCheckoutConstants.STATES.SHIPPING_METHOD.name());
                addMessage(GLOBALMESSAGE, seedData.getBundleAsString("checkout.select.shipping.method"));
                return;
            }

            // Attach the shipping quote to the order
            KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().addShippingQuoteToOrder(shippingMethod);
        }

    }

    @Override
    public void doAdditionalData() {
        super.doAdditionalData();

        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        List<String> acceptedStates = Arrays.asList(KKCheckoutConstants.STATES.PAYMENT_METHOD.name(), KKCheckoutConstants.STATES.ORDER_REVIEW.name());

        String state = seedData.getState();

        if (StringUtils.isNotEmpty(state) && acceptedStates.contains(state)) {
            hstRequest.getRequestContext().setAttribute(getAcceptState().concat("_EDIT"), true);
            hstRequest.setAttribute(getAcceptState().concat("_EDIT"), true);
        }
    }
}
