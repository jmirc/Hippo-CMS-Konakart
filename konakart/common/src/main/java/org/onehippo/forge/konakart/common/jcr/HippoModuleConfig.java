package org.onehippo.forge.konakart.common.jcr;

import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.common.engine.KKEngineConfig;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.HashMap;
import java.util.Map;

public class HippoModuleConfig {

    public static final Logger log = LoggerFactory.getLogger(HippoModuleConfig.class);

    public static final String STORES_PROPERTY = "stores";
    public static final String DEFAULT_PRODUCT_FOLDER_PROPERTY = "default.product.folder";
    public static final String DEFAULT_REVIEW_FOLDER_PROPERTY = "default.review.folder";
    public static final String DEFAULT_PRODUCT_FACTORY_CLASS_NAME_PROPERTY = "default.product.factory.class";
    public static final String PRODUCT_FOLDER_PROPERTY = "product.folder";
    public static final String REVIEW_FOLDER_PROPERTY = "review.folder";
    public static final String ENGINEMODE_PROPERTY = "enginemode";
    public static final String IS_CUSTOMERS_SHARED_PROPERTY = "isCustomersShared";
    public static final String IS_PRODUCTS_SHARED_PROPERTY = "isProductsShared";
    public static final String SYNCHRONIZATION_ENABLED_PROPERTY = "synchronization.enabled";
    public static final String SYNCHRONIZATION_CRON_EXPRESSION_PROPERTY = "synchronization.cronexpression";
    public static final String CONTENT_ROOT_PROPERTY = "contentRoot";
    public static final String GALLERY_ROOT_PROPERTY = "galleryRoot";
    public static final String JOB_CLASS_PROPERTY = "job.class";
    public static final String LOCALE_PROPERTY = "locale";
    public static final String STORE_ID_PROPERTY = "store.id";
    public static final String CATALOG_ID_PROPERTY = "catalog.id";

    public static final String ENGINE_CONFIG_NODE_PATH = "/hippo:configuration/hippo:frontend/cms/cms-services/KonakartClientEngineService";
    public static final String SYNC_CONFIG_NODE_PATH = "/hippo:configuration/hippo:frontend/cms/cms-services/KonakartSynchronizationService";
    public static final String PRODUCT_TYPE_NAMESPACES_PATH = SYNC_CONFIG_NODE_PATH + "/producttypenamespaces";
    private static HippoModuleConfig config = new HippoModuleConfig();
    public static final String LAST_UPDATED_TIME_KONAKART_TO_REPOSITORY = "lastUpdatedTimeKonakartToRepository";
    public static final String LAST_UPDATED_TIME_REPOSITORY_TO_KONNAKART = "lastUpdatedTimeRepositoryToKonnakart";

    private KKEngineConfig engineConfig = new KKEngineConfig();

    /**
     * Mapping between a contentRoot and a storeConfig
     */
    private Map<String, KKStoreConfig> storesConfig = new HashMap<String, KKStoreConfig>();

    /**
     * @return the config class
     */
    public static HippoModuleConfig getConfig() {
        return config;
    }

    public Map<String, KKStoreConfig> getStoresConfig() {
        return storesConfig;
    }

    /**
     * @return the engine config.
     */
    public KKEngineConfig getEngineConfig() {
        return engineConfig;
    }

    /**
     * @param session a JCR session
     * @return an instance of the config
     */
    public static HippoModuleConfig load(Session session) {

        if (session == null) {
            log.error("Failed to load the Konakart config. JCR Session is null");
            throw new RuntimeException("Failed to load the Konakart config. JCR Session is null");
        }

        // load configuration
        config.loadEngineConfiguration(session);
        config.loadProductTypeNamespaces(session);
        config.loadStoresConfiguration(session);

        return config;
    }

    /**
     * @param session the Jcr session
     */
    private void loadEngineConfiguration(Session session) {
        try {
            Node node = session.getNode(ENGINE_CONFIG_NODE_PATH);

            if (node.hasProperty(ENGINEMODE_PROPERTY)) {
                engineConfig.setEngineMode(node.getProperty(ENGINEMODE_PROPERTY).getLong());
            }

            if (node.hasProperty(IS_CUSTOMERS_SHARED_PROPERTY)) {
                engineConfig.setCustomersShared(node.getProperty(IS_CUSTOMERS_SHARED_PROPERTY).getBoolean());
            }

            if (node.hasProperty(IS_PRODUCTS_SHARED_PROPERTY)) {
                engineConfig.setProductsShared(node.getProperty(IS_PRODUCTS_SHARED_PROPERTY).getBoolean());
            }
        } catch (RepositoryException e) {
            log.error("Failed to load Product Type Namespaces mapping: " + e.toString());
        }

    }

    /**
     * Initialize the mapping between a Konakart product id and the Hippo document namespace
     * @param session the Jcr session
     */
    private void loadProductTypeNamespaces(Session session) {
        try {
            Node node = session.getNode(PRODUCT_TYPE_NAMESPACES_PATH);

            KKCndConstants.PRODUCT_TYPE[] product_types = KKCndConstants.PRODUCT_TYPE.values();

            for (KKCndConstants.PRODUCT_TYPE product_type : product_types) {
                if (node.hasProperty(product_type.getNamespace())) {
                    engineConfig.addProductNodeTypeMapping(product_type.getNamespace(),
                            node.getProperty(product_type.getNamespace()).getString());
                }
            }
        } catch (RepositoryException e) {
            log.error("Failed to load Product Type Namespaces mapping: " + e.toString());
        }

    }

