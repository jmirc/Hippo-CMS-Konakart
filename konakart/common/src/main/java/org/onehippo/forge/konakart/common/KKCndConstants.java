package org.onehippo.forge.konakart.common;

public interface KKCndConstants {


    String PRODUCT_TYPE_MIXIN = "konakart:isproduct";

    String NEW_PRODUCTS_FOLDER_TEMPLATE = "new-products-folder";
    String NEW_PRODUCT_DOCUMENT_TEMPLATE = "new-konakartproduct-document";

    String NEW_MANUFACTURERS_FOLDER_TEMPLATE = "new-manufacturers-folder";
    String NEW_MANUFACTURER_DOCUMENT_TEMPLATE = "new-manufacturer-document";

    String KONAKART_CONFIG_STORE_NAME = "konakart:storeName";

    String BASEDOCUMENT_DOC_TYPE = "konakart:basedocument";
    String ECOMMERCE_DOC_TYPE = "konakart:ecommerce";

    //############################################
    //    PRODUCT DEFINITIONS
    //############################################

    String PRODUCT_DOC_TYPE = "konakart:physicalproduct";

    String PRODUCT_NAME = "konakart:ppname";
    String PRODUCT_ID = "konakart:ppid";
    String PRODUCT_ABSTRACT = "konakart:ppabstract";
    String PRODUCT_DESCRIPTION = "konakart:ppdescription";
    String PRODUCT_SKU = "konakart:ppsku";
    String PRODUCT_STORE_ID = "konakart:ppstoreid";

    String PRODUCT_SPECIAL_PRICE = "konakart:ppspecialpriceextax";
    String PRODUCT_PRICE_0 = "konakart:pppriceextax";
    String PRODUCT_PRICE_1 = "konakart:pppriceextax1";
    String PRODUCT_PRICE_2 = "konakart:pppriceextax2";
    String PRODUCT_PRICE_3 = "konakart:pppriceextax3";

    String PRODUCT_QUANTITY = "konakart:ppquantity";
    String PRODUCT_WEIGHT = "konakart:ppweight";

    String PRODUCT_ORDER_NOT_IN_STOCK = "konakart:ppordernotinstock";

    String PRODUCT_TAX_CLASS = "konakart:pptaxclass";
    String PRODUCT_MANUFACTURER = "konakart:ppmanufacturer";

    String PRODUCT_IMAGES = "konakart:ppimages";
    String PRODUCT_CATEGORIES = "konakart:ppcategories";


    //############################################
    //    MANUFACTURER DEFINITIONS
    //############################################

    String MANUFACTURER_DOC_TYPE = "konakart:manufacturerdocument";
    String MANUFACTURER_ID = "konakart:manufacturerid";
    String MANUFACTURER_NAME = "konakart:manufacturername";
    String MANUFACTURER_IMAGE = "konakart:manufacturerimage";
    String MANUFACTURER_URL = "konakart:manufacturerurl";
    String MANUFACTURER_CUSTOM = "konakart:manufacturercustom";

    String REVIEW_DOC_TYPE = "konakart:review";
    String REVIEW_NAME = "konakart:reviewname";
    String REVIEW_EMAIL = "konakart:reviewemail";
    String REVIEW_RATING = "konakart:reviewrating";
    String REVIEW_VOTES = "konakart:reviewvotes";
    String REVIEW_COMMENT = "konakart:reviewcomment";
    String REVIEW_PRODUCT_LINK = "konakart:reviewproductlink";
    String REVIEW_CUSTOMER_ID = "konakart:reviewkonakartid";
    String REVIEW_DATE = "konakart:reviewdate";

    String DEFAULT_REVIEWS_FOLDER = "reviews";


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
