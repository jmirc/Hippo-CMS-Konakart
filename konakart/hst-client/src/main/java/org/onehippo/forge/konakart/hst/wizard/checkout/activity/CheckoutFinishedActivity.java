package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import com.konakart.appif.OrderIf;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.onehippo.forge.konakart.site.service.impl.KKEventServiceImpl;

public class CheckoutFinishedActivity extends BaseCheckoutActivity {

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
    }
}
