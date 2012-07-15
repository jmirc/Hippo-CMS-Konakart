package org.onehippo.cms7.hst.hippokart.components;

import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.cms7.hst.hippokart.channels.WebsiteInfo;
import org.onehippo.forge.konakart.hst.utils.KKComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Header extends BaseHstComponent {

    public static final Logger log = LoggerFactory.getLogger(Header.class);

    @Override
    public void doBeforeRender(final HstRequest request, final HstResponse response) throws HstComponentException {
        final Mount mount = request.getRequestContext().getResolvedMount().getMount();
        final WebsiteInfo info = mount.getChannelInfo();

        if (info != null) {
            request.setAttribute("headerName", info.getHeaderName());
        } else {
            log.warn("No channel info available for website '{}'", mount.getMountPath());
        }

        KKComponentUtils.setGlobalKonakartAttributes(request);

        request.setAttribute("menu",request.getRequestContext().getHstSiteMenus().getSiteMenu("quicklinks"));
    }

}
