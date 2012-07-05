/*
 * =========================================================
 * Hippo CMS - Konakart
 * https://bitbucket.org/jmirc/hippo-cms-konakart
 * =========================================================
 * Copyright 2012
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================================================
 */

package org.onehippo.forge.konakart.hst.utils;

public interface KKCheckoutConstants {

    static final String DEF_STORE_ID = com.konakart.util.KKConstants.KONAKART_DEFAULT_STORE_ID;

    String ACTION = "action";

    // List of actions
    enum ACTIONS {
        REGISTER, LOGIN, SELECT, EDIT, UPDATE, REVIEW, ADD_TO_BASKET, REMOVE_FROM_BASKET, VALID_EMAIL, VALID_PASSWORD
    }

    String STATE = "state";

    enum STATES {
        INITIAL, CHECKOUT_METHOD_REGISTER, BILLING_ADDRESS,
        SHIPPING_ADDRESS, SHIPPING_METHOD, PAYMENT_METHOD, ORDER_REVIEW, CHECKOUT_FINISHED
    }


    String DONT_HAVE_ACCOUNT = "dontHaveAccount";
    String CHECKOUT_AS_GUEST = "checkoutAsGuest";
    String CHECKOUT_ASK_REGISTER = "checkoutAskRegister";


    String PRODUCT_ID = "prodId";
    String BASKET_ID = "basketId";
    String ADD_TO_WISH_LIST = "addToWishList";
    String WISH_LIST_ID = "wishListId";

    /*
    * Customer tags
    */
    String TAG_PRODUCTS_VIEWED = "PRODUCTS_VIEWED";

}
