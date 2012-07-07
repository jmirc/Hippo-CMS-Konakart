package org.onehippo.forge.konakart.hst.components;

import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.utils.KKComponentUtils;

public class KKMyAccount extends KKBaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        KKComponentUtils.setLoginAttributes(request);
    }
}
