package org.onehippo.forge.konakart.common.al;

import com.konakart.app.KKException;
import com.konakart.appif.AddressIf;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.ProductIf;
import org.onehippo.forge.konakart.common.engine.KKEngine;

import java.math.BigDecimal;

/**
 * Contains methods to fetch and manage lists of products and keeps track of the current selected product.
 */
public class ProductMgr extends BaseMgr {

    /**
     * Default constuctor
     *
     * @param kkEngine the Konakart Engine
     */
    public ProductMgr(KKEngine kkEngine) {
        super(kkEngine);
    }

    /**
     * Retrieve a product by his ID
     *
     * @param productId  id of the product to search
     * @param languageId id of the language associated with the product
     * @return the product
     * @throws KKException .
     */
    public ProductIf getProductById(int productId, int languageId) throws KKException {
        return kkEngine.getEngine().getProduct(kkEngine.getSessionId(), productId, languageId);
    }

    /**
     * Called to get the price of a product.
     *
     * When KonaKart is configured to display prices that include tax, the price displayed may be different
     * for logged in users from different tax zones. This method calculates the price to display.
     *
     * If the user isn't logged in, then it returns the standard price including tax defined by the product.
     *
     * @param paramProductIf the product
     * @return the price including tax
     */
    public BigDecimal getPriceIncTax(ProductIf paramProductIf) {
        return getPriceIncTaxPrivate(paramProductIf, false);
    }

    /**
     * to get the special price of a random product. Random products are kept in a static list for performance reasons.
     * When KonaKart is configured to display prices that include tax, the price displayed may be different for logged
     * in users from different tax zones. This method calculates the price to display.
     *
     * If the user isn't logged in, then it returns the standard special price including tax defined by the product.
     *
     * @param paramProductIf the product
     * @return the price including tax
     */
    public BigDecimal getSpecialPriceIncTax(ProductIf paramProductIf) {
        return getPriceIncTaxPrivate(paramProductIf, true);
    }

    private BigDecimal getPriceIncTaxPrivate(ProductIf product, boolean specialPrice) {
        if (product == null) {
            return null;
        }

        BigDecimal priceIncTax;
        BigDecimal priceExTax;

        if (specialPrice) {
            priceIncTax = product.getSpecialPriceIncTax();
            priceExTax = product.getSpecialPriceExTax();
        } else {
            priceIncTax = product.getPriceIncTax();
            priceExTax = product.getPriceExTax();
        }

        if (priceExTax == null) {
            return priceIncTax;
        }

        try {
            if (kkEngine.getSessionId() != null) {
                CustomerIf currentCustomer = kkEngine.getCustomerMgr().getCurrentCustomer();

                if (currentCustomer != null) {
                    AddressIf customerAddress = currentCustomer.getDefaultAddr();

                    // Temporary customer
                    if (customerAddress == null) {
                        kkEngine.getCustomerMgr().populateCurrentCustomerAddresses(false);
                        customerAddress = currentCustomer.getDefaultAddr();
                    }

                    if (customerAddress != null) {
                        return kkEng.addTax(priceExTax, customerAddress.getCountryId(), customerAddress.getZoneId(),
                                product.getTaxClassId());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Unable to calculate price including tax.", e);
        }

        return priceIncTax;
    }


}
