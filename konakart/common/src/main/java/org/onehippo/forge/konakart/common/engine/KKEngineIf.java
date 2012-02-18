package org.onehippo.forge.konakart.common.engine;

import com.konakart.appif.LanguageIf;
import org.onehippo.forge.konakart.common.al.BasketMgr;
import org.onehippo.forge.konakart.common.al.CustomerMgr;
import org.onehippo.forge.konakart.common.al.ProductMgr;
import org.onehippo.forge.konakart.common.al.WishListMgr;

public interface KKEngineIf {

    static final String KONAKART_PROPERTIES = "konakart.properties";
    static final String KONAKART_KEY = "konakart-key";

    /**
     * Login to the session
     *
     * @param username the username of the user
     * @param password the password of the user
     *
     * @throws Exception if the logged-in process failed
     */
    void login(String username, String password) throws Exception;

    /**
     * Logout from Konakart
     * @throws Exception if the logout process failed
     */
    void logout() throws Exception;

    /**
     * @return true if the cookie are enabled, false otherwise
     */
    boolean isKkCookieEnabled();

    /**
     * @return the current language
     */
    LanguageIf getLanguage();


    /**
     * @return the customer manager
     */
    CustomerMgr getCustomerMgr();

    /**
     * @return the basket manager
     */
    BasketMgr getBasketMgr();

    /**
     * @return the wishList manager
     */
    WishListMgr getWishListMgr();

    /**
     * @return the product manager
     */
    ProductMgr getProductMgr();


    /**
     * @return the language manager
     */
//    LanguageMgr getLangMgr();
}
