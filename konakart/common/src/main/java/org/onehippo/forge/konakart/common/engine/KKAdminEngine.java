package org.onehippo.forge.konakart.common.engine;

import com.konakartadmin.app.AdminEngineConfig;
import com.konakartadmin.app.KKAdminException;
import com.konakartadmin.appif.KKAdminIf;
import com.konakartadmin.bl.AdminMgrFactory;
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

    private KKAdminEngineConfig adminEngineConfig;

    private KKAdminIf kkAdminEng;

    private boolean isEnterprise;

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
     * @return true if we are in Enterprise Mode
     */
    public boolean isEnterprise() {
        return isEnterprise;
    }

    /**
     * @return true if we are in multistore Mode
     */
    public boolean isMultiStore() {
        return adminEngineConfig.getEngineMode() > 0;
    }

    /**
     * @return an helper class used to access to the administration functions
     */
    @Nonnull
    public AdminMgrFactory getFactory() {
        return new AdminMgrFactory(getEngine());
    }

    /**
     * @return the Konakart Admin client
     */
    @Nullable
    private KKAdminIf getEngine() {

        try {
            // the connection has expired.
            if (session == null || kkAdminEng.checkSession(session) == -1) {
                if (adminEngineConfig != null) {
                    // Login
                    login();
                } else {
                    log.error("Failed to log-in using the admin client. Admin engine config is null.");
                    throw new IllegalStateException("Failed to log-in using the admin client. Admin engine config is null.");
                }
            }

            return kkAdminEng;
        } catch (Exception e) {
            log.error("Failed to check the state of the Konakart admin connection", e);
            throw new IllegalStateException("Failed to check the state of the Konakart admin connection.", e);
        }
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
    public void init(@Nonnull Session session) throws Exception {
        // Retrieve the global admin engine.
        adminEngineConfig = HippoModuleConfig.getConfig().getAdminEngineConfig(session);

        // Initialize the admin engine
        init(adminEngineConfig);
    }


    /**
    * Configure the Engine Config
    *
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
            kkAdminEng = kkAdminEngMgr.getKKAdminByName(ENG_CLASS_NAME, adEngConf);

            isEnterprise = adEngConf.getEngineId().equals("E");
        }
    }

    /**
     * Create a connection using the admin client
     * @throws KKAdminException if the login failed.
     */
    protected void login() throws KKAdminException {
        // login
        session = kkAdminEng.login(adminEngineConfig.getUsername(), adminEngineConfig.getPassword());
    }
}
