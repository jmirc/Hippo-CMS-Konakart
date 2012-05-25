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
        processorContext.getSeedData().getRequest().setAttribute(SHIPPING_QUOTES,
                kkAppEng.getOrderMgr().getShippingQuotes());

        processorContext.getSeedData().getRequest().setAttribute(SHIPPING_METHOD,
                kkAppEng.getOrderMgr().getCheckoutOrder().getShippingMethod());
    }

    @Override
    public void doAction() throws ActivityException {
        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        if (seedData.getAction().equals(KKCheckoutConstants.ACTIONS.SELECT.name())) {
            String shippingMethod = KKUtil.getEscapedParameter(seedData.getRequest(), SHIPPING_METHOD);

            if (StringUtils.isEmpty(shippingMethod)) {
                setNextLoggedState(STATES.SHIPPING_METHOD.name());
                addMessage(GLOBALMESSAGE, seedData.getBundle().getString("checkout.select.shipping.method"));
                return;
            }

            // Attach the shipping quote to the order
            KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().getCheckoutOrder().setShippingMethod(shippingMethod);
        }

    }

    @Override
    public void doAdditionalData() {
        super.doAdditionalData();

        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        List<String> acceptedStates = Arrays.asList(STATES.PAYMENT_METHOD.name(), STATES.ORDER_REVIEW.name());

        String state = seedData.getState();

        if (StringUtils.isNotEmpty(state) && acceptedStates.contains(state)) {
            processorContext.getSeedData().getRequest().setAttribute(getAcceptState().concat("_EDIT"), true);
        }
    }
}
