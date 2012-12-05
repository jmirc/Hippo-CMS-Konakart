package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.app.EmailOptions;
import com.konakart.app.KKException;
import com.konakart.app.PaymentDetails;
import com.konakart.appif.EmailOptionsIf;
import com.konakart.appif.OrderIf;
import com.konakart.appif.PaymentDetailsIf;
import com.konakart.bl.OrderMgr;
import org.onehippo.forge.konakart.hst.utils.KKActionsConstants;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;

import java.math.BigDecimal;

public class OrderReviewActivity extends BaseCheckoutActivity {

  @Override
  public void doBeforeRender() throws ActivityException {

    if (!validateCurrentCart()) {
      return;
    }


    // Call the engine to get the Order Totals
    try {
      kkAppEng.getOrderMgr().populateCheckoutOrderWithOrderTotals();
    } catch (KKException e) {
      throw new ActivityException("Failed to populate the checkout order with order totals", e);
    }

    // Check that order is there and valid
    OrderIf checkoutOrder = kkAppEng.getOrderMgr().getCheckoutOrder();

    if (checkoutOrder == null || checkoutOrder.getStatusTrail() == null) {
      // return to the cart detail page
      String cartDetailRefId = processorContext.getSeedData().getKkBaseHstComponent().getCartDetailRefId();
      processorContext.getSeedData().getKkBaseHstComponent().redirectByRefId(hstRequest, hstResponse, cartDetailRefId);
      return;
    }

        /*
        * Check to see whether the order total is set to 0. Don't bother with a payment gateway
        * if it is.
        */
    BigDecimal orderTotal = checkoutOrder.getTotalIncTax();
    if (orderTotal != null && orderTotal.compareTo(BigDecimal.ZERO) == 0) {

      // Set the order status
      checkoutOrder.setStatus(OrderMgr.PAYMENT_RECEIVED_STATUS);

      try {
        // Save the order
        int orderId = kkAppEng.getOrderMgr().saveOrder(/* sendEmail */true, getEmailOptions(kkAppEng));

        // Update the inventory
        kkAppEng.getOrderMgr().updateInventory(orderId);

        // If we received no exceptions, delete the basket
        kkAppEng.getBasketMgr().emptyBasket();
      } catch (Exception e) {
        log.error("Failed to finalize the payment", e);
      }
    }

    int paymentType = kkAppEng.getOrderMgr().getPaymentType();

    if (paymentType == 0) {
      updateNextLoggedState(KKActionsConstants.STATES.ORDER_REVIEW.name());
    } else {
      updateNextLoggedState(KKActionsConstants.STATES.CHECKOUT_FINISHED.name());
    }

  }

