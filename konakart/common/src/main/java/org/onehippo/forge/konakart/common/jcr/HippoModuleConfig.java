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

    public static final String SYNC_CONFIG_NODE_PATH = "/hippo:configuration/hippo:frontend/cms/cms-services/KonakartSynchronizationService";
    public static final String PRODUCT_TYPE_NAMESPACES_PATH = SYNC_CONFIG_NODE_PATH + "/producttypenamespaces";

    public static final String DEFAULT_PRODUCT_FOLDER_PROPERTY = "default.product.folder";
    public static final String DEFAULT_REVIEW_FOLDER_PROPERTY = "default.review.folder";
    public static final String DEFAULT_PRODUCT_FACTORY_CLASS_NAME_PROPERTY = "default.product.factory.class";
    public static final String DEFAULT_SYNC_CRON_EXPRESSION_PROPERTY = "default.sync.cronexpression";
    public static final String DEFAULT_SYNC_JOB_CLASS_PROPERTY = "default.sync.job.class";
    public static final String STORES_PROPERTY = "stores";

    public static final String KONAKART_KONAKART_PATH = "/konakart:konakart";
    public static final String KONAKART_STORES_PATH = KONAKART_KONAKART_PATH + "/konakart:stores";

    public static final String STORE_CONTENT_ROOT_PROPERTY = "konakart:contentroot";
    public static final String STORE_GALLERY_ROOT_PROPERTY = "konakart:galleryroot";
    public static final String STORE_STORE_ID_PROPERTY = "konakart:storeid";
    public static final String STORE_CATALOG_ID_PROPERTY = "konakart:catalogid";

    public static final String SYNC_NODE_PATH = "konakart:sync";

    public static final String SYNC_PRODUCT_FOLDER_PROPERTY = "konakart:productfolder";
    public static final String SYNC_REVIEW_FOLDER_PROPERTY = "konakart:reviewfolder";
    public static final String SYNC_JOB_CLASS = "konakart:jobclass";
    public static final String SYNC_CRON_EXPRESSION = "konakart:synchronizationcronexpression";
    public static final String SYNC_LAST_UPDATED_TIME_KONAKART_TO_REPOSITORY = "konakart:lastupdatedtimekonakarttorepository";
    public static final String SYNC_LAST_UPDATED_TIME_REPOSITORY_TO_KONNAKART = "konakart:lastupdatedtimerepositorytokonnakart";

    public static final String CLIENT_ENGINE_CONFIG_NODE_PATH = KONAKART_KONAKART_PATH + "/konakart:clientengine";

    public static final String CLIENT_ENGINEMODE_PROPERTY = "konakart:enginemode";
    public static final String CLIENT_IS_CUSTOMERS_SHARED_PROPERTY = "konakart:isCustomersShared";
    public static final String CLIENT_IS_PRODUCTS_SHARED_PROPERTY = "konakart:isProductsShared";

    private static HippoModuleConfig config = new HippoModuleConfig();

    private KKEngineConfig clientEngineConfig = new KKEngineConfig();

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

    public Map<String, KKStoreConfig> getStoresConfig(Session session) {
        if (storesConfig.isEmpty()) {
            loadStoresConfiguration(session);
        }

        return storesConfig;
    }

    public Map<String, KKStoreConfig> getStoresConfig() {
        return storesConfig;
    }

    public KKStoreConfig getStoreConfigByName(Session session, String storeId) throws RepositoryException {
        KKStoreConfig kkStoreConfig = new KKStoreConfig();

        loadStoreConfigByName(session, storeId, kkStoreConfig);
        return kkStoreConfig;
    }

    /**
     * @return the engine config.
     */
    public KKEngineConfig getClientEngineConfig(Session session) {
        if (clientEngineConfig == null) {
            loadClientEngineConfiguration(session);
        }

        return clientEngineConfig;
    }

    /**
     * @return the engine config.
     */
    public KKEngineConfig getClientEngineConfig() {
        return clientEngineConfig;
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
        config.loadProductTypeNamespaces(session);
        config.loadStoresConfiguration(session);

        return config;
    }


    /**
     * @param session the Jcr session
     */
    private void loadClientEngineConfiguration(Session session) {
        try {
            Node node = session.getNode(CLIENT_ENGINE_CONFIG_NODE_PATH);

            if (node.hasProperty(CLIENT_ENGINEMODE_PROPERTY)) {
                clientEngineConfig.setEngineMode(node.getProperty(CLIENT_ENGINEMODE_PROPERTY).getLong());
            }

            if (node.hasProperty(CLIENT_IS_CUSTOMERS_SHARED_PROPERTY)) {
                clientEngineConfig.setCustomersShared(node.getProperty(CLIENT_IS_CUSTOMERS_SHARED_PROPERTY).getBoolean());
            }

            if (node.hasProperty(CLIENT_IS_PRODUCTS_SHARED_PROPERTY)) {
                clientEngineConfig.setProductsShared(node.getProperty(CLIENT_IS_PRODUCTS_SHARED_PROPERTY).getBoolean());
            }
        } catch (RepositoryException e) {
            throw new IllegalStateException("Failed to load client engine mapping. Check the " + CLIENT_ENGINE_CONFIG_NODE_PATH + " node.", e);
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
                    clientEngineConfig.addProductNodeTypeMapping(product_type.getNamespace(),
                            node.getProperty(product_type.getNamespace()).getString());
                }
            }
        } catch (RepositoryException e) {
            log.error("Failed to load Product Type Namespaces mapping: " + e.toString());
        }

    }

    private void loadStoresConfiguration(Session session) {

        try {
            Node syncConfigNode = session.getNode(SYNC_CONFIG_NODE_PATH);


            if (syncConfigNode.hasProperty(STORES_PROPERTY)) {

                Property storesProperty = syncConfigNode.getProperty(STORES_PROPERTY);

                Value[] storesName = storesProperty.getValues();

                for (Value storeName : storesName) {

                    KKStoreConfig kkStoreConfig = new KKStoreConfig();

                    // Set the default configurations.
                    if (syncConfigNode.hasProperty(DEFAULT_SYNC_JOB_CLASS_PROPERTY)) {
                        kkStoreConfig.setJobClass(syncConfigNode.getProperty(DEFAULT_SYNC_JOB_CLASS_PROPERTY).getString());
                    }

                    if (syncConfigNode.hasProperty(DEFAULT_SYNC_CRON_EXPRESSION_PROPERTY)) {
                        kkStoreConfig.setCronExpression(syncConfigNode.getProperty(DEFAULT_SYNC_CRON_EXPRESSION_PROPERTY).getString());
                    }

                    if (syncConfigNode.hasProperty(DEFAULT_PRODUCT_FOLDER_PROPERTY)) {
                        kkStoreConfig.setProductFolder(syncConfigNode.getProperty(DEFAULT_PRODUCT_FOLDER_PROPERTY).getString());
                    }

                    if (syncConfigNode.hasProperty(DEFAULT_REVIEW_FOLDER_PROPERTY)) {
                        kkStoreConfig.setReviewFolder(syncConfigNode.getProperty(DEFAULT_REVIEW_FOLDER_PROPERTY).getString());
                    }

                    if (syncConfigNode.hasProperty(DEFAULT_PRODUCT_FACTORY_CLASS_NAME_PROPERTY)) {
                        kkStoreConfig.setProductFactoryClassName(syncConfigNode.getProperty(DEFAULT_PRODUCT_FACTORY_CLASS_NAME_PROPERTY).getString());
                    }

                    loadStoreConfigByName(session, storeName.getString(), kkStoreConfig);
                }
            }
        } catch (RepositoryException e) {
            log.error("Failed to load Hippo Module configuration: " + e.toString());
        }
    }

    private void loadStoreConfigByName(Session session, String storeName, KKStoreConfig kkStoreConfig) throws RepositoryException {
        Node rootNode = session.getNode(KONAKART_STORES_PATH);

        if (rootNode.hasNode(storeName)) {
            Node storeNode = rootNode.getNode(storeName);

            kkStoreConfig.setNodePath(storeNode.getPath());

            if (storeNode.hasProperty(STORE_CONTENT_ROOT_PROPERTY)) {
                kkStoreConfig.setContentRoot(storeNode.getProperty(STORE_CONTENT_ROOT_PROPERTY).getString());
            }

            if (storeNode.hasProperty(STORE_GALLERY_ROOT_PROPERTY)) {
                kkStoreConfig.setGalleryRoot(storeNode.getProperty(STORE_GALLERY_ROOT_PROPERTY).getString());
            }

            if (storeNode.hasProperty(STORE_STORE_ID_PROPERTY)) {
                kkStoreConfig.setStoreId(storeNode.getProperty(STORE_STORE_ID_PROPERTY).getString());
            }

            if (storeNode.hasProperty(STORE_CATALOG_ID_PROPERTY)) {
                kkStoreConfig.setCatalogId(storeNode.getProperty(STORE_CATALOG_ID_PROPERTY).getString());
            }

            if (storeNode.hasNode(SYNC_NODE_PATH)) {
                Node syncNode = storeNode.getNode(SYNC_NODE_PATH);

                if (syncNode.hasProperty(SYNC_JOB_CLASS)) {
                    kkStoreConfig.setJobClass(syncNode.getProperty(SYNC_JOB_CLASS).getString());
                }

                if (syncNode.hasProperty(SYNC_CRON_EXPRESSION)) {
                    kkStoreConfig.setCronExpression(syncNode.getProperty(SYNC_CRON_EXPRESSION).getString());
                }

                if (syncNode.hasProperty(SYNC_PRODUCT_FOLDER_PROPERTY)) {
                    kkStoreConfig.setProductFolder(syncNode.getProperty(SYNC_PRODUCT_FOLDER_PROPERTY).getString());
                }

                if (syncNode.hasProperty(SYNC_REVIEW_FOLDER_PROPERTY)) {
                    kkStoreConfig.setReviewFolder(syncNode.getProperty(SYNC_REVIEW_FOLDER_PROPERTY).getString());
                }

                if (syncNode.hasProperty(SYNC_LAST_UPDATED_TIME_KONAKART_TO_REPOSITORY)) {
                    kkStoreConfig.setLastUpdatedTimeKonakartToRepository(syncNode.getProperty(SYNC_LAST_UPDATED_TIME_KONAKART_TO_REPOSITORY).getDate().getTime());
                } else {
                    kkStoreConfig.setLastUpdatedTimeKonakartToRepository(null);
                }

                if (syncNode.hasProperty(SYNC_LAST_UPDATED_TIME_REPOSITORY_TO_KONNAKART)) {
                    kkStoreConfig.setLastUpdatedTimeRepositoryToKonakart(syncNode.getProperty(SYNC_LAST_UPDATED_TIME_REPOSITORY_TO_KONNAKART).getDate().getTime());
                } else {
                    kkStoreConfig.setLastUpdatedTimeRepositoryToKonakart(null);
                }
            }


            kkStoreConfig.setInitialized(true);
            storesConfig.put(kkStoreConfig.getStoreId(), kkStoreConfig);
        } else {
            log.warn("Failed to find a valid node for the store named " + storeName + ". " +
                    "Please create the node " + KONAKART_STORES_PATH + "/" + storeName);
        }
    }
}