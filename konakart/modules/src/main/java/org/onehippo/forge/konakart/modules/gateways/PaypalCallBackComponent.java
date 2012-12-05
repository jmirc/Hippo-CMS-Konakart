package org.onehippo.forge.konakart.modules.gateways;

import com.konakart.al.KKAppEng;
import com.konakart.app.IpnHistory;
import com.konakart.app.KKException;
import com.konakart.app.OrderUpdate;
import com.konakart.appif.*;
import com.konakart.bl.ConfigConstants;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.components.KKGatewayCallBackComponent;

import java.net.HttpURLConnection;
import java.util.Enumeration;
import java.util.List;

public class PaypalCallBackComponent extends KKGatewayCallBackComponent {

    // Module name must be the same as the class name although it can be all in lowercase in order
    // to remain compatible with osCommerce.
    private static final String code = "paypal";

    // PayPal constants
    private static final String custom = "custom";

    private static final String payment_status = "payment_status";

    private static final String txn_id = "txn_id";

    private static final String completed = "Completed";

    // Return codes and descriptions
    private static final int RET0 = 0;

    private static final String RET0_DESC = "Transaction OK";

    private static final int RET4 = -4;

    private static final String RET4_DESC = "There has been an unexpected exception. Please look at the log.";

    // Order history comments. These comments are associated with the order.
    private static final String ORDER_HISTORY_COMMENT_OK = "PayPal payment successful. PayPal TransactionId = ";

    private static final String ORDER_HISTORY_COMMENT_KO = "PayPal payment not successful. PayPal Payment Status = ";


    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        String paymentStatus = null, txnId = null;

        if (log.isDebugEnabled()) {
            log.debug("*********** PayPal Callback");
        }

        // Create thes outside of try / catch since they are needed in the case of a general
        // exception
        IpnHistoryIf ipnHistory = new IpnHistory();
        ipnHistory.setOrderId(-1);
        ipnHistory.setModuleCode(code);

        String sessionId = null;

        KKAppEng kkAppEng = null;

        try {
            if (request == null) {
                return;
            }

            /*
             * Get the uuid from the request so that we can look up the SSO Token
             */
            String uuid = request.getParameter(PaypalCallBackComponent.custom);
            if (uuid == null) {
                throw new Exception(
                        "The callback from PayPal did not contain the 'custom' parameter.");
            }

            // Get an instance of the KonaKart engine
            kkAppEng = this.getKKAppEng(request);

            SSOTokenIf token = kkAppEng.getEng().getSSOToken(uuid, /* deleteToken */true);
            if (token == null) {
                throw new Exception("The SSOToken from the PayPal callback is null");
            }

            try {
                // Get the order id from custom1
                int orderId = Integer.parseInt(token.getCustom1());
                ipnHistory.setOrderId(orderId);
            } catch (Exception e) {
                throw new Exception("The SSOToken does not contain an order id");
            }

            /*
             * Use the session of the logged in user to initialise kkAppEng
             */
            try {
                kkAppEng.getEng().checkSession(token.getSessionId());
            } catch (KKException e) {
                throw new Exception(
                        "The SessionId from the SSOToken in the PayPal Callback is not valid: "
                                + token.getSessionId());
            }

            // Log in the user
            kkAppEng.getCustomerMgr().loginBySession(token.getSessionId());
            sessionId = token.getSessionId();

            // See if we need to send an email, by looking at the configuration
            String sendEmailsConfig = kkAppEng.getConfig(ConfigConstants.SEND_EMAILS);
            boolean sendEmail = false;
            if (sendEmailsConfig != null && sendEmailsConfig.equalsIgnoreCase("true")) {
                sendEmail = true;
            }

            // Process the parameters sent in the callback
            StringBuilder sb = new StringBuilder();
            Enumeration<?> en = request.getParameterNames();
            while (en.hasMoreElements()) {
                String paramName = (String) en.nextElement();
                String paramValue = request.getParameter(paramName);
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(paramName);
                sb.append(" = ");
                sb.append(paramValue);

                // Capture important variables so that we can determine whether the transaction
                // was successful or not
                if (paramName != null) {
                    if (paramName.equalsIgnoreCase(PaypalCallBackComponent.payment_status)) {
                        paymentStatus = paramValue;
                    } else if (paramName.equalsIgnoreCase(PaypalCallBackComponent.txn_id)) {
                        txnId = paramValue;
                    }
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("PayPal CallBack data:");
                log.debug(sb.toString());
            }

            // If successful, we update the inventory as well as changing the state of the
            // order.
            OrderUpdateIf updateOrder = new OrderUpdate();
            updateOrder.setUpdatedById(kkAppEng.getActiveCustId());

            String comment;
            if (paymentStatus != null && paymentStatus.equalsIgnoreCase(completed)) {
                comment = ORDER_HISTORY_COMMENT_OK + txnId;
                kkAppEng.getEng().updateOrder(sessionId, ipnHistory.getOrderId(),
                        com.konakart.bl.OrderMgr.PAYMENT_RECEIVED_STATUS, sendEmail, comment,
                        updateOrder);
                // If the order payment was approved we update the inventory
                kkAppEng.getEng().updateInventory(sessionId, ipnHistory.getOrderId());
                if (sendEmail) {
                    sendOrderConfirmationMail(kkAppEng, ipnHistory.getOrderId(), /* success */
                            true);
                }
            } else {
                comment = ORDER_HISTORY_COMMENT_KO + paymentStatus;
                kkAppEng.getEng().updateOrder(sessionId, ipnHistory.getOrderId(),
                        com.konakart.bl.OrderMgr.PAYMENT_DECLINED_STATUS, sendEmail, comment,
                        updateOrder);
                if (sendEmail) {
                    sendOrderConfirmationMail(kkAppEng, ipnHistory.getOrderId(), /* success */
                            false);
                }
            }

            ipnHistory.setKonakartResultDescription(RET0_DESC);
            ipnHistory.setKonakartResultId(RET0);
            kkAppEng.getEng().saveIpnHistory(sessionId, ipnHistory);

        } catch (Exception e) {
            try {
                if (sessionId != null) {
                    ipnHistory.setKonakartResultDescription(RET4_DESC);
                    ipnHistory.setKonakartResultId(RET4);
                    if (kkAppEng != null) {
                        kkAppEng.getEng().saveIpnHistory(sessionId, ipnHistory);
                    }
                }
            } catch (KKException e1) {
                log.warn("Failed to execute the Paypal components", e);
            }
            log.error("Failed to execute the Paypal components", e);
        }
    }

    @Override
    protected void customizeConnection(HttpURLConnection connection, PaymentDetailsIf pd, List<NameValueIf> paramList) {
        // no customization connection because a redirect must be done.
    }
}