  @Override
  public void doAction() throws ActivityException {
    super.doAction();

    // Get the host name and port number
    // TODO - check if this is needed. At this point, we need more analysis
    String hostAndPort = hstRequest.getServerName() + ":" + hstRequest.getServerPort();

    // Check that order is there and valid
    OrderIf checkoutOrder = kkAppEng.getOrderMgr().getCheckoutOrder();


    try {
      int paymentType = kkAppEng.getOrderMgr().getPaymentType();

      if (paymentType == PaymentDetails.BROWSER_PAYMENT_GATEWAY
          || paymentType == PaymentDetails.BROWSER_IN_FRAME_PAYMENT_GATEWAY) {

                /*
                * This payment gateway is a type where the customer enters the credit card details
                * on a browser window belonging to the gateway. The result is normally returned
                * through a callback. Therefore we don't update the inventory here, but leave it
                * for the callback action which will do it if the payment was approved.
                */
        // Set the order status
        checkoutOrder.setStatus(OrderMgr.WAITING_PAYMENT_STATUS);

        // Save the order
        int orderId = kkAppEng.getOrderMgr().saveOrder(/* sendEmail */true, getEmailOptions(kkAppEng));

        // Get a fully populated PaymentDetails object and attach it to the order
        PaymentDetailsIf pd = kkAppEng.getEng().getPaymentDetails(kkAppEng.getSessionId(),
            checkoutOrder.getPaymentDetails().getCode(), orderId, hostAndPort,
            kkAppEng.getLangId());
        checkoutOrder.setPaymentDetails(pd);

        // Save pd.getCustom1() on the session with key: orderId + "-CUSTOM1"
        kkAppEng.setCustomConfig(Integer.toString(orderId) + "-CUSTOM1", pd.getCustom1());

        // If we received no exceptions, delete the basket
        kkAppEng.getBasketMgr().emptyBasket();

        // TODO - Fix it @see C:\app\konakart\KonaKart-6.3.0.0\custom\appn\src\com\konakart\actions\CheckoutConfirmationSubmitAction.java
//                    if (pd.getPreProcessCode() != null) {
//                        return mapping.findForward(pd.getPreProcessCode());
//                    }
//
//                    if (paymentType == PaymentDetails.BROWSER_IN_FRAME_PAYMENT_GATEWAY) {
//                        return mapping.findForward("CheckoutExternalPaymentFrame");
//                    }
//                    return mapping.findForward("CheckoutExternalPayment");


      } else if (paymentType == PaymentDetails.SERVER_PAYMENT_GATEWAY) {
                /*
                * This payment gateway is a type where the customer enters the credit card details
                * on a browser window belonging to KonaKart. The details are passed to the KonaKart
                * server which communicates with the Gateway server side. A response is returned
                * immediately but we still save the order and chsnge the state later.
                *
                * Some notes on this:
                *
                * -- If we save it after receiving payment notification, something may go wrong and
                * we would have a payment notification for an unknown order.
                *
                * -- If we save it after receiving payment notification, we don't have an order id
                * to send to the gateway. The order id often appears in the email response from the
                * gateway in order to match the response to the order.
                *
                * -- We save the order with a pending status but don't send an email immediately.
                * If the payment is approved, we change the status and then send an email.
                *
                * -- If the payment request is never made, we keep the order in the database with a
                * pending status.
                *
                * -- If the payment is never approved, we keep the order in the database with a
                * payment declined status. If the user made at least one attempt to pay for the
                * order, we should also have an ipnHistory object with details of the gateway
                * transaction.
                */
        // Set the order status
        checkoutOrder.setStatus(OrderMgr.WAITING_PAYMENT_STATUS);

        // Save the order
        int orderId = kkAppEng.getOrderMgr().saveOrder(/* sendEmail */false, null);

        // Get a fully populated PaymentDetails object and attach it to the order
        PaymentDetailsIf pd = kkAppEng.getEng().getPaymentDetails(kkAppEng.getSessionId(),
            checkoutOrder.getPaymentDetails().getCode(), orderId, hostAndPort,
            kkAppEng.getLangId());
        checkoutOrder.setPaymentDetails(pd);

        // Save pd.getCustom1() on the session with key: orderId + "-CUSTOM1"
        kkAppEng.setCustomConfig(Integer.toString(orderId) + "-CUSTOM1", pd.getCustom1());

        // TODO - Fix it @see C:\app\konakart\KonaKart-6.3.0.0\custom\appn\src\com\konakart\actions\CheckoutConfirmationSubmitAction.java
        // return mapping.findForward("CheckoutServerPayment");


      } else if (paymentType == PaymentDetails.COD) {
                /*
                * Cash On Delivery. The order is saved with a pending status and the inventory is
                * updated.
                */

        // Set the order status
        checkoutOrder.setStatus(OrderMgr.PENDING_STATUS);

        // Save the order
        int orderId = kkAppEng.getOrderMgr().saveOrder(/* sendEmail */true,
            getEmailOptions(kkAppEng));

        // Update the inventory
        kkAppEng.getOrderMgr().updateInventory(orderId);

        // If we received no exceptions, delete the basket
        kkAppEng.getBasketMgr().emptyBasket();
      } else {
        throw new ActivityException("This Payment Type is not supported");
      }


    } catch (KKException e) {
      throw new ActivityException("Failed during the payment method", e);
    } catch (KKAppException e) {
      throw new ActivityException("Failed during the payment method", e);
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
