package org.onehippo.forge.konakart.common.engine;

import com.konakart.app.KKException;
import com.konakart.appif.KKEngIf;
import com.konakart.appif.LanguageIf;
import org.onehippo.forge.konakart.common.al.*;

import java.math.BigDecimal;
import java.util.Locale;

public interface KKEngineIf {

    static final String KONAKART_PROPERTIES = "konakart.properties";
    static final String KONAKART_KEY = "konakart-key";

    /**
     * @return konakart engine
     */
    KKEngIf getEngine();

    /**
     * @return the session id
     */
    String getSessionId();

    /**
     * @return the current locle
     */
    String getLocale();

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
     * @return the review manager
     */
    ReviewMgr getReviewMgr();

    /**
     * @return the customer tag manager
     */
    CustomerTagMgr getCustomerTagMgr();

    /**
     * @return the order manager
     */
    OrderMgr getOrderMgr();



    /**
     * Used to create a string in order to visualize a price. It ensures that the decimal places,
     * the thousands separator and the currency symbol are correct. Uses the default currency.
     *
     * @param number to be formatted
     * @return The formatted price
     * @throws KKException .
     */
    String formatPrice(BigDecimal number) throws KKException;

    /**
     * Used to create a string in order to visualise a price. It ensures that the decimal places,
     * the thousands separator and the currency symbol are correct.
     * An entry must exist in the database for the currency code passed in as a parameter.
     *
     * @param numberToFormat number to be formatted
     * @param currencyCode   Three letter currency code (USD, GBP, EUR etc.)
     * @return The formatted price
     * @throws KKException .
     */
    public String formatPrice(BigDecimal numberToFormat, String currencyCode) throws KKException;
}