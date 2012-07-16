package org.onehippo.forge.konakart.hst.utils;

import com.konakart.util.KKConstants;

public interface KKActionsConstants {

    static final String DEF_STORE_ID = KKConstants.KONAKART_DEFAULT_STORE_ID;

    String ACTION = "action";

    // List of actions
    enum ACTIONS {
        REGISTER, CREATE_ACCOUNT, SELECT, EDIT, UPDATE, REVIEW, ADD_TO_BASKET, REMOVE_FROM_BASKET
    }

    String STATE = "state";

    enum STATES {
        INITIAL, CHECKOUT_METHOD_REGISTER, BILLING_ADDRESS,
        SHIPPING_ADDRESS, SHIPPING_METHOD, PAYMENT_METHOD, ORDER_REVIEW, CHECKOUT_FINISHED
    }

    String FORCE_NEXT_LOGGED_STATE = "FORCE_NEXT_LOGGED_STATE";
    String FORCE_NEXT_NON_LOGGED_STATE = "FORCE_NEXT_NON_LOGGED_STATE";

    String DONT_HAVE_ACCOUNT = "dontHaveAccount";
    String CHECKOUT_AS_GUEST = "checkoutAsGuest";
    String CHECKOUT_ASK_REGISTER = "checkoutAskRegister";


    String PRODUCT_ID = "prodId";
    String BASKET_ID = "basketId";
    String QUANTITY = "quantity";
    String ADD_TO_WISH_LIST = "addToWishList";
    String WISH_LIST_ID = "wishListId";

}
