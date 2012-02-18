package org.onehippo.forge.konakart.common.engine;

public class KKEngineConfig {

    private String engineClassName;
    private String username;
    private String password;
    private Long engineMode;
    private boolean isCustomersShared;
    private boolean isProductsShared;

    public String getEngineClassName() {
        return engineClassName;
    }

    public void setEngineClassName(String engineClassName) {
        this.engineClassName = engineClassName;
    }

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
        return engineMode.intValue();
    }

    public void setEngineMode(Long engineMode) {
        this.engineMode = engineMode;
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
}
