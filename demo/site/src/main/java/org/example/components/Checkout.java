package org.example.components;

import org.onehippo.forge.konakart.hst.components.KKCheckout;

public class Checkout extends KKCheckout {


    @Override
    protected String getProcessorName() {
        return "checkoutProcessor";
    }
}
