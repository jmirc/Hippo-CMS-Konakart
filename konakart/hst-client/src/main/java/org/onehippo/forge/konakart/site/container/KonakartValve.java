package org.onehippo.forge.konakart.site.container;

import com.konakart.al.KKAppEng;
import org.apache.cxf.common.util.StringUtils;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.container.ContainerException;
import org.hippoecm.hst.core.container.Valve;
import org.hippoecm.hst.core.container.ValveContext;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
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

        Session jcrSession;

        try {
            jcrSession = context.getRequestContext().getSession();
        } catch (RepositoryException e) {
            throw new IllegalStateException("Failed to retrieve the Jcr Session", e);
        }

        // Pre-load the checkout activities
        try {
            HippoModuleConfig.getConfig().preLoadActivityList(jcrSession);
        } catch (RepositoryException e) {
            throw new IllegalStateException("Failed to load the list of activities. ", e);
        }

        // Initialize internal Konakart Engine configuration
        KKEngine.init(jcrSession);

        // Retrieve the Konakart client
        KKAppEng kkAppEng = KKServiceHelper.getKKEngineService().getKKAppEng(servletRequest);

        // Initialize the konakart client if it has not been created
        // TODO: how to handle multiple stores??????
        if (kkAppEng == null) {
            // Set the current store config
            Mount resolvedMount = context.getRequestContext().getResolvedMount().getMount();
            String storeName = resolvedMount.getProperty(KKCndConstants.KONAKART_CONFIG_STORE_NAME);

            //  Set the default one
            if (StringUtils.isEmpty(storeName)) {
                storeName = KKCheckoutConstants.DEF_STORE_ID;
            }


            // Check if the store config has been created under /hippo-configuration/cms-services/KonakartSynchronizationService
            KKStoreConfig kkStoreConfig;
            try {
                kkStoreConfig = HippoModuleConfig.getConfig().getStoreConfigByName(jcrSession, storeName);

                servletRequest.setAttribute(KKStoreConfig.KK_STORE_CONFIG, kkStoreConfig);
            } catch (RepositoryException e) {
                throw new IllegalStateException("Failed to load the storeConfig. Please verify if a new storeConfig named "
                        + storeName + " within /hippo-configuration/cms-services/KonakartSynchronizationService");
            }

            // Initialize Konakart Engine
            kkAppEng = KKServiceHelper.getKKEngineService().initKKEngine(servletRequest, servletResponse, kkStoreConfig);
        }

        // Validate the current konakart session
        KKServiceHelper.getKKEngineService().validKKSession(servletRequest, servletResponse);

        // Set the konakart client
        servletRequest.setAttribute(KKAppEng.KONAKART_KEY, kkAppEng);


        // Instantiate the next context
        context.invokeNext();
    }
}
