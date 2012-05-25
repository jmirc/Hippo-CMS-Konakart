package org.onehippo.forge.konakart.site.container;

import com.konakart.al.KKAppEng;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.container.Valve;
import org.hippoecm.hst.core.container.ValveContext;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.hst.channel.KonakartSiteInfo;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class KonakartValve implements Valve {


    @Override
    public void initialize() throws ContainerException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void invoke(ValveContext context) throws ContainerException {
        HttpServletRequest servletRequest = context.getServletRequest();
        HttpServletResponse servletResponse = context.getServletResponse();
        HstRequestContext requestContext = context.getRequestContext();

        if (KKAppEng.getEngConf() == null) {
            // Initialize the Engine Conf
            Mount mount = requestContext.getResolvedMount().getMount();
            KonakartSiteInfo siteInfo = mount.getChannelInfo();

            if (siteInfo != null) {
                try {
                    KKEngine.init(KKServiceHelper.getEngineMode(), KKServiceHelper.getCustomersShared(),
                            KKServiceHelper.getProductsShared());
                } catch (Exception e) {
                    throw new ContainerException("Failed to initialize the Konakart Engine.", e);
                }
            }
        }

        // Retrieve the Konakart client
        KKAppEng kkAppEng = KKServiceHelper.getKKEngineService().getKKAppEng(servletRequest);

        // Initialize the konakart client if it has not been created
        if (kkAppEng == null) {
            // Initialize Konakart Engine
            kkAppEng = KKServiceHelper.getKKEngineService().initKKEngine(servletRequest, servletResponse);
        }

        // Validate the current konakart session
        KKServiceHelper.getKKEngineService().validKKSession(servletRequest, servletResponse);

        // Set the konakart client
        servletRequest.setAttribute(KKAppEng.KONAKART_KEY, kkAppEng);

    }
}
