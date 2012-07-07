package org.onehippo.forge.konakart.cms.replication.service;

import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.Plugin;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.session.UserSession;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KonakartAdminEngineService extends Plugin {

    private static Logger log = LoggerFactory.getLogger(KonakartAdminEngineService.class);

    public KonakartAdminEngineService(IPluginContext context, IPluginConfig config) {
        super(context, config);

        initializeKonakartEngine();
    }

    private void initializeKonakartEngine() {

        try {
            // Retrieve the Wicket Session
            UserSession userSession = (UserSession) org.apache.wicket.Session.get();

            // Initialize the konakart engine
            KKAdminEngine.getInstance().init(userSession.getJcrSession());
        } catch (Exception e) {
            log.error("Failed to initialize the Konakart engine");
        }
    }
}
