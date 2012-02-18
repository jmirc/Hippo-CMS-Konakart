package org.onehippo.forge.konakart.common.al;

import com.konakart.app.AddToBasketOptions;
import com.konakart.app.KKException;
import com.konakart.appif.AddToBasketOptionsIf;
import com.konakart.appif.BasketIf;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.KKEngIf;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.common.engine.KKEngine;

import java.math.BigDecimal;

/**
 * Contains methods to add and remove products from the shopping basket.
 */
public class BasketMgr extends BaseMgr {

    private BigDecimal basketTotal = new BigDecimal(0.0D);

    /**
     * Default constuctor
     *
     * @param kkEngine the Konakart Engine
     */
    public BasketMgr(KKEngine kkEngine) {
        super(kkEngine);
    }

    /**
     * Saves a new Basket object in the database for the current customer.
     * We read a new list from the database and refresh the current customer's basket if refresh is set to true.
     *
     * @param item    The basket object to be added
     * @param refresh If set to true, the current customer's basket is refreshed
     * @throws KKException .
     */
    public void addToBasket(com.konakart.appif.BasketIf item, boolean refresh) throws KKException {
        addToBasketWithOptions(item, getAddToBasketOptions(), refresh);
    }

    /**
     * Saves a new Basket object in the database for the current customer.
     * We read a new list from the database and refresh the current customer's basket if refresh is set to true.
     * It receives an options object as input in order to configure certain aspects of the method.
     *
     * @param item    The basket object to be added
     * @param options An object containing options for the method. It may be set to null.
     * @param refresh If set to true, the current customer's basket is refreshed
     * @throws KKException .
     */
    public void addToBasketWithOptions(BasketIf item, AddToBasketOptionsIf options, boolean refresh)
            throws KKException {

        if ((item != null) && (kkEngine.getCustomerMgr().getCurrentCustomer() != null)) {
            kkEng.addToBasketWithOptions(kkEngine.getSessionId(), kkEngine.getCustomerMgr().getCurrentCustomer().getId(),
                    item, options);

            if (refresh) {
                getBasketItemsPerCustomer();
            }
        }
    }

    /**
     * The Basket object is removed from the database and so no longer appears in the customer's basket.
     * We read a new list from the database and refresh the current customer's basket if refresh is set to true.
     *
     * @param item    The basket object to be removed
     * @param refresh If set to true, the current customer's basket is refreshed
     * @throws KKException .
     */
    public void removeFromBasket(BasketIf item, boolean refresh) throws KKException {
        if ((item != null) && (kkEngine.getCustomerMgr().getCurrentCustomer() != null)) {
            kkEng.removeFromBasket(kkEngine.getSessionId(), kkEngine.getCustomerMgr().getCurrentCustomer().getId(), item);

            if (refresh) {
                getBasketItemsPerCustomer();
            }
        }
    }

    /**
     * Updates the Basket object in the database. The only attribute that may be changed is quantity.
     * We read a new list from the database and refresh the current customer's basket if refresh is set to true.
     *
     * @param item    The basket object to be updated
     * @param refresh If set to true, the current customer's basket is refreshed
     * @throws KKException .
     */
    public void updateBasket(BasketIf item, boolean refresh) throws KKException {
        if ((item != null) && (kkEngine.getCustomerMgr().getCurrentCustomer() != null)) {
            kkEng.updateBasketWithOptions(kkEngine.getSessionId(), kkEngine.getCustomerMgr().getCurrentCustomer().getId(),
                    item, getAddToBasketOptions());

            if (refresh) {
                getBasketItemsPerCustomer();
            }
        }
    }

    /**
     * Returns the total price of the basket as a formatted string so that it may be used directly in the UI.
     *
     * @return Total value of basket already formatted
     * @throws KKException .
     */
    public String getBasketTotal() throws KKException {
        return kkEngine.formatPrice(basketTotal);
    }


    /**
     * Get the basket items for a customer and language
     * and set them on the customer customer object of the customerMgr.
     *
     * @throws com.konakart.app.KKException .
     */
    public void getBasketItemsPerCustomer() throws KKException {

        CustomerIf currentCustomer = kkEngine.getCustomerMgr().getCurrentCustomer();

        // If the customer is null, we can't retrieve the basket
        if (currentCustomer != null) {
            BasketIf[] baskets = kkEng.getBasketItemsPerCustomerWithOptions(kkEngine.getSessionId(),
                    currentCustomer.getId(), kkEngine.getLanguage().getId(), getAddToBasketOptions());

            // Set the current basket to the engine
            currentCustomer.setBasketItems(baskets);

            // Refresh the total
            updateBasketTotal();
        }
    }

    /**
     * All items in the basket are removed from the database
     * and the array of basket items for the current customer, are deleted.
     *
     * @throws com.konakart.app.KKException .
     */
    public void emptyBasket() throws KKException {
        kkEng.removeBasketItemsPerCustomer(kkEngine.getSessionId(), 0);
        getBasketItemsPerCustomer();
    }

    /**
     * @return the basket option set with the parameter initialized by Hippo
     */
    private AddToBasketOptionsIf getAddToBasketOptions() {

        AddToBasketOptions options = new AddToBasketOptions();

        if (StringUtils.isNotEmpty(kkEngine.getCatalogId())) {
            options.setCatalogId(kkEngine.getCatalogId());
        }

        options.setUseExternalPrice(kkEngine.getCatalogId() != null);

        return options;
    }

    /**
     * Calculate the total
     * @throws KKException .
     */
    private void updateBasketTotal() throws KKException {
        if (kkEngine.getCustomerMgr().getCurrentCustomer() != null) {

            BigDecimal total = new BigDecimal(0.0D);

            if ((kkEngine.getCustomerMgr().getCurrentCustomer().getBasketItems() != null) &&
                    (kkEngine.getCustomerMgr().getCurrentCustomer().getBasketItems().length > 0)) {


                BasketIf[] items = kkEngine.getCustomerMgr().getCurrentCustomer().getBasketItems();
                boolean displayPriceWithTax = kkEngine.displayPriceWithTax();


                for (BasketIf item : items) {
                    if (displayPriceWithTax) {
                        total = total.add(item.getFinalPriceIncTax());
                    } else {
                        total = total.add(item.getFinalPriceExTax());
                    }
                }
            }

            this.basketTotal = total;
        }
    }
}
