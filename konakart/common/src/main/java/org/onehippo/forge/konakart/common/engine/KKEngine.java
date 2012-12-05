package org.onehippo.forge.konakart.common.engine;

import com.konakart.al.KKAppEng;
import com.konakart.al.StoreInfo;
import com.konakart.app.EngineConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;

import javax.jcr.Session;

public final class KKEngine {

  private static final String KONAKART_PROPERTIES = "konakart.properties";
  private static final String KONAKART_APP_PROPERTIES = "konakart_app.properties";

  public static final ThreadLocal<KKAppEng> kkAppEngThreadLocal = new ThreadLocal<KKAppEng>();


  private KKEngine() {
  }

  /**
   * Configure the Engine Config
   *
   * @param session the Jcr Session
   * @throws IllegalStateException thrown if the KonakartEngine is not able to start.
   */
  static public void init(Session session) {

    // Initialize the Engine conf if not exits
    KKClientEngineConfig clientEngineConfig = HippoModuleConfig.getConfig().getClientEngineConfig(session);

    // Initialize the engine conf
    EngineConfig engConf = new EngineConfig();
    engConf.setMode(clientEngineConfig.getEngineMode());
    engConf.setCustomersShared(clientEngineConfig.isCustomersShared());
    engConf.setProductsShared(clientEngineConfig.isProductsShared());
    engConf.setCategoriesShared(clientEngineConfig.isCategoriesShared());
    engConf.setPropertiesFileName(KONAKART_PROPERTIES);
    engConf.setAppPropertiesFileName(KONAKART_APP_PROPERTIES);

    try {
      new KKAppEng(engConf);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to start the Konakart engine", e);
    }
  }

  /**
   * Initialise a KonaKart engine instance and perform a login to get a session id.
   *
   * @param storeId the storeId.
   * @return the konakart engine
   * @throws Exception e
   */
  static public KKAppEng get(String storeId) throws Exception {

    // Initialize Konakart engine
    StoreInfo storeInfo = new StoreInfo();
    storeInfo.setStoreId(storeId);

    return new KKAppEng(storeInfo);
  }

  /**
   * Retrieve the KKAppEng from the thread local.
   *
   * @return the konakart engine
   * @throws Exception e
   */
  static public KKAppEng get() throws Exception {
    return kkAppEngThreadLocal.get();
  }

  /**
   * Set the KKAppEng to the thread local
   *
   * @param kkAppEng the engine to set
   */
  public static void set(KKAppEng kkAppEng) {
    kkAppEngThreadLocal.set(kkAppEng);
  }
}
