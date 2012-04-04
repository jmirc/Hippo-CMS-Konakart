package org.onehippo.forge.konakart.common;

public interface KKCndConstants {

    public static final String BASEDOCUMENT_DOC_TYPE = "konakart:basedocument";

    public static final String PRODUCT_DOC_TYPE = "konakart:konakart";
    public static final String PRODUCT_NAME = "konakart:name";
    public static final String PRODUCT_ID = "konakart:id";
    public static final String PRODUCT_DESCRIPTION = "konakart:description";
    public static final String PRODUCT_SKU = "konakart:sku";
    public static final String PRODUCT_LANGUAGE_ID = "konakart:languageid";
    public static final String PRODUCT_STORE_ID = "konakart:storeid";
    public static final String PRODUCT_MANUFACTURER = "konakart:manufacturer";
    public static final String PRODUCT_PRODUCT_TYPE = "konakart:producttype";
    public static final String PRODUCT_MANUFACTURER_ID = "konakart:manufacturerid";
    public static final String PRODUCT_SPECIAL_PRICE = "konakart:specialprice";
    public static final String PRODUCT_STANDARD_PRICE = "konakart:standardcompoundprice";
    public static final String PRODUCT_IMAGES = "konakart:images";

    public static final String CP_PRICE_TYPE = "konakart:compoundprice";
    public static final String CP_PRICE_0 = "konakart:priceextax";
    public static final String CP_PRICE_1 = "konakart:price1extax";
    public static final String CP_PRICE_2 = "konakart:price2extax";
    public static final String CP_PRICE_3 = "konakart:price3extax";

    public static final String REVIEW_DOC_TYPE = "konakart:review";
    public static final String REVIEW_NAME = "konakart:reviewname";
    public static final String REVIEW_EMAIL = "konakart:reviewemail";
    public static final String REVIEW_RATING = "konakart:reviewrating";
    public static final String REVIEW_VOTES = "konakart:reviewvotes";
    public static final String REVIEW_COMMENT = "konakart:reviewcomment";
    public static final String REVIEW_PRODUCT_LINK = "konakart:reviewproductlink";
    public static final String REVIEW_CUSTOMER_ID = "konakart:reviewkonakartid";
    public static final String REVIEW_DATE = "konakart:reviewdate";


    public static final String DEFAULT_REVIEWS_FOLDER = "reviews";


    // Product Type constants
    public static enum PRODUCT_TYPE {
        ALL(-1, "All"),
        PHYSICAL_PRODUCT(0, "Physical Product"),
        DIGITAL_DOWNLOAD(1, "Digital Download"),
        PHYSICAL_PROD_FREE_SHIPPING(2, "Physical Prod-Free Shipping"),
        BUNDLE(3, "Bundle"),
        BUNDLE_FREE_SHIPPING(4, "Bundle Free Shipping"),
        GIFT_CERTIFICATE(5, "Gift Certificate"),
        BOOKABLE_PRODUCT(6, "Bookable Product");

        private int type;
        private String name;
        
        
        private PRODUCT_TYPE(int type, String name) {
            this.type = type;
            this.name = name;
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

        public int getType() {
            return type;
        }

        public String getName() {
            return name;
        }
    }
}
