package org.onehippo.forge.konakart.hst.wizard.checkout;

import org.onehippo.forge.konakart.hst.wizard.SeedData;

public class CheckoutSeedData extends SeedData {

    private String action;
    private String state;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getState() {
        return state;
    }

    public void setState(String sate) {
        this.state = sate;
    }
}
