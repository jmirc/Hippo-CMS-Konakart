package org.onehippo.forge.konakart.common.jcr;

import com.google.common.collect.Maps;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.common.engine.*;
import org.onehippo.forge.utilities.commons.NodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import java.util.Map;

public class HippoModuleConfig {

    public static final Logger log = LoggerFactory.getLogger(HippoModuleConfig.class);

    public static final String SYNC_CONFIG_NODE_PATH = "/hippo:configuration/hippo:frontend/cms/cms-services/KonakartSynchronizationService";

    public static final String DEFAULT_PRODUCT_FOLDER_PROPERTY = "default.product.folder";
    public static final String DEFAULT_PRODUCT_FACTORY_CLASS_NAME_PROPERTY = "default.product.factory.class";
    public static final String DEFAULT_SYNC_CRON_EXPRESSION_PROPERTY = "default.sync.cronexpression";
    public static final String DEFAULT_SYNC_JOB_CLASS_PROPERTY = "default.sync.job.class";
    public static final String STORES_PROPERTY = "stores";
    public static final String DEVELOPMENT_MODE = "developper.mode";

    public static final String KONAKART_KONAKART_PATH = "/konakart:konakart";
    public static final String KONAKART_STORES_PATH = KONAKART_KONAKART_PATH + "/konakart:stores";
    public static final String KONAKART_PRODUCT_TYPE_NAMESPACES_PATH = KONAKART_KONAKART_PATH + "/konakart:producttypenamespaces";

    public static final String STORE_CONTENT_ROOT_PROPERTY = "konakart:contentroot";
    public static final String STORE_GALLERY_ROOT_PROPERTY = "konakart:galleryroot";
    public static final String STORE_STORE_ID_PROPERTY = "konakart:storeid";
    public static final String STORE_CATALOG_ID_PROPERTY = "konakart:catalogid";

    public static final String SYNC_NODE_PATH = "konakart:sync";

    public static final String SYNC_PRODUCT_FOLDER_PROPERTY = "konakart:productfolder";
    public static final String SYNC_JOB_CLASS = "konakart:jobclass";
    public static final String SYNC_CRON_EXPRESSION = "konakart:synchronizationcronexpression";
    public static final String SYNC_LAST_UPDATED_TIME_KONAKART_TO_REPOSITORY = "konakart:lastupdatedtimekonakarttorepository";
    public static final String SYNC_LAST_UPDATED_TIME_REPOSITORY_TO_KONNAKART = "konakart:lastupdatedtimerepositorytokonnakart";

    public static final String KONAKART_CHECKOUT_PATH = KONAKART_KONAKART_PATH + "/konakart:checkout";

    public static final String KONAKART_PROCESSOR = "konakart:processor";

    public static final String KONAKART_ACTIVITIES = "konakart:activities";

    public static final String KONAKART_ACTIVITY_CLASS = "konakart:class";
    public static final String KONAKART_ACTIVITY_ACCEPT_EMPTY_STATE = "konakart:acceptEmptyState";
    public static final String KONAKART_ACTIVITY_ACCEPT_STATE = "konakart:acceptState";
    public static final String KONAKART_ACTIVITY_NEXT_NON_LOGGED_STATE = "konakart:nextNonLoggedState";
    public static final String KONAKART_ACTIVITY_NEXT_LOGGED_STATE = "konakart:nextLoggedState";
    public static final String KONAKART_ACTIVITY_TEMPLATE_RENDER_PATH = "konakart:templateRenderpath";

    private static HippoModuleConfig config = new HippoModuleConfig();

    private KKAdminEngineConfig adminEngineConfig = new KKAdminEngineConfig();
    private KKClientEngineConfig clientEngineConfig = new KKClientEngineConfig();

    /**
     * Mapping between a contentRoot and a storeConfig
     */
    private Map<String, KKStoreConfig> storesConfig = Maps.newHashMap();

    /**
     * Contains the checkout process.
     */
    private KKCheckoutConfig checkoutConfig = new KKCheckoutConfig();

    /**
     * @return the config class
     */
    public static HippoModuleConfig getConfig() {
        return config;
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
     * @return the admin engine config.
     */
    public KKAdminEngineConfig getAdminEngineConfig(Session session) {
        if (!adminEngineConfig.isInitialized()) {
            adminEngineConfig.loadAdminEngineConfiguration(session);
        }

        return adminEngineConfig;
    }

    /**
     * @return the client engine config.
     */
    public KKClientEngineConfig getClientEngineConfig(Session session) {
        if (!clientEngineConfig.isInitialized()) {
            clientEngineConfig.loadClientEngineConfiguration(session);
        }

        return clientEngineConfig;
    }

    /**
     * @return the engine config.
     */
    public KKClientEngineConfig getClientEngineConfig() {
        return clientEngineConfig;
    }

    /**
     * @return the checkout process config
     */
    public KKCheckoutConfig getCheckoutConfig() {
        return checkoutConfig;
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
     * Initialize the mapping between a Konakart product id and the Hippo document namespace
     *
     * @param session the Jcr session
     */
    private void loadProductTypeNamespaces(Session session) {
        try {
            Node node = session.getNode(KONAKART_PRODUCT_TYPE_NAMESPACES_PATH);

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

                    kkStoreConfig.setJobClass(NodeUtils.getString(syncConfigNode, DEFAULT_SYNC_JOB_CLASS_PROPERTY));
                    kkStoreConfig.setCronExpression(NodeUtils.getString(syncConfigNode, DEFAULT_SYNC_CRON_EXPRESSION_PROPERTY));
                    kkStoreConfig.setProductFolder(NodeUtils.getString(syncConfigNode, DEFAULT_PRODUCT_FOLDER_PROPERTY));
                    kkStoreConfig.setProductFactoryClassName(NodeUtils.getString(syncConfigNode, DEFAULT_PRODUCT_FACTORY_CLASS_NAME_PROPERTY));
                    kkStoreConfig.setDevelopmentMode(NodeUtils.getBoolean(syncConfigNode, DEVELOPMENT_MODE, false));

                    loadStoreConfigByName(session, storeName.getString(), kkStoreConfig);
                }
            }
        } catch (RepositoryException e) {
            log.error("Failed to load Hippo Module configuration: " + e.toString());
        }
    }

