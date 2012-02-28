package org.onehippo.forge.konakart.replication.config;

import com.konakart.util.KKConstants;

import java.util.List;

/**
 * Sometimes you would be able to synchronize different types of products in different location.
 *
 * For example the product could be saved under the node "products", and the events under the node "events".
 * The type of the product could be set to "Physical Product" and the type of the event could be set to Bookable product.
 *
 */
public class HippoKonakartMapping {

    private List<MappingByProductType> byProductTypeList;

    /**
     * Set the mapping definition by product's type
     * @param byProductTypeList the list of Mapping
     */
    public void setByProductTypeList(List<MappingByProductType> byProductTypeList) {
        this.byProductTypeList = byProductTypeList;
    }

    public List<MappingByProductType> getByProductTypeList() {
        return byProductTypeList;
    }

    /**
     * This class is used to map a Hippo content root with a store id and a catalog name
     *
     *    Hippo                      |                Konakart
     *                               |
     *    locale - en_US        is mapped with       store 1 - no catalog
     *    locale - en_CA        is mapped with       store 2 - en_ca catalog
     *    locale - fr_CA        is mapped with       store 2 - fr_ca catalog
     *                               |
     *
     *
     *    en_CA and fr_CA share the same products but could have different prices and quantities
     *
     */
    public static class MappingByProductType {
        private String storeId = KKConstants.KONAKART_DEFAULT_STORE_ID;
        private String hippoContentRoot;
        private String catalogId;
        private String reviewFolder;
        private int productType = -1;


        public String getStoreId() {
            return storeId;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public String getHippoContentRoot() {
            return hippoContentRoot;
        }

        public void setHippoContentRoot(String hippoContentRoot) {
            this.hippoContentRoot = hippoContentRoot;
        }

        public String getCatalogId() {
            return catalogId;
        }

        public void setCatalogId(String catalogId) {
            this.catalogId = catalogId;
        }

        public String getReviewFolder() {
            return reviewFolder;
        }

        public void setReviewFolder(String reviewFolder) {
            this.reviewFolder = reviewFolder;
        }

        public boolean isProductTypeSet() {
            return productType != -1;
        }

        public int getProductType() {
            return productType;
        }

        public void setProductType(int productType) {
            this.productType = productType;
        }
    }
}
