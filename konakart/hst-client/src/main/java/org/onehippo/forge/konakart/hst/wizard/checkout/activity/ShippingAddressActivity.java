package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import com.konakart.app.KKException;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.hst.utils.KKActionsConstants;
import org.onehippo.forge.konakart.hst.utils.KKRegisterFormUtils;
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


        if (seedData.getAction().equals(KKActionsConstants.ACTIONS.SELECT.name())) {
            Integer addressId = Integer.valueOf(KKUtil.getEscapedParameter(seedData.getRequest(), KKRegisterFormUtils.ADDRESS));

            // Ask for a new address
            if (addressId == -1) {
                try {
                    addressId = KKServiceHelper.getKKEngineService().
                            getKKAppEng(hstRequest).getCustomerMgr().addAddressToCustomer(registerFormUtils.createAddressForCustomer(formMap));
                } catch (Exception e) {
                    updateNextLoggedState(KKActionsConstants.STATES.INITIAL.name());
                    addMessage("globalmessage", seedData.getBundleAsString("checkout.failed.create.address"));
                    return;
                }
            }

            try {
                KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().setCheckoutOrderShippingAddress(addressId);

                // Skip the SHIPPING ADDRESS step because the customer has decided to use the
                // same billing address
                updateNextLoggedState(KKActionsConstants.STATES.SHIPPING_METHOD.name());
            } catch (KKException e) {
                log.error("Failed to set the shipping address", e);
            }
        }
    }

    @Override
    public void doAdditionalData() {
        super.doAdditionalData();

        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        List<String> acceptedStates = Arrays.asList(KKActionsConstants.STATES.SHIPPING_METHOD.name(), KKActionsConstants.STATES.PAYMENT_METHOD.name(),
                KKActionsConstants.STATES.ORDER_REVIEW.name());


        String state = seedData.getState();

        if (StringUtils.isNotEmpty(state) && acceptedStates.contains(state)) {
            hstRequest.getRequestContext().setAttribute(getAcceptState().concat("_EDIT"), true);
            hstRequest.setAttribute(getAcceptState().concat("_EDIT"), true);
        }

    }

}
