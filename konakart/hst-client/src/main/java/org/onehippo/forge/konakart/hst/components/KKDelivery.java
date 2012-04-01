package org.onehippo.forge.konakart.hst.components;

import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

public class KKDelivery extends KKHstActionComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        // TODO add reward points - see EditCartSubmitAction.java


    }

    @Override
    public void doAction(String action, HstRequest request, HstResponse response) {

    }
}
