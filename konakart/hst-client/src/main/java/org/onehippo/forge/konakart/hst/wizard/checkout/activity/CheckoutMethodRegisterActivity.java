package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutProcessContext;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutSeedData;

public class CheckoutMethodRegisterActivity extends BaseCheckoutActivity {

    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    @Override
    public void doBeforeRender() {
        // do nothing
    }

    @Override
    public void doAction() {
        CheckoutProcessContext checkoutProcessContext = (CheckoutProcessContext) processorContext;
        CheckoutSeedData seedData = checkoutProcessContext.getSeedData();


        if (seedData.getAction().equals(ACTIONS.LOGIN.name())) {

            String username = KKUtil.getEscapedParameter(seedData.getRequest(), EMAIL);
            String password = KKUtil.getEscapedParameter(seedData.getRequest(), PASSWORD);

            if (!(seedData.getKkHstComponent().loggedIn(seedData.getRequest(), seedData.getResponse(), username, password))) {
                addMessage(EMAIL, seedData.getBundle().getString("checkout.invalid.password"));
            } else {
                try {
                    // Create an order object that we will use for the checkout process
                    seedData.getKkHstComponent().getKkAppEng().getOrderMgr().createCheckoutOrder();

                    // Get shipping quotes from the engine
                    seedData.getKkHstComponent().getKkAppEng().getOrderMgr().createShippingQuotes();
                } catch (Exception e) {
                    log.error("A new Order could not be created", e);
                }
            }

        }
    }

}
