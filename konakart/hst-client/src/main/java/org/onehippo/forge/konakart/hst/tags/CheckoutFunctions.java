package org.onehippo.forge.konakart.hst.tags;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.hst.utils.KKActionsConstants;

/**
 * Global JSTL functions for the checkout process
 */
public class CheckoutFunctions {

    /**
     * Check if the customer asked to checkout as a guest
     *
     * @param hstRequest the hst request
     * @return true if the customer asked to checkout as a guest, false otherwise
     */
    public static boolean hasCheckoutAsGuest(HstRequest hstRequest) {
        String dontHaveAccount = hstRequest.getParameter(KKActionsConstants.DONT_HAVE_ACCOUNT);

        return StringUtils.isNotBlank(dontHaveAccount) && StringUtils.equals(dontHaveAccount, KKActionsConstants.CHECKOUT_AS_GUEST);
    }

    /**
     * Check if the customer asked to checkout as a register
     *
     * @param hstRequest the hst request
     * @return true if the customer asked to checkout as a register, false otherwise
     */
    public static boolean hasCheckoutAsRegister(HstRequest hstRequest) {
        String dontHaveAccount = hstRequest.getParameter(KKActionsConstants.DONT_HAVE_ACCOUNT);

        return StringUtils.isNotBlank(dontHaveAccount) && StringUtils.equals(dontHaveAccount, KKActionsConstants.CHECKOUT_ASK_REGISTER);
    }

    /**
     * Check if the current state is equals to the state's parameter
     *
     * @param hstRequest   the hstRequest
     * @param stateToCheck the state to check
     * @return true if the current state is equals to the state's parameter
     */
    public static boolean checkCheckoutState(HstRequest hstRequest, String stateToCheck) {
        String currentState = hstRequest.getParameter(KKActionsConstants.STATE);

        return StringUtils.isNotBlank(currentState) && StringUtils.equals(stateToCheck, currentState);
    }

}
