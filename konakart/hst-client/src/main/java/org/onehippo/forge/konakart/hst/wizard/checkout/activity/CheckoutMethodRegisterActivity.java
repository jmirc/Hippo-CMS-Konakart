package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.ProcessorContext;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutProcessContext;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutSeedData;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.onehippo.forge.konakart.site.service.impl.KKEventServiceImpl;

public class CheckoutMethodRegisterActivity extends BaseCheckoutActivity {

    private static final String EMAIL = "email";
    private static final String PASSWORD = "password";

    @Override
    public void initialize(ProcessorContext processorContext) {
        super.initialize(processorContext);

        // Insert event
        KKServiceHelper.getKKEventService().insertCustomerEvent(hstRequest, KKEventServiceImpl.ACTION_ENTER_CHECKOUT);
    }

    @Override
    public void doBeforeRender() {
        // do nothing
    }

    @Override
    public void doAction() throws ActivityException {
        super.doAction();

        CheckoutProcessContext checkoutProcessContext = (CheckoutProcessContext) processorContext;
        CheckoutSeedData seedData = checkoutProcessContext.getSeedData();


        if (seedData.getAction().equals(KKCheckoutConstants.ACTIONS.LOGIN.name())) {

            String username = KKUtil.getEscapedParameter(seedData.getRequest(), EMAIL);
            String password = KKUtil.getEscapedParameter(seedData.getRequest(), PASSWORD);

            if (!(KKServiceHelper.getKKEngineService().loggedIn(seedData.getRequest(), seedData.getResponse(), username, password))) {
                addMessage(EMAIL, seedData.getBundle().getString("checkout.invalid.password"));
            } else {
                try {
                    // Create an order object that we will use for the checkout process
                    KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().createCheckoutOrder();

                    // Get shipping quotes from the engine
                    KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().createShippingQuotes();
                } catch (Exception e) {
                    log.error("A new Order could not be created", e);
                }
            }

        }
    }
}
