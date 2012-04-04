package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import org.onehippo.forge.konakart.hst.utils.KKCustomerEventMgr;
import org.onehippo.forge.konakart.hst.wizard.ProcessorContext;

/**
 * Initial activity.
 */
public class InitialActivity extends BaseCheckoutActivity {

    @Override
    public void initialize(ProcessorContext processorContext) {
        super.initialize(processorContext);

        // Insert event
        processorContext.getSeedData().getKkHstComponent().getEventMgr().
                insertCustomerEvent(processorContext.getSeedData().getKkHstComponent().getKkAppEng(),
                        KKCustomerEventMgr.ACTION_ENTER_CHECKOUT);
    }

    @Override
    public boolean acceptState(String state) {
        return (state == null) || super.acceptState(state);
    }

    @Override
    public void doBeforeRender() {
        // do nothing
    }

    @Override
    public void doAction() {
        // do nothing
    }
}
