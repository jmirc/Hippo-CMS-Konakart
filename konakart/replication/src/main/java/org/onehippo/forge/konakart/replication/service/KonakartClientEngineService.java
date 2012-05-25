package org.onehippo.forge.konakart.replication.service;

import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.Plugin;
import org.hippoecm.frontend.plugin.config.IPluginConfig;

public class KonakartClientEngineService extends Plugin {

    public KonakartClientEngineService(IPluginContext context, IPluginConfig config) {
        super(context, config);

        initializeKonakartEngine();

    }


    private void initializeKonakartEngine() {

    }
}
