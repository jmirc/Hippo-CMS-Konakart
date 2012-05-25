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
