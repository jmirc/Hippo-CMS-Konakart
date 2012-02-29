package org.onehippo.forge.konakart.common.al;

import com.konakart.app.DataDescriptor;
import com.konakart.app.FetchProductOptions;
import com.konakart.app.KKException;
import com.konakart.appif.AddressIf;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.FetchProductOptionsIf;
import com.konakart.appif.ProductIf;
import com.konakart.bl.ConfigConstants;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.common.engine.KKEngine;

import java.math.BigDecimal;

import static com.konakart.bl.ConfigConstants.*;

/**
 * Contains methods to fetch and manage lists of products and keeps track of the current selected product.
 */
public class ProductMgr extends BaseMgr {

    private StaticData sd = new StaticData();

    private int maxProdRowsUser;


    // Order history
    ProductIf[] orderHistory;

    /**
     * Default constuctor
     *
     * @param kkEngine the Konakart Engine
     */
    public ProductMgr(KKEngine kkEngine) {
        super(kkEngine);

        loadConfigs();
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
     * <p/>
     * When KonaKart is configured to display prices that include tax, the price displayed may be different
     * for logged in users from different tax zones. This method calculates the price to display.
     * <p/>
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
     * <p/>
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


    /**
     * Populates the orderHistory array for the current logged in user.
     *
     * @throws com.konakart.app.KKException .
     */
    public void fetchOrderHistoryArray() throws KKException {
        if (kkEngine.getSessionId() == null) {
            return;
        }

        DataDescriptor dataDescriptor = new DataDescriptor();
        dataDescriptor.setLimit(sd.maxDispNewProds);
        dataDescriptor.setOffset(0);
        dataDescriptor.setOrderBy(null);
        orderHistory = kkEng.getOrderHistoryWithOptions(dataDescriptor,
                kkEngine.getSessionId(), kkEngine.getLanguage().getId(), getFetchProdOptions());
    }

    /**
     * Load configurations
     */
    private void loadConfigs() {
        if (kkEngine.getConfig(MAX_DISPLAY_SEARCH_RESULTS) != null) {
            sd.maxProdRows = new Integer(kkEngine.getConfig(MAX_DISPLAY_SEARCH_RESULTS));
        }

        if (kkEngine.getConfig(MAX_DISPLAY_PAGE_LINKS) != null) {
            sd.maxPageLinks = new Integer(kkEngine.getConfig(MAX_DISPLAY_PAGE_LINKS));
        }
        
        if (kkEngine.getConfig(MAX_RANDOM_SELECT_NEW) != null) {
            sd.maxRandomNewProds = new Integer(kkEngine.getConfig(MAX_RANDOM_SELECT_NEW));
        }

        if (kkEngine.getConfig(MAX_RANDOM_SELECT_SPECIALS) != null) {
            sd.maxRandomSpecials = new Integer(kkEngine.getConfig(MAX_RANDOM_SELECT_SPECIALS));
        }

        if (kkEngine.getConfig(MAX_DISPLAY_MANUFACTURER_NAME_LEN) != null) {
            sd.maxDispManuNameLength = new Integer(kkEngine.getConfig(MAX_DISPLAY_MANUFACTURER_NAME_LEN));
        }

        if (kkEngine.getConfig(MAX_DISPLAY_PRODUCTS_IN_ORDER_HISTORY_BOX) != null) {
            sd.maxDispOrderHistoryRows = new Integer(kkEngine.getConfig(MAX_DISPLAY_PRODUCTS_IN_ORDER_HISTORY_BOX));
        }

        if (kkEngine.getConfig(MAX_DISPLAY_ALSO_PURCHASED) != null) {
            sd.maxDispAlsoPurchased = new Integer(kkEngine.getConfig(MAX_DISPLAY_ALSO_PURCHASED));
        }

        if (kkEngine.getConfig(MAX_DISPLAY_UP_SELL) != null) {
            sd.maxDispUpSellProds = new Integer(kkEngine.getConfig(MAX_DISPLAY_UP_SELL));
        }

        if (kkEngine.getConfig(MAX_DISPLAY_CROSS_SELL) != null) {
            sd.maxDispCrossSellProds = new Integer(kkEngine.getConfig(MAX_DISPLAY_CROSS_SELL));
        }

        if (kkEngine.getConfig(MAX_DISPLAY_ACCESSORIES) != null) {
            sd.maxDispAccessories = new Integer(kkEngine.getConfig(MAX_DISPLAY_ACCESSORIES));
        }

        if (kkEngine.getConfig(MAX_DISPLAY_DEPENDENT_PRODUCTS) != null) {
            sd.maxDispDependentProds = new Integer(kkEngine.getConfig(MAX_DISPLAY_DEPENDENT_PRODUCTS));
        }

        if (kkEngine.getConfig(MAX_DISPLAY_NEW_PRODUCTS) != null) {
            sd.maxDispNewProds = new Integer(kkEngine.getConfig(MAX_DISPLAY_NEW_PRODUCTS));
        }

        if (kkEngine.getConfig(MAX_DISPLAY_BESTSELLERS) != null) {
            sd.maxDispBestSellers = new Integer(kkEngine.getConfig(MAX_DISPLAY_BESTSELLERS));
        }

        String cStockAllowCheckout = kkEngine.getConfig(STOCK_ALLOW_CHECKOUT);
        if (StringUtils.isNotBlank(cStockAllowCheckout) && (cStockAllowCheckout.equalsIgnoreCase("true"))) {
            sd.stockAllowCheckout = true;
        }

        String cStockCheck = kkEngine.getConfig(STOCK_CHECK);
        if (StringUtils.isNotBlank(cStockCheck) && (cStockCheck.equalsIgnoreCase("true"))) {
            sd.stockCheck = true;
        }
    }
    

    /**
     * @return the FetchProductOptionsIf configuration.
     */
    private FetchProductOptionsIf getFetchProdOptions() {

        FetchProductOptions fetchProductOptions = new FetchProductOptions();
        if (!StringUtils.isEmpty(kkEngine.getCatalogId())) {
            fetchProductOptions.setCatalogId(kkEngine.getCatalogId());
            fetchProductOptions.setUseExternalPrice(kkEngine.getCatalogId() != null);
        }

        return new FetchProductOptions();
    }

    /**
     * Used to set a user defined maximum number of search results displayed. When set to a number greater than zero,
     * this value is used instead of the value in the configuration variable MAX_DISPLAY_SEARCH_RESULTS.
     *
     * @param prodPageSize set the number of product per page
     */
    public void setMaxDisplaySearchResults(int prodPageSize) {

        if (getMaxDisplaySearchResult() != prodPageSize) {
            this.maxProdRowsUser = prodPageSize;
            
        }
        
    }

    public int getMaxDisplaySearchResult() {
        if (maxProdRowsUser > 0) {
            return maxProdRowsUser;
        }

        return sd.maxProdRows;
    }


    private static class StaticData {
        protected int maxRandomSpecials = 20;
        protected int maxRandomNewProds = 20;
        protected int maxProdRows = 7;
        protected int maxPageLinks = 5;
        protected int maxDispManuNameLength = 15;
        protected int maxDispOrderHistoryRows = 6;
        protected int maxDispAlsoPurchased = 6;
        protected int maxDispBestSellers = 6;
        protected int maxDispNewProds = 9;
        protected int maxDispUpSellProds = 6;
        protected int maxDispCrossSellProds = 6;
        protected int maxDispAccessories = 6;
        protected int maxDispDependentProds = 6;
        protected boolean stockAllowCheckout = true;
        protected boolean stockCheck = true;
    }
}
