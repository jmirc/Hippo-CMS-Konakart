package org.onehippo.cms7.hst.components;

import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.components.KKMyAccount;

public class MyAccount extends KKMyAccount {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        if (isGuestCustomer(request)) {
            response.setRenderPath("jsp/myaccount/main/login.jsp");
        }
    }
}
