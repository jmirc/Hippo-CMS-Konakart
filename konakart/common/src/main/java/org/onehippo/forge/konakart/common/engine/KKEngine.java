package org.onehippo.forge.konakart.common.engine;

import com.konakart.app.EngineConfig;
import com.konakart.app.KKException;
import com.konakart.appif.CurrencyIf;
import com.konakart.appif.KKConfigurationIf;
import com.konakart.appif.KKEngIf;
import com.konakart.appif.LanguageIf;
import com.konakart.bl.ConfigConstants;
import com.konakart.util.KKConstants;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.common.al.BasketMgr;
import org.onehippo.forge.konakart.common.al.CustomerMgr;
import org.onehippo.forge.konakart.common.al.ProductMgr;
import org.onehippo.forge.konakart.common.al.WishListMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The purpose of this engine is only for the replication. Please use KKClient for the HST connection.
 */
public class KKEngine implements KKEngineIf {

    protected Logger log = LoggerFactory.getLogger(KKEngine.class);

    private static ConcurrentHashMap<String, String> configMap = new ConcurrentHashMap<String, String>();

    private KKEngIf engine;
    private KKEngineConfig config;
    private String sessionId;
    private String storeId = KKConstants.KONAKART_DEFAULT_STORE_ID;
    private String catalogId;
    private boolean kkCookieEnabled;
    private LanguageIf language;

    // Currency
    private CurrencyIf defaultCurrency;
    private DecimalFormat defaultCurrencyFormatter;

    private CurrencyIf userCurrency;
    private DecimalFormat userCurrencyFormatter;

    // Different managers
    private CustomerMgr customerMgr;
    private BasketMgr basketMgr;
    private WishListMgr wishListMgr;
    private ProductMgr productMgr;

    /**
     * Default contructor
     *
     * @param config the Konakart engine config.
     * @throws Exception .
     */
    public KKEngine(KKEngineConfig config) throws Exception {
        this(config, null);
    }

    /**
     * Default contructor
     *
     * @param config the Konakart engine config.
     * @param locale the current locale
     * @throws Exception .
     */
    public KKEngine(KKEngineConfig config, Locale locale) throws Exception {
        this.config = config;

        init(locale);
    }

    @Override
    public void login(String username, String password) throws KKException {
        /*
        * Login with default credentials
        */
        sessionId = engine.login(username, password);

        if (sessionId == null) {
            String msg = "Login of " + username + " was unsuccessful";
            throw new KKException(msg);
        }
    }

    @Override
    public void logout() throws Exception {
        if (sessionId != null) {
            engine.logout(sessionId);
        }

    }

    @Override
    public boolean isKkCookieEnabled() {
        return kkCookieEnabled;
    }

    @Override
    public LanguageIf getLanguage() {
        return language;
    }

    @Override
    public CustomerMgr getCustomerMgr() {
        return customerMgr;
    }

    @Override
    public BasketMgr getBasketMgr() {
        return basketMgr;
    }

    @Override
    public WishListMgr getWishListMgr() {
        return wishListMgr;
    }

    @Override
    public ProductMgr getProductMgr() {
        return productMgr;
    }


    /**
     * Called by the UI to determine whether to display prices with tax.
     *
     * @return True if we should display prices with tax
     */
    public boolean displayPriceWithTax() {
        String str = getConfig(ConfigConstants.DISPLAY_PRICE_WITH_TAX);

        return (str == null) || (!str.equalsIgnoreCase("false"));
    }


    /**
     * Retrieve the Konakart configuration
     *
     * @param key the key to search
     * @return the value associated with the key or null if the key has not been found
     */
    public String getConfig(String key) {
        if (configMap.containsKey(key)) {
            return configMap.get(key);
        }

        return null;
    }

    /**
     * @return konakart engine
     */
    public KKEngIf getEngine() {
        return engine;
    }

    /**
     * @return the session id
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @return the store id
     */
    public String getStoreId() {
        return storeId;
    }

    /**
     * @param storeId the store id
     */
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    /**
     * @return the catalog id
     */
    public String getCatalogId() {
        return catalogId;
    }

