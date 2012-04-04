package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutSeedData;

public class ShippingMethodActivity extends BaseCheckoutActivity {

    private static final String SHIPPING_QUOTES = "shippingQuotes";

    @Override
    public void doBeforeRender() throws ActivityException {
        processorContext.getSeedData().getRequest().setAttribute(SHIPPING_QUOTES,
                processorContext.getSeedData().getKkHstComponent().getKkAppEng().getOrderMgr().getShippingQuotes());
    }

    @Override
    public void doAction() throws ActivityException {
        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        if (seedData.getAction().equals(BaseCheckoutActivity.ACTIONS.SELECT.name())) {
            String shippingMethod = KKUtil.getEscapedParameter(seedData.getRequest(), "shipping");

            if (StringUtils.isEmpty(shippingMethod)) {
                setNextLoggedState(BaseCheckoutActivity.STATES.SHIPPING_ADDRESS.name());
                addMessage(GLOBALMESSAGE, seedData.getBundle().getString("checkout.select.shipping.method"));
                return;
            }

            // Attach the shipping quote to the order
            seedData.getKkHstComponent().getKkAppEng().getOrderMgr().addShippingQuoteToOrder(shippingMethod);
        }

    }
}
