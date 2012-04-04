package org.onehippo.forge.konakart.common.engine;

import com.konakart.al.KKAppEng;
import com.konakart.al.StoreInfo;
import com.konakart.app.EngineConfig;

public final class KKEngine {

    private static final String KONAKART_PROPERTIES = "konakart.properties";
    private static final String KONAKART_APP_PROPERTIES = "konakart_app.properties";

    private KKEngine() {
    }

    /**
     * Configure the Engine Config
     * @param mode the engine mode
     * @param storeId the storeId
     * @param isCustomersShared set to true if the customers are shared
     * @param isProductsShared set to true if the products are shared
     * @throws Exception .
     */
    static public void init(int mode, boolean isCustomersShared, boolean isProductsShared)
            throws Exception {

        // Initialize the Engine conf if not exits
        if (KKAppEng.getEngConf() == null) {
            // Initialize the engine conf
            EngineConfig engConf = new EngineConfig();
            engConf.setMode(mode);
            engConf.setCustomersShared(isCustomersShared);
            engConf.setProductsShared(isProductsShared);
            engConf.setPropertiesFileName(KONAKART_PROPERTIES);
            engConf.setAppPropertiesFileName(KONAKART_APP_PROPERTIES);

            new KKAppEng(engConf);
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
