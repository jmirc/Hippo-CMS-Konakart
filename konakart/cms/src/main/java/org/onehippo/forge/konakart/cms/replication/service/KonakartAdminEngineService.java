/*
 * =========================================================
 * Hippo CMS - Konakart
 * https://bitbucket.org/jmirc/hippo-cms-konakart
 * =========================================================
 * Copyright 2012
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================================================
 */

package org.onehippo.forge.konakart.cms.replication.service;

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