    /**
     * @param catalogId the catalog id
     */
    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }


    /**
     * Used to create a string in order to visualize a price. It ensures that the decimal places,
     * the thousands separator and the currency symbol are correct. Uses the default currency.
     *
     * @param number to be formatted
     * @return The formatted price
     * @throws KKException .
     */
    public String formatPrice(BigDecimal number) throws KKException {
        return formatPrice(number, null);
    }

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
    public String formatPrice(BigDecimal numberToFormat, String currencyCode) throws KKException {

        if (numberToFormat == null) {
            return "null";
        }

        CurrencyIf localCurrencyIf;
        DecimalFormat localDecimalFormat;

        // Currency code
        if (currencyCode == null) {
            localCurrencyIf = defaultCurrency;
            localDecimalFormat = defaultCurrencyFormatter;
        } else if (currencyCode.equals(getUserCurrency().getCode())) {
            localCurrencyIf = defaultCurrency;
            localDecimalFormat = defaultCurrencyFormatter;
        } else { // Retrieve the currency from Konakart
            try {
                localCurrencyIf = engine.getCurrency(currencyCode);
            } catch (KKException localKKException) {
                throw new KKException("A currency cannot be found for currency code = " + currencyCode, localKKException);
            }

            if (localCurrencyIf == null)
                throw new KKException("A currency cannot be found for currency code = " + currencyCode);

            localDecimalFormat = CurrencyUtil.getFormatter(localCurrencyIf);
        }

        // Use the user's currency
        if ((currencyCode == null) && (this.userCurrency != null) && (this.userCurrency.getId() != defaultCurrency.getId())
                && (this.userCurrencyFormatter != null)) {
            BigDecimal currencyMutiplicater = this.userCurrency.getValue();
            numberToFormat = numberToFormat.multiply(currencyMutiplicater);
        }


        String price = localDecimalFormat.format(numberToFormat);

        if ((localCurrencyIf.getSymbolLeft() != null) && (localCurrencyIf.getSymbolLeft().length() > 0)) {
            return localCurrencyIf.getSymbolLeft() + price;
        }

        if ((localCurrencyIf.getSymbolRight() != null) && (localCurrencyIf.getSymbolRight().length() > 0)) {
            return price + localCurrencyIf.getSymbolRight();
        }

        return price;
    }

    /**
     * Returns the user's currency which may be different to the default currency
     *
     * @return Returns a currency object
     */
    public CurrencyIf getUserCurrency() {
        if (this.userCurrency == null)
            return defaultCurrency;
        return this.userCurrency;
    }


    /**
     * Initialise a KonaKart engine instance and perform a login to get a session id.
     *
     * @param locale the current locale
     * @throws Exception .
     */
    private void init(Locale locale) throws Exception {
        EngineConfig engConf = new EngineConfig();
        engConf.setMode(config.getEngineMode());
        engConf.setStoreId(getStoreId());
        engConf.setCustomersShared(config.isCustomersShared());
        engConf.setProductsShared(config.isProductsShared());

        if (System.getProperty(KONAKART_PROPERTIES) != null) {
            engConf.setPropertiesFileName(System.getProperty(KONAKART_PROPERTIES));
        }

        /*
         * Instantiate a KonaKart Engine. Different engines can be instantiated by passing
         * KKWSEngName or KKRMIEngName or KKJSONEngName for the SOAP, RMI or JSON engines
         */
        engine = getKKEngByName(config.getEngineClassName(), engConf);

        // Initialize all managers
        customerMgr = new CustomerMgr(this);
        basketMgr = new BasketMgr(this);
        wishListMgr = new WishListMgr(this);
        productMgr = new ProductMgr(this);

        // Retrieve the list of languages defined into konakart.
        setLanguageId(locale);

        // Set the default currency
        defaultCurrency = getEngine().getDefaultCurrency();
        defaultCurrencyFormatter = CurrencyUtil.getFormatter(defaultCurrency);

        // Retrieve the configurations from database
        initKonakartConfigurations();

        // Check if the cookie are installed
        checkKKCookieInstalled();
    }

    /**
     * Read the Konakart configurations and insert them into the cache
     *
     * @throws com.konakart.app.KKException .
     */
    private void initKonakartConfigurations() throws KKException {
        KKConfigurationIf[] kkConfigurationIfs = engine.getConfigurations();

        for (KKConfigurationIf kkConfigurationIf : kkConfigurationIfs) {
            configMap.put(kkConfigurationIf.getKey(), kkConfigurationIf.getValue());
        }

    }

    /**
     * Set the Konakart language from the current locale that has been set by Hippo CMS
     *
     * @param locale the current locale
     * @throws KKException if the language can't be set
     */
    private void setLanguageId(Locale locale) throws KKException {
        LanguageIf[] languages = engine.getAllLanguages();

        if (locale == null) {
            this.language = engine.getDefaultLanguage();
            return;
        }

        String sLocale = locale.toString();

        for (LanguageIf language : languages) {
            if (StringUtils.equals(language.getLocale(), sLocale)) {
                this.language = language;
                break;
            }
        }

        if (this.language == null) {
            log.error("Unable to find a Konakart language for the locale - " + sLocale);
        }
    }

    /**
     * Utility method to instantiate an engine instance. The class name of the engine is passed in
     * as a parameter so this method may be used to instantiate a POJO engine, a SOAP engine, an RMI
     * engine or a JSON engine.
     *
     * @param engineClassName the engine class name
     * @param engineConfig    the engine config
     * @return Returns an Engine Instance
     * @throws IllegalArgumentException  .
     * @throws InstantiationException    .
     * @throws IllegalAccessException    .
     * @throws InvocationTargetException .
     * @throws ClassNotFoundException    .
     */
    private KKEngIf getKKEngByName(String engineClassName, EngineConfig engineConfig)
            throws IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException, ClassNotFoundException {
        Class<?> engineClass = Class.forName(engineClassName);
        KKEngIf kkeng = null;
        Constructor<?>[] constructors = engineClass.getConstructors();
        Constructor<?> engConstructor = null;
        if (constructors != null && constructors.length > 0) {
            for (Constructor<?> constructor : constructors) {
                Class<?>[] parmTypes = constructor.getParameterTypes();
                if (parmTypes != null && parmTypes.length == 1) {
                    String parmName = parmTypes[0].getName();
                    if (parmName != null && parmName.equals("com.konakart.appif.EngineConfigIf")) {
                        engConstructor = constructor;
                    }
                }
            }
        }

        if (engConstructor != null) {
            kkeng = (KKEngIf) engConstructor.newInstance(engineConfig);
        }

        return kkeng;
    }

    /**
     * Check if the cookie are installed.
     */
    private void checkKKCookieInstalled() {
        try {
            getEngine().getCookie("aaaa", "bbbb");
        } catch (KKException localKKException) {
            this.kkCookieEnabled = false;

            if (log.isDebugEnabled()) {
                this.log.debug("KK Cookie functionality is not installed");
            }

            return;
        }

        if (log.isDebugEnabled()) {
            this.log.debug("KK Cookie functionality is installed");
        }
        this.kkCookieEnabled = true;
    }

}
