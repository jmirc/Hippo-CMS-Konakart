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

import com.konakartadmin.app.AdminEngineConfig;
import com.konakartadmin.appif.KKAdminIf;
import com.konakartadmin.ws.KKAdminEngineMgr;

public class KKAdminEngine {

    private static final String KONAKART_PROPERTIES = "konakart.properties";

    /** engClassName - Name of the engine to use */
    private static final String ENG_CLASS_NAME = "com.konakartadmin.bl.KKAdmin";

    private static KKAdminEngine instance = new KKAdminEngine();

    private KKAdminIf kkAdminEng;

    private String session;

    private KKAdminEngine() {
    }

    /**
     * @return the KKAdminEngine instance
     */
    public static KKAdminEngine getInstance() {
        return instance;
    }

    /**
     * @return the Konakart Admin client
     */
    public KKAdminIf getEngine() {
        return kkAdminEng;
    }

    /**
     * @return the session
     */
    public String getSession() {
        return session;
    }

    /**
     * Configure the Engine Config
     * @param engineMode the engine mode
     * @throws Exception .
     */
    public void init(int engineMode, String username, String password) throws Exception {
        if (kkAdminEng == null) {
            KKAdminEngineMgr kkAdminEngMgr = new KKAdminEngineMgr();
            AdminEngineConfig adEngConf = new AdminEngineConfig();
            adEngConf.setMode(engineMode); //
            adEngConf.setPropertiesFileName(KONAKART_PROPERTIES);

            /*
            * This creates a KonaKart Admin Engine by name using the constructor that requires an
            * AdminEngineConfig object. This is the recommended approach.
            */
            kkAdminEng = kkAdminEngMgr.getKKAdminByName(ENG_CLASS_NAME, adEngConf);

            // login
            session = kkAdminEng.login(username, password);
        }
    }
}
