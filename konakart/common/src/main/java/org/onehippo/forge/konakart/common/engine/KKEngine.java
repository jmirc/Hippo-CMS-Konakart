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

package org.onehippo.forge.konakart.common.engine;

import com.konakart.al.KKAppEng;
import com.konakart.al.StoreInfo;
import com.konakart.app.EngineConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;

import javax.jcr.Session;

public final class KKEngine {

    private static final String KONAKART_PROPERTIES = "konakart.properties";
    private static final String KONAKART_APP_PROPERTIES = "konakart_app.properties";

    private KKEngine() {
    }

    /**
     * Configure the Engine Config
     * @throws IllegalStateException thrown if the KonakartEngine is not able to start.
     * @param session the Jcr Session
     */
    static public void init(Session session) {

        // Initialize the Engine conf if not exits
        if (KKAppEng.getEngConf() == null) {
            KKEngineConfig engineConfig = HippoModuleConfig.getConfig().getClientEngineConfig(session);

            // Initialize the engine conf
            EngineConfig engConf = new EngineConfig();
            engConf.setMode(engineConfig.getEngineMode());
            engConf.setCustomersShared(engineConfig.isCustomersShared());
            engConf.setProductsShared(engineConfig.isProductsShared());
            engConf.setPropertiesFileName(KONAKART_PROPERTIES);
            engConf.setAppPropertiesFileName(KONAKART_APP_PROPERTIES);

            try {
                new KKAppEng(engConf);
            } catch (Exception e) {
                throw new IllegalStateException("Failed to start the Konakart engine", e);
            }
        }
    }
    
    /**
     * Initialise a KonaKart engine instance and perform a login to get a session id.

     * @param storeId the storeId.
     *
     * @return the konakart engine
     *
     * @throws Exception e
     */
    static public KKAppEng get(String storeId) throws Exception {

        // Initialize Konakart engine
        StoreInfo storeInfo = new StoreInfo();
        storeInfo.setStoreId(storeId);

        return new KKAppEng(storeInfo);
    }
}
