package org.onehippo.forge.konakart.replication.config;

import com.konakart.util.KKConstants;

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
public class HippoKonakartMapping {

    private String storeId = KKConstants.KONAKART_DEFAULT_STORE_ID;
    private String hippoContentRoot;
    private String catalogId;


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
}
