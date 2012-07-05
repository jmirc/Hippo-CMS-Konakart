package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import com.google.common.collect.Lists;
import com.konakart.al.NotifiedProductItem;
import com.konakart.app.KKException;
import com.konakart.appif.BasketIf;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.OrderIf;
import com.konakart.appif.OrderProductIf;
import org.hippoecm.hst.component.support.forms.FormField;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.onehippo.forge.konakart.site.service.impl.KKEventServiceImpl;

import java.util.List;

public class CheckoutFinishedActivity extends BaseCheckoutActivity {

    public static final String REMOVE = "remove_";

    public static final String GLOBAL_PRODUCT_NOTIFIER_ENABLED = "globalProductNotifierEnabled";
    public static final String NOTIFIED_PRODUCTS = "notifiedProducts";

    @Override
    public void doBeforeRender() throws ActivityException {

        // Set events
        OrderIf order = kkAppEng.getOrderMgr().getCheckoutOrder();
        if (order != null) {
            KKServiceHelper.getKKEventService().
                    insertCustomerEvent(hstRequest, KKEventServiceImpl.ACTION_CONFIRM_ORDER, order.getId());
            KKServiceHelper.getKKEventService().
                    insertCustomerEvent(hstRequest, KKEventServiceImpl.ACTION_PAYMENT_METHOD_SELECTED, order.getPaymentModuleCode());
        }

        CustomerIf currentCustomer = kkAppEng.getCustomerMgr().getCurrentCustomer();

        if ((currentCustomer != null) && (currentCustomer.getType() != 2)) {
            if (currentCustomer.getGlobalProdNotifier() == 0) {

                OrderIf currentOrder = kkAppEng.getOrderMgr().getCheckoutOrder();

                if (currentOrder != null && currentOrder.getOrderProducts() != null) {

                    List<NotifiedProductItem> notifiedProductItems = Lists.newArrayList();

                    for (OrderProductIf orderProductIf : currentOrder.getOrderProducts()) {
                        NotifiedProductItem notifiedProductItem = new NotifiedProductItem(orderProductIf.getProductId(),
                                orderProductIf.getName());
                        notifiedProductItem.setRemove(false);
                        notifiedProductItems.add(notifiedProductItem);
                    }

                    hstRequest.setAttribute(NOTIFIED_PRODUCTS, notifiedProductItems);
                }

                hstRequest.setAttribute(GLOBAL_PRODUCT_NOTIFIER_ENABLED, true);
            }
        }
    }

    @Override
    public void doAction() throws ActivityException {
        super.doAction();

        OrderIf currentOrder = kkAppEng.getOrderMgr().getCheckoutOrder();

        if (currentOrder != null) {
            OrderProductIf[] orderProductIfs = currentOrder.getOrderProducts();

            if (orderProductIfs != null) {
                String[] definedFormFields = new String[orderProductIfs.length];

                int i = 0;

                for (OrderProductIf orderProductIf : orderProductIfs) {
                    definedFormFields[i] = REMOVE + orderProductIf.getId();
                    i++;
                }

                FormMap formMap = new FormMap(hstRequest, definedFormFields);

                for (OrderProductIf orderProductIf : orderProductIfs) {
                    FormField removeFormField = formMap.getField(REMOVE + orderProductIf.getId());

                    // Remove the basket item
                    if (removeFormField != null && removeFormField.getValues() != null
                            && removeFormField.getValues().size() > 0) {

                        try {
                            kkAppEng.getCustomerMgr().addProductNotificationsToCustomer(orderProductIf.getId());
                        } catch (KKException e) {
                            log.warn("Failed to notify the customer about the availability of the product", e);
                        }
                    }
                }
            }
        }

        // Remove checkout order
        kkAppEng.getOrderMgr().setCheckoutOrder(null);

        // Update the order history array
        try {
            kkAppEng.getProductMgr().fetchOrderHistoryArray();
        } catch (KKException e) {
            log.warn("Failed to fetch the orders' history ");
        }


        processorContext.getSeedData().getKkBaseHstComponent().redirectByRefId(hstRequest, hstResponse,
                processorContext.getSeedData().getKkBaseHstComponent().getMyAccountRefId());

    }
}