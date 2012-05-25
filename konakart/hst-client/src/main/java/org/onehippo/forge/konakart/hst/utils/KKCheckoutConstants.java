package org.onehippo.forge.konakart.hst.utils;

public interface KKCheckoutConstants {

    static final String DEF_STORE_ID = com.konakart.util.KKConstants.KONAKART_DEFAULT_STORE_ID;


    String ACTION = "action";

    // List of actions
    enum ACTIONS {
        LOGIN, SELECT, EDIT, UPDATE, REVIEW, ADD_TO_BASKET, REMOVE_FROM_BASKET
    }

    String PRODUCT_ID = "prodId";
    String BASKET_ID = "basketId";
    String ADD_TO_WISH_LIST = "addToWishList";
    String WISH_LIST_ID = "wishListId";

    /*
    * Customer tags
    */
    String TAG_PRODUCTS_VIEWED = "PRODUCTS_VIEWED";

}
