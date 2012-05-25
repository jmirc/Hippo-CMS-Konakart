package org.onehippo.forge.konakart.common.engine;

import java.util.HashMap;
import java.util.Map;

public class KKEngineConfig {

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
}
