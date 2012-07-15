package org.onehippo.cms7.hst.hippokart.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.utils.KKComponentUtils;

public class LeftMenu  extends BaseHstComponent{

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) throws HstComponentException {

        KKComponentUtils.setCategoriesFacet(request);
    }

    @Override
    public void doAction(HstRequest request, HstResponse response) throws HstComponentException {
        super.doAction(request, response);
    }
}
