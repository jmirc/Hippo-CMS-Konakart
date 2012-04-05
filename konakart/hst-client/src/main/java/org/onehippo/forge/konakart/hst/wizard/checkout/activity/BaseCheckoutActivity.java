package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import org.onehippo.forge.konakart.hst.wizard.BaseActivity;

public abstract class BaseCheckoutActivity extends BaseActivity {

    public static enum STATES {
        INITIAL, CHECKOUT_METHOD_REGISTER, BILLING_ADDRESS,
        SHIPPING_ADDRESS, SHIPPING_METHOD, PAYMENT_METHOD, ORDER_REVIEW
    }

    public static enum ACTIONS {
        LOGIN, SELECT
    }
}
