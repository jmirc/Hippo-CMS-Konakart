package org.onehippo.forge.konakart.common;

public interface KKCndConstants {

    static final String BASEDOCUMENT_DOC_TYPE = "konakart:basedocument";

    static final String KONAKART_PRODUCT_TYPE_MIXIN = "konakart:producttype";

    static final String PRODUCT_DOC_TYPE = "konakart:physicalproduct";
    static final String PRODUCT_NAME = "konakart:ppname";
    static final String PRODUCT_ID = "konakart:ppid";
    static final String PRODUCT_DESCRIPTION = "konakart:ppdescription";
    static final String PRODUCT_SKU = "konakart:ppsku";
    static final String PRODUCT_STORE_ID = "konakart:ppstoreid";
    static final String PRODUCT_LANGUAGE_ID = "konakart:pplanguageid";

    static final String PRODUCT_PRICE_0 = "konakart:pppriceextax";
    static final String PRODUCT_PRICE_1 = "konakart:pppriceextax1";
    static final String PRODUCT_PRICE_2 = "konakart:pppriceextax2";
    static final String PRODUCT_PRICE_3 = "konakart:pppriceextax3";

    static final String PRODUCT_QUANTITY = "konakart:ppquantity";
    static final String PRODUCT_WEIGHT = "konakart:ppweight";

    static final String PRODUCT_ORDER_NOT_IN_STOCK = "konakart:ppordernotinstock";

    static final String PRODUCT_TAX_CLASS = "konakart:pptaxclass";
    static final String PRODUCT_MANUFACTURER = "konakart:ppmanufacturer";

    static final String PRODUCT_IMAGES = "konakart:ppimages";
    static final String PRODUCT_CATEGORIES = "konakart:ppcategories";



    static final String REVIEW_DOC_TYPE = "konakart:review";
    static final String REVIEW_NAME = "konakart:reviewname";
    static final String REVIEW_EMAIL = "konakart:reviewemail";
    static final String REVIEW_RATING = "konakart:reviewrating";
    static final String REVIEW_VOTES = "konakart:reviewvotes";
    static final String REVIEW_COMMENT = "konakart:reviewcomment";
    static final String REVIEW_PRODUCT_LINK = "konakart:reviewproductlink";
    static final String REVIEW_CUSTOMER_ID = "konakart:reviewkonakartid";
    static final String REVIEW_DATE = "konakart:reviewdate";

    static final String DEFAULT_REVIEWS_FOLDER = "reviews";


    // Product Type constants
    static enum PRODUCT_TYPE {
        ALL(-1, "All", "All"),
        PHYSICAL_PRODUCT(0, "Physical Product", "physicalproduct"),
        DIGITAL_DOWNLOAD(1, "Digital Download", "digitaldownload"),
        PHYSICAL_PROD_FREE_SHIPPING(2, "Physical Prod-Free Shipping", "physicalproductfreeshipping"),
        BUNDLE(3, "Bundle", "bundle"),
        BUNDLE_FREE_SHIPPING(4, "Bundle Free Shipping", "bundlefreeshipping"),
        GIFT_CERTIFICATE(5, "Gift Certificate", "giftcertificate"),
        BOOKABLE_PRODUCT(6, "Bookable Product", "bookableproduct");

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
