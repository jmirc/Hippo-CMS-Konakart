package org.onehippo.forge.konakart.common.engine;

import com.konakartadmin.app.AdminEngineConfig;
import com.konakartadmin.app.KKAdminException;
import com.konakartadmin.appif.KKAdminIf;
import com.konakartadmin.ws.KKAdminEngineMgr;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.Session;

public class KKAdminEngine {

    public static final Logger log = LoggerFactory.getLogger(KKAdminEngine.class);

    private static final String KONAKART_PROPERTIES = "konakart.properties";

    /**
     * engClassName - Name of the engine to use
     */
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
    @Nullable
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
     *
     * @param session the Jcr Session
     * @throws Exception .
     */
    public KKAdminIf init(@Nonnull Session session) throws Exception {
        // Retrieve the global admin engine.
        KKAdminEngineConfig adminEngineConfig = HippoModuleConfig.getConfig().getAdminEngineConfig(session);

        // Initialize the admin engine
        init(adminEngineConfig);

        return kkAdminEng;
    }


    /**
    * Configure the Engine Config
    *
    * @param adminEngineConfig the Konakart Admin Engine @see /konakart:konakart/konakart:clientengine within console
    * @throws Exception .
    */
    private void init(KKAdminEngineConfig adminEngineConfig) throws Exception {

        if (kkAdminEng == null) {
            KKAdminEngineMgr kkAdminEngMgr = new KKAdminEngineMgr();
            AdminEngineConfig adEngConf = new AdminEngineConfig();
            adEngConf.setMode(adminEngineConfig.getEngineMode()); //
            adEngConf.setCustomersShared(adminEngineConfig.isCustomersShared());
            adEngConf.setProductsShared(adminEngineConfig.isProductsShared());
            adEngConf.setPropertiesFileName(KONAKART_PROPERTIES);

            /*
            * This creates a KonaKart Admin Engine by name using the constructor that requires an
            * AdminEngineConfig object. This is the recommended approach.
            */
            KKAdminIf tempKkAdminEng = kkAdminEngMgr.getKKAdminByName(ENG_CLASS_NAME, adEngConf);

            try {
                // login
                session = tempKkAdminEng.login(adminEngineConfig.getUsername(), adminEngineConfig.getPassword());

                kkAdminEng = tempKkAdminEng;

            } catch (KKAdminException e) {
                log.warn("Failed to log-in to the Konakart Admin.", e);
            }
        }
    }
}
