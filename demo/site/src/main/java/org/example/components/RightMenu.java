package org.example.components;

import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.components.KKBaseHstComponent;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

public class RightMenu extends KKBaseHstComponent {


    @Override
    public void doAction(HstRequest request, HstResponse response) throws HstComponentException {
        super.doAction(request, response);

        String username = KKUtil.getEscapedParameter(request, "username");
        String password = KKUtil.getEscapedParameter(request, "password");

        KKServiceHelper.getKKEngineService().loggedIn(request, response, username, password);
    }
}
