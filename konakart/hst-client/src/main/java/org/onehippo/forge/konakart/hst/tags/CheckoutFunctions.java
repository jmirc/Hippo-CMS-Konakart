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

package org.onehippo.forge.konakart.hst.tags;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;

/**
 * Global JSTL functions for the checkout process
 */
public class CheckoutFunctions {

    /**
     * Check if the customer asked to checkout as a guest
     * @param hstRequest the hst request
     * @return true if the customer asked to checkout as a guest, false otherwise
     */
    public static boolean hasCheckoutAsGuest(HstRequest hstRequest) {
        String dontHaveAccount = hstRequest.getParameter(KKCheckoutConstants.DONT_HAVE_ACCOUNT);

        return StringUtils.isNotBlank(dontHaveAccount) && StringUtils.equals(dontHaveAccount, KKCheckoutConstants.CHECKOUT_AS_GUEST);
    }

    /**
     * Check if the customer asked to checkout as a register
     * @param hstRequest the hst request
     * @return true if the customer asked to checkout as a register, false otherwise
     */
    public static boolean hasCheckoutAsRegister(HstRequest hstRequest) {
        String dontHaveAccount = hstRequest.getParameter(KKCheckoutConstants.DONT_HAVE_ACCOUNT);

        return StringUtils.isNotBlank(dontHaveAccount) && StringUtils.equals(dontHaveAccount, KKCheckoutConstants.CHECKOUT_ASK_REGISTER);
    }

    /**
     * Check if the current state is equals to the state's parameter
     * @param hstRequest the hstRequest
     * @param stateToCheck the state to check
     * @return true if the current state is equals to the state's parameter
     */
    public static boolean checkCheckoutState(HstRequest hstRequest, String stateToCheck) {
        String currentState = hstRequest.getParameter(KKCheckoutConstants.STATE);

        return StringUtils.isNotBlank(currentState) && StringUtils.equals(stateToCheck, currentState);
    }

}
