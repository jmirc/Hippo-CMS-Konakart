package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import com.konakart.al.KKAppEng;
import com.konakart.app.EmailOptions;
import com.konakart.app.KKException;
import com.konakart.appif.EmailOptionsIf;
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
        // Get payment gateways from the engine
        try {
            kkAppEng.getOrderMgr().createPaymentGatewayList();

            hstRequest.setAttribute(PAYMENT_DETAILS, kkAppEng.getOrderMgr().getPaymentDetailsArray());
            hstRequest.setAttribute(PAYMENT_METHOD, kkAppEng.getOrderMgr().getPaymentType());

        } catch (KKException e) {
            throw new ActivityException("Failed to retrieve the list of payment gateway", e);
        }


//        // Ensure that the user hasn't submitted the order and then got back to here using the
//        // back button. We check to see whether the basket is null
//        // Check to see whether there is something in the cart
//        CustomerIf cust = KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getCustomerMgr().getCurrentCustomer();
//
//        if (cust.getBasketItems() == null || cust.getBasketItems().length == 0) {
//            processorContext.getSeedData().getKkBaseHstComponent().redirectByRefId(
//                    processorContext.getSeedData().getRequest(),
//                    processorContext.getSeedData().getResponse(),
//                    processorContext.getSeedData().getKkBaseHstComponent().getCartDetailRefId());
//
//            return;
//        }
//
//        /*
//         * Check to see whether the order total is set to 0. Don't bother with a payment gateway
//         * if it is.
//         */
//        OrderIf checkoutOrder = kkAppEng.getOrderMgr().getCheckoutOrder();
//
//        BigDecimal orderTotal = checkoutOrder.getTotalIncTax();
//        if (orderTotal != null && orderTotal.compareTo(java.math.BigDecimal.ZERO) == 0) {
//
//            // Set the order status
//            checkoutOrder.setStatus(com.konakart.bl.OrderMgr.PAYMENT_RECEIVED_STATUS);
//
//            try {
//                // Save the order
//                int orderId = kkAppEng.getOrderMgr().saveOrder(/* sendEmail */true, getEmailOptions(kkAppEng));
//
//                // Update the inventory
//                kkAppEng.getOrderMgr().updateInventory(orderId);
//
//                // If we received no exceptions, delete the basket
//                kkAppEng.getBasketMgr().emptyBasket();
//
//                setNextLoggedState(STATES.ORDER_REVIEW.name());
//            } catch (Exception e) {
//                log.error("Failed to finalize the payment", e);
//            }
//        }
//
//        // Get the host name and port number
//        // TODO - check if this is needed. At this point, we need more analysis
//        String hostAndPort = processorContext.getSeedData().getRequest().getServerName() + ":" +
//                processorContext.getSeedData().getRequest().getServerPort();
//
//        int paymentType = kkAppEng.getOrderMgr().getPaymentType();
//        processorContext.getSeedData().getRequest().setAttribute(PAYMENT_METHOD, paymentType);
//
//        try {
//            if (paymentType == PaymentDetails.BROWSER_PAYMENT_GATEWAY
//                    || paymentType == PaymentDetails.BROWSER_IN_FRAME_PAYMENT_GATEWAY) {
//
//                /*
//                * This payment gateway is a type where the customer enters the credit card details
//                * on a browser window belonging to the gateway. The result is normally returned
//                * through a callback. Therefore we don't update the inventory here, but leave it
//                * for the callback action which will do it if the payment was approved.
//                */
//
//                // Set the order status
//                checkoutOrder.setStatus(com.konakart.bl.OrderMgr.WAITING_PAYMENT_STATUS);
//
//                // Save the order
//                int orderId = kkAppEng.getOrderMgr().saveOrder(/* sendEmail */true, getEmailOptions(kkAppEng));
//
//                // Get a fully populated PaymentDetails object and attach it to the order
//                PaymentDetailsIf pd = kkAppEng.getEng().getPaymentDetails(kkAppEng.getSessionId(),
//                        checkoutOrder.getPaymentDetails().getCode(), orderId, hostAndPort,
//                        kkAppEng.getLangId());
//                checkoutOrder.setPaymentDetails(pd);
//
//                // If we received no exceptions, delete the basket
//                kkAppEng.getBasketMgr().emptyBasket();
//            } else if (paymentType == PaymentDetails.SERVER_PAYMENT_GATEWAY) {
//                /*
//                 * This payment gateway is a type where the customer enters the credit card details
//                 * on a browser window belonging to KonaKart. The details are passed to the KonaKart
//                 * server which communicates with the Gateway server side. A response is returned
//                 * immediately but we still save the order and chsnge the state later.
//                 *
//                 * Some notes on this:
//                 *
//                 * -- If we save it after receiving payment notification, something may go wrong and
//                 * we would have a payment notification for an unknown order.
//                 *
//                 * -- If we save it after receiving payment notification, we don't have an order id
//                 * to send to the gateway. The order id often appears in the email response from the
//                 * gateway in order to match the response to the order.
//                 *
//                 * -- We save the order with a pending status but don't send an email immediately.
//                 * If the payment is approved, we change the status and then send an email.
//                 *
//                 * -- If the payment request is never made, we keep the order in the database with a
//                 * pending status.
//                 *
//                 * -- If the payment is never approved, we keep the order in the database with a
//                 * payment declined status. If the user made at least one attempt to pay for the
//                 * order, we should also have an ipnHistory object with details of the gateway
//                 * transaction.
//                 */
//                // Set the order status
//                checkoutOrder.setStatus(com.konakart.bl.OrderMgr.WAITING_PAYMENT_STATUS);
//
//                // Save the order
//                int orderId = kkAppEng.getOrderMgr().saveOrder(/* sendEmail */false, null);
//
//                // Get a fully populated PaymentDetails object and attach it to the order
//                PaymentDetailsIf pd = kkAppEng.getEng().getPaymentDetails(kkAppEng.getSessionId(),
//                        checkoutOrder.getPaymentDetails().getCode(), orderId, hostAndPort,
//                        kkAppEng.getLangId());
//                checkoutOrder.setPaymentDetails(pd);
//            } else if (paymentType == PaymentDetails.COD) {
//                /*
//                * Cash On Delivery. The order is saved with a pending status and the inventory is
//                * updated.
//                */
//
//                // Set the order status
//                checkoutOrder.setStatus(com.konakart.bl.OrderMgr.PENDING_STATUS);
//
//                // Save the order
//                int orderId = kkAppEng.getOrderMgr().saveOrder(/* sendEmail */true,
//                        getEmailOptions(kkAppEng));
//
//                // Update the inventory
//                kkAppEng.getOrderMgr().updateInventory(orderId);
//
//                // If we received no exceptions, delete the basket
//                kkAppEng.getBasketMgr().emptyBasket();
//
//                // Display the order review
//                setNextLoggedState(STATES.ORDER_REVIEW.name());
//            } else {
//                throw new ActivityException("This Payment Type is not supported");
//            }
//
//
//        } catch (KKException e) {
//            throw new ActivityException("Failed during the payment method", e);
//        } catch (KKAppException e) {
//            throw new ActivityException("Failed during the payment method", e);
//        }

    }

    @Override
    public void doAction() throws ActivityException {
        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        if (seedData.getAction().equals(KKCheckoutConstants.ACTIONS.SELECT.name())) {
            String paymentMethod = KKUtil.getEscapedParameter(seedData.getRequest(), PAYMENT_METHOD);

            if (StringUtils.isEmpty(paymentMethod)) {
                setNextLoggedState(STATES.PAYMENT_METHOD.name());
                addMessage(GLOBALMESSAGE, seedData.getBundle().getString("checkout.select.payment.method"));
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

        List<String> acceptedStates = Arrays.asList(STATES.PAYMENT_METHOD.name(), STATES.ORDER_REVIEW.name());

        String state = seedData.getState();

        if (StringUtils.isNotEmpty(state) && acceptedStates.contains(state)) {
            hstRequest.getRequestContext().setAttribute(getAcceptState().concat("_EDIT"), true);
            hstRequest.setAttribute(getAcceptState().concat("_EDIT"), true);
        }
    }

    /**
     * Instantiate an EmailOptions object. Edit this method if you have installed Enterprise
     * Extensions and want to attach an invoice to the eMail.
     *
     * @param kkAppEng .
     * @return Returns a populated EmailOptions object
     */
    protected EmailOptionsIf getEmailOptions(KKAppEng kkAppEng) {
        EmailOptionsIf options = new EmailOptions();
        options.setCountryCode(kkAppEng.getLocale().substring(0, 2));
        options.setTemplateName("OrderConfReceived");

        // Attach the invoice to the confirmation email (Enterprise Only). Defaults to false.
        // options.setAttachInvoice(true);

        // Create the invoice (if not already present) for attaching to the confirmation email
        // (Enterprise Only). Defaults to false.
        // options.setCreateInvoice(true);

        return options;
    }
}
