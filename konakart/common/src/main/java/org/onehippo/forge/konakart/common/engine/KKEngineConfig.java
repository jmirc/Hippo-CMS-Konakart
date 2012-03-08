package org.onehippo.forge.konakart.common.engine;

public class KKEngineConfig {

    private int engineMode;
    private boolean isCustomersShared;
    private boolean isProductsShared;
    private boolean updateKonakartProductsToRepository;
    private boolean updateRepositoryToKonakartProducts;

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

    public boolean isUpdateKonakartToRepository() {
        return updateKonakartProductsToRepository;
    }

    public void setUpdateKonakartProductsToRepository(boolean updateKonakartProductsToRepository) {
        this.updateKonakartProductsToRepository = updateKonakartProductsToRepository;
    }

    public boolean isUpdateRepositoryToKonakart() {
        return updateRepositoryToKonakartProducts;
    }

    public void setUpdateRepositoryToKonakartProducts(boolean updateRepositoryToKonakartProducts) {
        this.updateRepositoryToKonakartProducts = updateRepositoryToKonakartProducts;
    }
}
