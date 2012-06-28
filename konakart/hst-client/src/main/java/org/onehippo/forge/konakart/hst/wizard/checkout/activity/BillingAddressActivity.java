package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import com.konakart.app.KKException;
import com.konakart.app.OrderStatusHistory;
import com.konakart.appif.OrderIf;
import com.konakart.appif.OrderStatusHistoryIf;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutProcessContext;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutSeedData;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import java.util.Arrays;
import java.util.List;

public class BillingAddressActivity extends BaseAddressActivity {

    public static final String SHIPPING_ADDRESS = "shippingAddress";
    public static final String SELECT_SAME_SHIPPING_ADDRESS = "same";


    @Override
    public void doAction() throws ActivityException {
        super.doAction();

        CheckoutProcessContext checkoutProcessContext = (CheckoutProcessContext) processorContext;
        CheckoutSeedData seedData = checkoutProcessContext.getSeedData();


        if (seedData.getAction().equals(KKCheckoutConstants.ACTIONS.SELECT.name())) {
            Integer addressId = Integer.valueOf(KKUtil.getEscapedParameter(seedData.getRequest(), ADDRESS));
            String shippingAddress = KKUtil.getEscapedParameter(seedData.getRequest(), SHIPPING_ADDRESS);

            // Create a new address
            if (addressId == -1) {
                try {
                    addressId = KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getCustomerMgr().addAddressToCustomer(createAddressForCustomer());
                } catch (Exception e) {
                    setNextLoggedState(STATES.INITIAL.name());
                    addMessage(GLOBALMESSAGE, seedData.getBundle().getString("checkout.failed.create.address"));
                    return;
                }
            }

            KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().setCheckoutOrderBillingAddress(addressId);

            if (shippingAddress.equals(SELECT_SAME_SHIPPING_ADDRESS)) {
                try {
                    KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().setCheckoutOrderShippingAddress(addressId);

                    // Skip the SHIPPING ADDRESS step because the customer has decided to use the
                    // same billing address
                    setNextLoggedState(STATES.SHIPPING_METHOD.name());
                } catch (KKException e) {
                    log.error("Failed to set the shipping address", e);
                }
            }

            OrderIf checkoutOrder = KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().getCheckoutOrder();

            // Set the comment
            OrderStatusHistoryIf osh = new OrderStatusHistory();
            // TODO CAN SET COMMENTS
            osh.setComments("");
            OrderStatusHistoryIf[] oshArray = new OrderStatusHistoryIf[1];
            oshArray[0] = osh;
            osh.setUpdatedById(kkAppEng.getOrderMgr().getIdForUserUpdatingOrder(checkoutOrder));
            checkoutOrder.setStatusTrail(oshArray);
        }
    }

    @Override
    public void doAdditionalData() {

        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        List<String> acceptedStates = Arrays.asList(STATES.SHIPPING_ADDRESS.name(), STATES.SHIPPING_METHOD.name(),
                STATES.PAYMENT_METHOD.name(), STATES.ORDER_REVIEW.name());

        String state = seedData.getState();

        if (StringUtils.isNotEmpty(state) && acceptedStates.contains(state)) {
            hstRequest.getRequestContext().setAttribute(getAcceptState().concat("_EDIT"), true);
            hstRequest.setAttribute(getAcceptState().concat("_EDIT"), true);
        }

    }
}
