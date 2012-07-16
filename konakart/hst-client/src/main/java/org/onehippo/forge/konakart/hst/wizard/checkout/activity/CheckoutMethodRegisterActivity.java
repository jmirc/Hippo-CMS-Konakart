package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import com.konakart.bl.ConfigConstants;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.hst.utils.KKActionsConstants;
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

        if (action.equals(KKActionsConstants.ACTIONS.REGISTER.name())) {
            String dontHaveAccount = getDontHaveAccountValue();

            if (StringUtils.isNotBlank(dontHaveAccount)) {
                hstResponse.setRenderParameter(KKActionsConstants.DONT_HAVE_ACCOUNT, dontHaveAccount);

                // Insert event
                KKServiceHelper.getKKEventService().insertCustomerEvent(hstRequest, KKEventServiceImpl.ACTION_ENTER_CHECKOUT);
            }
        }

        hstResponse.setRenderParameter(KKActionsConstants.ACTION, action);
    }

}