    private void loadStoresConfiguration(Session session) {

        try {
            Node rootNode = session.getNode(SYNC_CONFIG_NODE_PATH);


            if (rootNode.hasProperty(STORES_PROPERTY)) {

                Property storesProperty = rootNode.getProperty(STORES_PROPERTY);

                Value[] storesName = storesProperty.getValues();

                for (Value storeName : storesName) {

                    KKStoreConfig kkStoreConfig = new KKStoreConfig();

                    Node storeNode = rootNode.getNode(storeName.getString());

                    kkStoreConfig.setNodePath(storeNode.getPath());

                    if (storeNode.hasProperty(PRODUCT_FOLDER_PROPERTY)) {
                        kkStoreConfig.setProductFolder(storeNode.getProperty(PRODUCT_FOLDER_PROPERTY).getString());
                    } else {
                        kkStoreConfig.setProductFolder(rootNode.getProperty(DEFAULT_PRODUCT_FOLDER_PROPERTY).getString());
                    }

                    if (storeNode.hasProperty(REVIEW_FOLDER_PROPERTY)) {
                        kkStoreConfig.setReviewFolder(storeNode.getProperty(REVIEW_FOLDER_PROPERTY).getString());
                    } else {
                        kkStoreConfig.setReviewFolder(rootNode.getProperty(DEFAULT_REVIEW_FOLDER_PROPERTY).getString());
                    }

                    if (storeNode.hasProperty(SYNCHRONIZATION_ENABLED_PROPERTY)) {
                        kkStoreConfig.setEnabled(storeNode.getProperty(SYNCHRONIZATION_ENABLED_PROPERTY).getBoolean());
                    }

                    if (storeNode.hasProperty(SYNCHRONIZATION_CRON_EXPRESSION_PROPERTY)) {
                        kkStoreConfig.setCronExpression(storeNode.getProperty(SYNCHRONIZATION_CRON_EXPRESSION_PROPERTY).getString());
                    }

                    if (storeNode.hasProperty(STORE_ID_PROPERTY)) {
                        kkStoreConfig.setStoreId(storeNode.getProperty(STORE_ID_PROPERTY).getString());
                    }

                    if (storeNode.hasProperty(CATALOG_ID_PROPERTY)) {
                        kkStoreConfig.setCatalogId(storeNode.getProperty(CATALOG_ID_PROPERTY).getString());
                    }

                    if (storeNode.hasProperty(LAST_UPDATED_TIME_REPOSITORY_TO_KONNAKART)) {
                        kkStoreConfig.setLastUpdatedTimeRepositoryToKonakart(storeNode.getProperty(LAST_UPDATED_TIME_REPOSITORY_TO_KONNAKART).getDate().getTime());
                    } else {
                        kkStoreConfig.setLastUpdatedTimeRepositoryToKonakart(null);
                    }

                    if (storeNode.hasProperty(LAST_UPDATED_TIME_KONAKART_TO_REPOSITORY)) {
                        kkStoreConfig.setLastUpdatedTimeKonakartToRepository(storeNode.getProperty(LAST_UPDATED_TIME_KONAKART_TO_REPOSITORY).getDate().getTime());
                    } else {
                        kkStoreConfig.setLastUpdatedTimeKonakartToRepository(null);
                    }

                    if (storeNode.hasProperty(JOB_CLASS_PROPERTY)) {
                        kkStoreConfig.setJobClass(storeNode.getProperty(JOB_CLASS_PROPERTY).getString());
                    }

                    if (storeNode.hasProperty(LOCALE_PROPERTY)) {
                        kkStoreConfig.setLocale(storeNode.getProperty(LOCALE_PROPERTY).getString());
                    }

                    if (storeNode.hasProperty(GALLERY_ROOT_PROPERTY)) {
                        kkStoreConfig.setGalleryRoot(storeNode.getProperty(GALLERY_ROOT_PROPERTY).getString());
                    }

                    if (storeNode.hasProperty(DEFAULT_PRODUCT_FACTORY_CLASS_NAME_PROPERTY)) {
                        kkStoreConfig.setProductFactoryClassName(storeNode.getProperty(DEFAULT_PRODUCT_FACTORY_CLASS_NAME_PROPERTY).getString());
                    }

                    if (storeNode.hasProperty(CONTENT_ROOT_PROPERTY)) {
                        String contentRoot = storeNode.getProperty(CONTENT_ROOT_PROPERTY).getString();
                        kkStoreConfig.setContentRoot(contentRoot);
                        storesConfig.put(contentRoot, kkStoreConfig);
                    }

                    kkStoreConfig.setInitialized(true);
                }
            }



        } catch (RepositoryException e) {
            log.error("Failed to load Hippo Module configuration: " + e.toString());
        }
    }
}