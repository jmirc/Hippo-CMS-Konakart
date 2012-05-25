package org.onehippo.forge.konakart.replication.service;

import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.Plugin;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KonakartAdminEngineService extends Plugin {

    private static Logger log = LoggerFactory.getLogger(KonakartAdminEngineService.class);

    public KonakartAdminEngineService(IPluginContext context, IPluginConfig config) {
        super(context, config);

        initializeKonakartEngine(config);
    }

    private void initializeKonakartEngine(IPluginConfig config) {

        int engineMode = (int) config.getLong("enginemode");
        String username = config.getString("admin.username");
        String password = config.getString("admin.password");


        try {
            KKAdminEngine.getInstance().init(engineMode, username, password);
        } catch (Exception e) {
            log.error("Failed to initialize the Konakart engine");
        }
    }
}
