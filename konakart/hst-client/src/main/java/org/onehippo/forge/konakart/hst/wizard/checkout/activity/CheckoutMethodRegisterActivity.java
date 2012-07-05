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

import com.konakart.bl.ConfigConstants;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutProcessContext;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutSeedData;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.onehippo.forge.konakart.site.service.impl.KKEventServiceImpl;

public class CheckoutMethodRegisterActivity extends BaseCheckoutActivity {

    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    @Override
    public String computeNextState() {
        boolean allowNoRegister = kkAppEng.getConfigAsBoolean(ConfigConstants.ALLOW_CHECKOUT_WITHOUT_REGISTRATION, false);
        boolean hasSelectedRegisterMode = getDontHaveAccountValue() != null;

        if (allowNoRegister && hasSelectedRegisterMode) {
            return getNextLoggedState();
        }

        return super.computeNextState();
    }

    @Override
    public void doBeforeRender() {
        validateCurrentCart();
    }

    @Override
    public void doAction() throws ActivityException {
        super.doAction();

        CheckoutProcessContext checkoutProcessContext = (CheckoutProcessContext) processorContext;
        CheckoutSeedData seedData = checkoutProcessContext.getSeedData();


        String action = seedData.getAction();

        if (action.equals(KKCheckoutConstants.ACTIONS.LOGIN.name())) {

            String username = KKUtil.getEscapedParameter(hstRequest, EMAIL);
            String password = KKUtil.getEscapedParameter(hstRequest, PASSWORD);

            if (!(KKServiceHelper.getKKEngineService().loggedIn(seedData.getRequest(), seedData.getResponse(), username, password))) {
                addMessage(EMAIL, seedData.getBundleAsString("checkout.invalid.password"));
            } else {
                // Insert event
                KKServiceHelper.getKKEventService().insertCustomerEvent(hstRequest, KKEventServiceImpl.ACTION_ENTER_CHECKOUT);
            }
        } else if (action.equals(KKCheckoutConstants.ACTIONS.REGISTER.name())) {
            String dontHaveAccount = getDontHaveAccountValue();

            if (StringUtils.isNotBlank(dontHaveAccount)) {
                hstResponse.setRenderParameter(KKCheckoutConstants.DONT_HAVE_ACCOUNT, dontHaveAccount);

                // Insert event
                KKServiceHelper.getKKEventService().insertCustomerEvent(hstRequest, KKEventServiceImpl.ACTION_ENTER_CHECKOUT);
            }
        }

        hstResponse.setRenderParameter(KKCheckoutConstants.ACTION, action);
    }

}
