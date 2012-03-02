package org.onehippo.forge.konakart.hst.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.utils.KKUtil;

public abstract class KKHstActionComponent extends KKHstComponent {

    protected static final String ACTION = "action";

    @Override
    final public void doAction(HstRequest request, HstResponse response) {

        String type = KKUtil.getEscapedParameter(request, ACTION);

        doAction(type, request, response);
    }

    public abstract void doAction(String action, HstRequest request, HstResponse response);
}
