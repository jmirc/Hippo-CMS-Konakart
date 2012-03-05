package org.onehippo.forge.konakart.common.engine;

import com.konakart.al.KKAppEng;
import com.konakart.al.StoreInfo;
import com.konakart.app.EngineConfig;

public class KKEngine {

    private static final String KONAKART_PROPERTIES = "konakart.properties";
    private static final String KONAKART_APP_PROPERTIES = "konakart_app.properties";

    /**
     * Initialise a KonaKart engine instance and perform a login to get a session id.

     * @param config the hippo module config
     * @return the konakart engine
     *
     * @throws Exception e
     */
    static public KKAppEng get(KKEngineConfig config) throws Exception {

        // Initialize the Engine conf if not exits
        if (KKAppEng.getEngConf() == null) {
            // Initialize the engine conf
            EngineConfig engConf = new EngineConfig();
            engConf.setMode(config.getEngineMode());
            engConf.setStoreId(config.getStoreId());
            engConf.setCustomersShared(config.isCustomersShared());
            engConf.setProductsShared(config.isProductsShared());
            engConf.setPropertiesFileName(KONAKART_PROPERTIES);
            engConf.setAppPropertiesFileName(KONAKART_APP_PROPERTIES);

            new KKAppEng(engConf);
        }

        // Initialize Konakart engine
        StoreInfo storeInfo = new StoreInfo();
        storeInfo.setStoreId(config.getStoreId());

        return new KKAppEng(storeInfo);
    }
}