    private void loadStoreConfigByName(Session session, String storeName, KKStoreConfig kkStoreConfig) throws RepositoryException {
        Node rootNode = session.getNode(KONAKART_STORES_PATH);


        // Set the default configurations.


        if (rootNode.hasNode(storeName)) {
            Node storeNode = rootNode.getNode(storeName);

            kkStoreConfig.setNodePath(storeNode.getPath());

            kkStoreConfig.setContentRoot(NodeUtils.getString(storeNode, STORE_CONTENT_ROOT_PROPERTY));
            kkStoreConfig.setGalleryRoot(NodeUtils.getString(storeNode, STORE_GALLERY_ROOT_PROPERTY));
            kkStoreConfig.setStoreId(NodeUtils.getString(storeNode, STORE_STORE_ID_PROPERTY));
            kkStoreConfig.setCatalogId(NodeUtils.getString(storeNode, STORE_CATALOG_ID_PROPERTY));

            if (storeNode.hasNode(SYNC_NODE_PATH)) {
                Node syncNode = storeNode.getNode(SYNC_NODE_PATH);

                kkStoreConfig.setJobClass(NodeUtils.getString(syncNode, SYNC_JOB_CLASS, kkStoreConfig.getJobClass()));
                kkStoreConfig.setCronExpression(NodeUtils.getString(syncNode, SYNC_CRON_EXPRESSION, kkStoreConfig.getCronExpression()));
                kkStoreConfig.setProductFolder(NodeUtils.getString(syncNode, SYNC_PRODUCT_FOLDER_PROPERTY, kkStoreConfig.getProductFolder()));
                kkStoreConfig.setLastUpdatedTimeKonakartToRepository(NodeUtils.getDate(syncNode, SYNC_LAST_UPDATED_TIME_KONAKART_TO_REPOSITORY));
                kkStoreConfig.setLastUpdatedTimeRepositoryToKonakart(NodeUtils.getDate(syncNode, SYNC_LAST_UPDATED_TIME_REPOSITORY_TO_KONNAKART));
            }


            kkStoreConfig.setInitialized(true);
            storesConfig.put(kkStoreConfig.getStoreId(), kkStoreConfig);
        } else {
            log.warn("Failed to find a valid node for the store named " + storeName + ". " +
                    "Please create the node " + KONAKART_STORES_PATH + "/" + storeName);
        }
    }

    /**
     * Load the list of activity
     *
     * @param session the JCR session
     */
    public void preLoadActivityList(Session session) throws RepositoryException {
        Node rootNode = session.getNode(KONAKART_CHECKOUT_PATH);

        checkoutConfig.getActivityConfigList().clear();

        if (rootNode.hasProperty(KONAKART_PROCESSOR)) {
            checkoutConfig.setProcessorClass(rootNode.getProperty(KONAKART_PROCESSOR).getString());
        }

        if (rootNode.hasProperty(KONAKART_ACTIVITIES)) {

            Value[] activities = rootNode.getProperty(KONAKART_ACTIVITIES).getValues();

            for (Value activity : activities) {
                String activityName = activity.getString();

                if (rootNode.hasNode(activityName)) {
                    Node activityNode = rootNode.getNode(activityName);

                    KKActivityConfig activityConfig = new KKActivityConfig();

                    activityConfig.setName(activityName);
                    activityConfig.setAcceptEmptyState(NodeUtils.getBoolean(activityNode, KONAKART_ACTIVITY_ACCEPT_EMPTY_STATE));
                    activityConfig.setAcceptState(NodeUtils.getString(activityNode, KONAKART_ACTIVITY_ACCEPT_STATE));
                    activityConfig.setActivityClass(NodeUtils.getString(activityNode, KONAKART_ACTIVITY_CLASS));
                    activityConfig.setNextLoggedState(NodeUtils.getString(activityNode, KONAKART_ACTIVITY_NEXT_LOGGED_STATE));
                    activityConfig.setNextNonLoggedState(NodeUtils.getString(activityNode, KONAKART_ACTIVITY_NEXT_NON_LOGGED_STATE));
                    activityConfig.setTemplateRenderpath(NodeUtils.getString(activityNode, KONAKART_ACTIVITY_TEMPLATE_RENDER_PATH));

                    checkoutConfig.addActivityConfigList(activityConfig);

                } else {
                    log.error("The activity <" + activityName + "> has been added into the list of konakart:activities " +
                            "but no konakart:activity node has been found. Please check the node " + KONAKART_CHECKOUT_PATH);
                }
            }
        } else {
            log.warn("Failed to find at least one activity node. Please check the node " + KONAKART_CHECKOUT_PATH);
        }
    }

}