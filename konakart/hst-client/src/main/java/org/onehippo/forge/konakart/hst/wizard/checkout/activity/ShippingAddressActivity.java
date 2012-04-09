package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import com.konakart.app.KKException;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.hst.utils.KKConstants;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutProcessContext;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutSeedData;

import java.util.Arrays;
import java.util.List;

public class ShippingAddressActivity extends BaseAddressActivity {

    @Override
    public void doAction() throws ActivityException {
        CheckoutProcessContext checkoutProcessContext = (CheckoutProcessContext) processorContext;
        CheckoutSeedData seedData = checkoutProcessContext.getSeedData();


        if (seedData.getAction().equals(KKConstants.ACTIONS.SELECT.name())) {
            Integer addressId = Integer.valueOf(KKUtil.getEscapedParameter(seedData.getRequest(), ADDRESS));

            // Ask for a new address
            if (addressId == -1) {
                try {
                    addressId = seedData.getKkHstComponent().getKkAppEng().getCustomerMgr().addAddressToCustomer(createAddressForCustomer());
                } catch (Exception e) {
                    setNextLoggedState(STATES.INITIAL.name());
                    addMessage("globalmessage", seedData.getBundle().getString("checkout.failed.create.address"));
                    return;
                }
            }

            try {
                seedData.getKkHstComponent().getKkAppEng().getOrderMgr().setCheckoutOrderShippingAddress(addressId);

                // Skip the SHIPPING ADDRESS step because the customer has decided to use the
                // same billing address
                setNextLoggedState(STATES.SHIPPING_METHOD.name());
            } catch (KKException e) {
                log.error("Failed to set the shipping address", e);
            }
        }
    }

    @Override
    public void doAdditionalData() {
        super.doAdditionalData();

        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        List<String> acceptedStates = Arrays.asList(STATES.SHIPPING_METHOD.name(), STATES.PAYMENT_METHOD.name(),
                STATES.ORDER_REVIEW.name());


        String state = seedData.getState();

        if (StringUtils.isNotEmpty(state) && acceptedStates.contains(state)) {
            processorContext.getSeedData().getRequest().setAttribute(getAcceptState().concat("_EDIT"), true);
        }

    }

}
