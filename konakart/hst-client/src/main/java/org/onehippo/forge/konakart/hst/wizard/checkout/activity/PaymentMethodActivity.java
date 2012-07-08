package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import com.konakart.app.KKException;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.OrderIf;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutSeedData;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import java.util.Arrays;
import java.util.List;

public class PaymentMethodActivity extends BaseCheckoutActivity {

    private static final String PAYMENT_METHOD = "paymentMethod";
    private static final String PAYMENT_DETAILS = "paymentDetails";

    @Override
    public void doBeforeRender() throws ActivityException {

        if (!validateCurrentCart()) {
            return;
        }

        // Ensure that the user hasn't submitted the order and then got back to here using the
        // back button. We check to see whether the basket is null
        // Check to see whether there is something in the cart
        CustomerIf cust = KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getCustomerMgr().getCurrentCustomer();

        if (cust.getBasketItems() == null || cust.getBasketItems().length == 0) {
            processorContext.getSeedData().getKkBaseHstComponent().redirectByRefId(
                    processorContext.getSeedData().getRequest(),
                    processorContext.getSeedData().getResponse(),
                    processorContext.getSeedData().getKkBaseHstComponent().getCartDetailRefId());

            return;
        }


        // Get payment gateways from the engine
        try {
            kkAppEng.getOrderMgr().createPaymentGatewayList();

            hstRequest.setAttribute(PAYMENT_DETAILS, kkAppEng.getOrderMgr().getPaymentDetailsArray());
            hstRequest.setAttribute(PAYMENT_METHOD, kkAppEng.getOrderMgr().getPaymentType());

        } catch (KKException e) {
            throw new ActivityException("Failed to retrieve the list of payment gateway", e);
        }

        OrderIf checkoutOrder = kkAppEng.getOrderMgr().getCheckoutOrder();
        processorContext.getSeedData().getRequest().setAttribute(PAYMENT_METHOD, checkoutOrder.getPaymentModuleCode());

    }

    @Override
    public void doAction() throws ActivityException {
        super.doAction();

        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        if (seedData.getAction().equals(KKCheckoutConstants.ACTIONS.SELECT.name())) {
            String paymentMethod = KKUtil.getEscapedParameter(seedData.getRequest(), PAYMENT_METHOD);

            if (StringUtils.isEmpty(paymentMethod)) {
                updateNextLoggedState(KKCheckoutConstants.STATES.PAYMENT_METHOD.name());
                addMessage(GLOBALMESSAGE, seedData.getBundleAsString("checkout.select.payment.method"));
                return;
            }

            // Attach the payment method to the order
            KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().addPaymentDetailsToOrder(paymentMethod);


        }

    }

    @Override
    public void doAdditionalData() {
        super.doAdditionalData();

        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        List<String> acceptedStates = Arrays.asList(KKCheckoutConstants.STATES.PAYMENT_METHOD.name(), KKCheckoutConstants.STATES.ORDER_REVIEW.name());

        String state = seedData.getState();

        if (StringUtils.isNotEmpty(state) && acceptedStates.contains(state)) {
            hstRequest.getRequestContext().setAttribute(getAcceptState().concat("_EDIT"), true);
            hstRequest.setAttribute(getAcceptState().concat("_EDIT"), true);
        }
    }


}
