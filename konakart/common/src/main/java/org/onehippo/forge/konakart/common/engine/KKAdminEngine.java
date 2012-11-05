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

    public static final ThreadLocal<KKAdminEngine> adminEngineThreadLocal = new ThreadLocal<KKAdminEngine>();

    private KKAdminEngineConfig adminEngineConfig;

    private KKAdminIf kkAdminEng;

    private boolean isEnterprise;

    private String session;

    protected KKAdminEngine() {
    }





    /**
     * @return true if we are in Enterprise Mode
     */
    public boolean isEnterprise() {
        try {
            return kkAdminEng.getEngConf().getEngineId().equals("E");
        } catch (KKAdminException e) {
            return false;
        }
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
     * @return true if the KKAdminEngine has been initialized, false otherwise.
     */
    public static boolean isInitialized() {
        KKAdminEngine kkAdminEngine = adminEngineThreadLocal.get();

        return kkAdminEngine != null;
    }

    /**
     * @return an helper class used to access to the administration functions
     */
    @Nonnull
    public static KKAdminEngine getInstance() {
        KKAdminEngine kkAdminEngine = adminEngineThreadLocal.get();

        if (kkAdminEngine == null) {
            throw new IllegalArgumentException("KKAdminEngine should not be null. This engine has not been initialized.");
        }

        return adminEngineThreadLocal.get();
    }



    /**
     * @return the Konakart Admin client
     */
    @Nullable
    public KKAdminIf getEngine() {
        boolean reconnect;

        try {
            int sessionId = kkAdminEng.checkSession(session);

            reconnect = sessionId == -1;
        } catch (KKAdminException e) {
            reconnect = true;
        }


        if (reconnect) {
            if (adminEngineConfig != null) {
                // Login
                try {
                    login();
                } catch (KKAdminException e) {
                    log.error("Failed to check the state of the Konakart admin connection", e);
                    throw new IllegalStateException("Failed to check the state of the Konakart admin connection.", e);
                }
            } else {
                log.error("Failed to log-in using the admin client.");
                throw new IllegalStateException("Failed to log-in using the admin client.");
            }
        }

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
    public static void init(@Nonnull Session session) throws Exception {

        KKAdminEngine kkAdminEngine = adminEngineThreadLocal.get();

        if (kkAdminEngine == null) {
            kkAdminEngine = new KKAdminEngine();
            kkAdminEngine.internalInit(session);

            adminEngineThreadLocal.set(kkAdminEngine);
        }
    }

    /**
     * Initialize internal init.
     * @param session the JCR Session
     * @throws Exception .
     */
    private void internalInit(@Nonnull Session session) throws Exception {
        // Retrieve the global admin engine.
        adminEngineConfig = HippoModuleConfig.getConfig().getAdminEngineConfig(session);

        // Initialize the admin engine
        init();

        adminEngineThreadLocal.set(this);
    }


    /**
    * Configure the Engine Config
     * @throws Exception .
    */
    private void init() throws Exception {

        KKAdminEngineMgr kkAdminEngMgr = new KKAdminEngineMgr();
        AdminEngineConfig adEngConf = new AdminEngineConfig();
        adEngConf.setMode(adminEngineConfig.getEngineMode()); //
        adEngConf.setStoreId(adminEngineConfig.getStoreId()); //
        adEngConf.setCustomersShared(adminEngineConfig.isCustomersShared());
        adEngConf.setProductsShared(adminEngineConfig.isProductsShared());
        adEngConf.setCategoriesShared(adminEngineConfig.isCategoriesShared());
        adEngConf.setPropertiesFileName(KONAKART_PROPERTIES);

        /*
        * This creates a KonaKart Admin Engine by name using the constructor that requires an
        * AdminEngineConfig object. This is the recommended approach.
        */
        kkAdminEng = kkAdminEngMgr.getKKAdminByName(ENG_CLASS_NAME, adEngConf);
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
