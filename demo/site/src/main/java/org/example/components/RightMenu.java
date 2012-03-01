package org.example.components;

import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.components.KKHstComponent;
import org.onehippo.forge.konakart.hst.utils.KKUtil;

public class RightMenu extends KKHstComponent {


    @Override
    public void doAction(HstRequest request, HstResponse response) throws HstComponentException {
        super.doAction(request, response);

        String username = KKUtil.getEscapedParameter(request, "username");
        String password = KKUtil.getEscapedParameter(request, "password");

        super.loggedIn(request, response, username, password);
    }
}
