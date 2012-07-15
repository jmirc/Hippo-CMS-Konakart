package org.onehippo.cms7.hst.hippokart.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

public class Nav extends BaseHstComponent {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {

        request.setAttribute("menu",request.getRequestContext().getHstSiteMenus().getSiteMenu("main"));
    }
}
