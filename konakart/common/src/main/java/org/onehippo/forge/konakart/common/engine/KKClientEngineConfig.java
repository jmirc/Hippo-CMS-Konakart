package org.onehippo.forge.konakart.common.engine;

import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.utilities.commons.NodeUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;

public class KKClientEngineConfig {

    public static final String CLIENT_ENGINE_CONFIG_NODE_PATH = HippoModuleConfig.KONAKART_KONAKART_PATH + "/konakart:clientengine";

    public static final String CLIENT_ENGINEMODE_PROPERTY = "konakart:enginemode";
    public static final String CLIENT_IS_CUSTOMERS_SHARED_PROPERTY = "konakart:iscustomersshared";
    public static final String CLIENT_IS_PRODUCTS_SHARED_PROPERTY = "konakart:isproductshared";

    private boolean initialized = false;
    private int engineMode;
    private boolean isCustomersShared;
    private boolean isProductsShared;
    private Map<String, String> productNodeTypeMapping = new HashMap<String, String>();


    public int getEngineMode() {
        return engineMode;
    }

    public void setEngineMode(long engineMode) {
        this.engineMode = (int) engineMode;
    }

    public boolean isCustomersShared() {
        return isCustomersShared;
    }

    public void setCustomersShared(boolean customersShared) {
        isCustomersShared = customersShared;
    }

    public boolean isProductsShared() {
        return isProductsShared;
    }

    public void setProductsShared(boolean productsShared) {
        isProductsShared = productsShared;
    }

    public Map<String, String> getProductNodeTypeMapping() {
        return productNodeTypeMapping;
    }

    public void addProductNodeTypeMapping(String namespace, String nodeType) {
        productNodeTypeMapping.put(namespace, nodeType);
    }

    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @param session the Jcr session
     */
    public void loadClientEngineConfiguration(Session session) {
        try {
            Node node = session.getNode(CLIENT_ENGINE_CONFIG_NODE_PATH);

            setEngineMode(NodeUtils.getLong(node, CLIENT_ENGINEMODE_PROPERTY, 0L));
            setCustomersShared(NodeUtils.getBoolean(node, CLIENT_IS_CUSTOMERS_SHARED_PROPERTY, false));
            setProductsShared(NodeUtils.getBoolean(node, CLIENT_IS_PRODUCTS_SHARED_PROPERTY, false));

            initialized = true;
        } catch (RepositoryException e) {
            throw new IllegalStateException("Failed to load client engine mapping. Check the " + CLIENT_ENGINE_CONFIG_NODE_PATH + " node.", e);
        }
    }

}
