package org.onehippo.forge.konakart.common;

public interface KKCndConstants {


    String NEW_PRODUCTS_FOLDER_TEMPLATE = "new-products-folder";
    String NEW_PRODUCT_DOCUMENT_TEMPLATE = "new-konakartproduct-document";

    String KONAKART_CONFIG_STORE_NAME = "konakart:storeName";

    String BASEDOCUMENT_DOC_TYPE = "konakart:basedocument";
    String ECOMMERCE_DOC_TYPE = "konakart:ecommerce";

    //############################################
    //    PRODUCT DEFINITIONS
    //############################################

    String PRODUCT_DOC_TYPE = "konakart:konakartproduct";

    String PRODUCT_ID = "konakart:ppid";
    String PRODUCT_IMAGES = "konakart:ppimages";
    String KONAKART_SECURITY_PROVIDER = "konakart";

    // Product Type constants
    static enum PRODUCT_TYPE {
        ALL(-1, "All", "All"),
        PHYSICAL_PRODUCT(0, "Physical Product", "konakart:physicalproduct"),
        DIGITAL_DOWNLOAD(1, "Digital Download", "konakart:digitaldownload"),
        PHYSICAL_PROD_FREE_SHIPPING(2, "Physical Prod-Free Shipping", "konakart:physicalproductfreeshipping"),
        BUNDLE(3, "Bundle", "konakart:bundle"),
        BUNDLE_FREE_SHIPPING(4, "Bundle Free Shipping", "konakart:bundlefreeshipping"),
        GIFT_CERTIFICATE(5, "Gift Certificate", "konakart:giftcertificate"),
        BOOKABLE_PRODUCT(6, "Bookable Product", "konakart:bookableproduct");

        private int type;
        private String name;
        private String namespace;


        private PRODUCT_TYPE(int type, String name, String namespace) {
            this.type = type;
            this.name = name;
            this.namespace = namespace;
        }

        public static PRODUCT_TYPE findByType(int type) {
            PRODUCT_TYPE[] productTypes = PRODUCT_TYPE.values();

            for (PRODUCT_TYPE productType : productTypes) {
                if (productType.getType() == type) {
                    return productType;
                }
            }

            return ALL;
        }

        public static PRODUCT_TYPE findByNamespace(String namespace) {
            PRODUCT_TYPE[] productTypes = PRODUCT_TYPE.values();

            for (PRODUCT_TYPE productType : productTypes) {
                if (productType.getNamespace().equalsIgnoreCase(namespace)) {
                    return productType;
                }
            }

            return ALL;
        }

        public int getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public String getNamespace() {
            return namespace;
        }
    }
}
