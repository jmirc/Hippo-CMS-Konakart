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

import com.konakart.app.KKException;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutProcessContext;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutSeedData;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import java.util.Arrays;
import java.util.List;

public class ShippingAddressActivity extends BaseAddressActivity {

    @Override
    public void doAction() throws ActivityException {
        super.doAction();

        CheckoutProcessContext checkoutProcessContext = (CheckoutProcessContext) processorContext;
        CheckoutSeedData seedData = checkoutProcessContext.getSeedData();


        if (seedData.getAction().equals(KKCheckoutConstants.ACTIONS.SELECT.name())) {
            Integer addressId = Integer.valueOf(KKUtil.getEscapedParameter(seedData.getRequest(), ADDRESS));

            // Ask for a new address
            if (addressId == -1) {
                try {
                    addressId = KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getCustomerMgr().addAddressToCustomer(createAddressForCustomer());
                } catch (Exception e) {
                    setNextLoggedState(KKCheckoutConstants.STATES.INITIAL.name());
                    addMessage("globalmessage", seedData.getBundleAsString("checkout.failed.create.address"));
                    return;
                }
            }

            try {
                KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().setCheckoutOrderShippingAddress(addressId);

                // Skip the SHIPPING ADDRESS step because the customer has decided to use the
                // same billing address
                setNextLoggedState(KKCheckoutConstants.STATES.SHIPPING_METHOD.name());
            } catch (KKException e) {
                log.error("Failed to set the shipping address", e);
            }
        }
    }

    @Override
    public void doAdditionalData() {
        super.doAdditionalData();

        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        List<String> acceptedStates = Arrays.asList(KKCheckoutConstants.STATES.SHIPPING_METHOD.name(), KKCheckoutConstants.STATES.PAYMENT_METHOD.name(),
                KKCheckoutConstants.STATES.ORDER_REVIEW.name());


        String state = seedData.getState();

        if (StringUtils.isNotEmpty(state) && acceptedStates.contains(state)) {
            hstRequest.getRequestContext().setAttribute(getAcceptState().concat("_EDIT"), true);
            hstRequest.setAttribute(getAcceptState().concat("_EDIT"), true);
        }

    }

}
