package org.onehippo.forge.konakart.common.engine;

import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.utilities.commons.NodeUtils;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class KKAdminEngineConfig {

    public static final String ADMIN_ENGINE_CONFIG_NODE_PATH = HippoModuleConfig.KONAKART_KONAKART_PATH + "/konakart:adminengine";

    public static final String ADMIN_USERNAME_PROPERTY = "konakart:adminusername";
    public static final String ADMIN_PASSWORD_PROPERTY = "konakart:adminpassword";
    public static final String ADMIN_ENGINEMODE_PROPERTY = "konakart:enginemode";
    public static final String ADMIN_IS_CUSTOMERS_SHARED_PROPERTY = "konakart:iscustomershared";
    public static final String ADMIN_IS_PRODUCTS_SHARED_PROPERTY = "konakart:isproductshared";


    private String username;
    private String password;
    private int engineMode;
    private boolean isCustomersShared;
    private boolean isProductsShared;
    private boolean initialized = false;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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

    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @param session the Jcr session
     */
    public void loadAdminEngineConfiguration(Session session) {
        try {
            Node node = session.getNode(ADMIN_ENGINE_CONFIG_NODE_PATH);

            setUsername(NodeUtils.getString(node, ADMIN_USERNAME_PROPERTY));
            setPassword(NodeUtils.getString(node, ADMIN_PASSWORD_PROPERTY));
            setEngineMode(NodeUtils.getLong(node, ADMIN_ENGINEMODE_PROPERTY, 0L));
            setCustomersShared(NodeUtils.getBoolean(node, ADMIN_IS_CUSTOMERS_SHARED_PROPERTY, false));
            setProductsShared(NodeUtils.getBoolean(node, ADMIN_IS_PRODUCTS_SHARED_PROPERTY, false));

            this.initialized = true;
        } catch (RepositoryException e) {
            throw new IllegalStateException("Failed to load client engine mapping. Check the " + ADMIN_ENGINE_CONFIG_NODE_PATH + " node.", e);
        }
    }

}
