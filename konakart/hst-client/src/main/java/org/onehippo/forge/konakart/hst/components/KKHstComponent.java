package org.onehippo.forge.konakart.hst.components;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.al.WishListMgr;
import com.konakart.app.FetchProductOptions;
import com.konakart.app.KKException;
import com.konakart.appif.*;
import com.konakart.bl.ConfigConstants;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryManager;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.query.filter.Filter;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.channel.KonakartSiteInfo;
import org.onehippo.forge.konakart.hst.utils.KKConstants;
import org.onehippo.forge.konakart.hst.utils.KKCookieMgr;
import org.onehippo.forge.konakart.hst.utils.KKCustomerEventMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * This class is the based class to interact with Konakart
 */
public class KKHstComponent extends BaseHstComponent {

    /**
     * The <code>Log</code> instance for this application.
     */
    protected Logger log = LoggerFactory.getLogger(KKHstComponent.class);


    /*
    * Customer tags
    */
    protected static final String TAG_PRODUCTS_VIEWED = "PRODUCTS_VIEWED";


    /**
     * Used to manage cookie
     */
    private KKCookieMgr kkCookieMgr = new KKCookieMgr();

    /**
     * The Konakart engine
     */
    protected KKAppEng kkAppEng;

    /**
     * Used to insert customer's event
     */
    protected KKCustomerEventMgr eventMgr = new KKCustomerEventMgr();

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        try {
            if (KKAppEng.getEngConf() == null) {
                // Initialize the Engine Conf
                Mount mount = request.getRequestContext().getResolvedMount().getMount();
                KonakartSiteInfo siteInfo = mount.getChannelInfo();

                if (siteInfo != null) {
                    KKEngine.init(Integer.parseInt(siteInfo.getEngineMode()), siteInfo.isCustomersShared(),
                            siteInfo.isProductsShared());
                }
            }

            // Retrieve the engine
            kkAppEng = getKKEngine(request, response);

            // Logged-in
            validKKSession(request, response);

            // Set the attribut isLogged if the user is a logged user
            request.setAttribute("isLogged", !isGuestCustomer());

            // Set the attribut displayPriceWithTax used to display or not the price with or without tax
            request.setAttribute("displayPriceWithTax", kkAppEng.displayPriceWithTax());

            // Set the attibute wishListEnabled. Set to true if the wish list functionality is allowed, false otherwise
            request.setAttribute("wishListEnabled", wishListEnabled());

            // Set the default wish list if exists
            request.setAttribute("defaultWishList", getDefaultWishList());

            // Set the current customer
            request.setAttribute("currentCustomer", getCurrentCustomer());
            request.setAttribute("basketTotal", kkAppEng.getBasketMgr().getBasketTotal());
        } catch (Exception e) {
            log.warn("Failed to render the HST component {}", e.toString());
        }
    }

    /**
     * Check if the current customer is a guest or a registered customer
     *
     * @return true if the customer is a guest, false otherwise.
     */
    protected  boolean isGuestCustomer() {
        return kkAppEng.getCustomerMgr().getCurrentCustomer().getId() < 0;
    }

    /**
     * Check if the wish list is enabled.
     * @return true if the wish list is enabled, false otherwise
     */
    protected boolean wishListEnabled() {
        boolean wishListEnabled = kkAppEng.getConfigAsBoolean(ConfigConstants.ENABLE_WISHLIST, false);
        boolean allowWishListWhenNotLoggedIn = kkAppEng.getConfigAsBoolean(ConfigConstants.ALLOW_WISHLIST_WHEN_NOT_LOGGED_IN, false);

        return wishListEnabled && (!isGuestCustomer() || (isGuestCustomer() && allowWishListWhenNotLoggedIn));
    }

    /**
     * Get the current customer.
     *
     * @return the current customer
     */
    protected CustomerIf getCurrentCustomer() {
        return kkAppEng.getCustomerMgr().getCurrentCustomer();
    }

    /**
     * Get the current Konakart Product Document
     *
     * @param request  the HST request
     * @param response the HST response
     * @return the product document
     * @throws org.hippoecm.hst.core.component.HstComponentException
     *          thrown if the document is not a type of KKProductDocument
     */
    protected KKProductDocument getProductDocument(HstRequest request, HstResponse response) throws HstComponentException {

        HippoBean currentBean = this.getContentBean(request);

        if (currentBean == null) {
            redirectToNotFoundPage(response);
            throw new HstComponentException("No document has been found");
        }

        // Not an instance of KKProductdocuemnt
        if (!(currentBean instanceof KKProductDocument)) {
            log.error(currentBean.getClass().getName() + " must extend " + KKProductDocument.class.getName());
            redirectToNotFoundPage(response);
            throw new HstComponentException(currentBean.getClass().getName() + " must extend " + KKProductDocument.class.getName());
        }

        KKProductDocument document = (KKProductDocument) currentBean;
        document.setKkEngine(kkAppEng);

        return document;
    }

    /**
     * Find and retrieve the associated KKProductDoucment from a product id.
     *
     * @param request the Hst Request
     * @param productId   id of the Konakart product to find
     * @return the Hippo Bean
     */
    protected KKProductDocument getProductDocumentById(HstRequest request, int productId) {

        HippoBean scope = super.getSiteContentBaseBean(request);

        HstQueryManager queryManager = getQueryManager(request);

        try {
            HstQuery hstQuery = queryManager.createQuery(scope, "myhippoproject:productdocument");
            Filter filter = hstQuery.createFilter();
            filter.addEqualTo("myhippoproject:konakart/konakart:id", (long) productId);

            hstQuery.setFilter(filter);

            HstQueryResult queryResult = hstQuery.execute();

            // No result
            if (queryResult.getTotalSize() == 0) {
                return null;
            }

            return (KKProductDocument) queryResult.getHippoBeans().nextHippoBean();

        } catch (QueryException e) {
            log.error("Failed to find the Hippo product document for the productId {} - {}", productId, e.toString());
        }

        return null;
    }

    /**
     * Generate the HstLink for the product defined by his id
     *
     * @param request Hst Request
     * @param productId id of the product
     * @return the generated HstLink or empty if the product id doesn't not exist.
     */
    protected String generateHstLink(HstRequest request, int productId) {
        // getting hold of the link creator
        HstLinkCreator linkCreator = request.getRequestContext().getHstLinkCreator();

        // The associated Hippo Bean
        KKProductDocument document = getProductDocumentById(request, productId);

        // create HstLink
        HstLink link = linkCreator.create(document, request.getRequestContext());

        // create the url String
        return link.toUrlForm(request.getRequestContext(), false);
    }


    /**
     * The store Id. By default this method return "store1" value or the value associated to the defaultStoreId's
     * context-param defined within the web.xml
     * <p/>
     * You can override this method to retrieve the store id from the channel info for example.
     *
     * @param request the hst request
     *
     * @return the store id associated to this channel.
     * @see org.hippoecm.hst.configuration.channel.ChannelInfo
     */
    protected String getStoreIdFromChannel(HstRequest request) {
        KonakartSiteInfo siteInfo = getKonakartSiteInfo(request);

        if (siteInfo == null) {
            return KKConstants.DEF_STORE_ID;

        }

        return siteInfo.getStoreId();
    }

    /**
     * The catalog Id (@see catalog feature. Available with the Konakart enterprise version.
     * By default this method return null.
     *
     * @param request the hst request
     *
     * @return the catalog id associated with this channel.
     */
    protected String getCatalogIdFromChannel(HstRequest request) {
        KonakartSiteInfo siteInfo = getKonakartSiteInfo(request);

        if (siteInfo == null) {
            return null;

        }

        return siteInfo.getCatalogId();
    }

    /**
     * Set if konakart needs to use external price
     * (@see catalog feature. Available with the Konakart enterprise version.
     * By default this method return false.
     *
     * @return true if konakart needs to use external price, false otherwise
     */
    protected boolean isUseExternalPrice() {
        return false;
    }

    /**
     * Set if konakart needs to use external quantity
     * (@see catalog feature. Available with the Konakart enterprise version.
     * By default this method return false.
     *
     * @return true if konakart needs to use external quantity, false otherwise
     */
    protected boolean isUseExternalQuantity() {
        return false;
    }

    /**
     * @return the siteMapItemRefId associated with the detail cart.
     */
    protected String getDetailCartRefId() {
        return "detailCartId";
    }

    /**
     * Redirect the user to the 404 page
     *
     * @param response the Hst response
     */
    protected void redirectToNotFoundPage(HstResponse response) {
        try {
            response.forward("/404");
        } catch (IOException e) {
            throw new HstComponentException(e);
        }
    }

    /**
     * Log a user to Konakart
     *
     * @param request  the Hst request
     * @param response the Hst response
     * @param username the username
     * @param password the password
     * @return true if the user is logged-in, false otherwise
     */
    protected boolean loggedIn(HstRequest request, HstResponse response, String username, String password) {

        try {
            int custId = validKKSession(request, response);

            // Check if the user is already logged in
            if (custId >= 0) {
                if (log.isDebugEnabled()) {
                    log.debug("User already logged in");
                }

                // Refresh the data relevant to the customer such as his basked and recent orders.
                kkAppEng.getCustomerMgr().refreshCustomerCachedData();

                return true;
            }

            // Get recently viewed products before logging in
            CustomerTagIf prodsViewedTagGuest = kkAppEng.getCustomerTagMgr().getCustomerTag(TAG_PRODUCTS_VIEWED);

            // Login
            String result = kkAppEng.getCustomerMgr().login(username, password);

            if (result == null) {
                return false;
            }

            // Update the custom1 of each product under the basket to set the full path of the product's node
            updateBaskets(request);

            /*
            * Manage Cookies
            */
            kkCookieMgr.manageCookiesLogin(request, response, kkAppEng);

            // Insert event
            eventMgr.insertCustomerEvent(kkAppEng, KKCustomerEventMgr.ACTION_CUSTOMER_LOGIN);

            // Set recently viewed products for the logged in customer if changed as guest
            CustomerTagIf prodsViewedTagCust = kkAppEng.getCustomerTagMgr().getCustomerTag(TAG_PRODUCTS_VIEWED);
            updateRecentlyViewedProducts(prodsViewedTagGuest, prodsViewedTagCust);

            return true;

        } catch (Exception e) {
            log.warn("Unable to logged-in", e);
        }

        return false;

    }

    /**
     * Set for each products into the cart, the path of the associated Hippo Bean
     *
     * @param request the hst request
     */
    private void updateBaskets(HstRequest request) {
        // Retrieve the list of products into the basket
        BasketIf[] basketIfs = getCurrentCustomer().getBasketItems();

        for (BasketIf basketIf : basketIfs) {
            // Set the path of a product
            basketIf.setCustom1(generateHstLink(request, basketIf.getProduct().getId()));
        }
    }

    /**
     * @return the wish list that has the type equals to 0
     */
    protected WishListIf getDefaultWishList() {
        if (!wishListEnabled()) {
            return null;
        }

        WishListIf[] wishLists = kkAppEng.getCustomerMgr().getCurrentCustomer().getWishLists();

        if (wishLists == null) {
            return null;
        }

        for (WishListIf wishList : wishLists) {
            if (wishList.getListType() == WishListMgr.WISH_LIST_TYPE) {
                return wishList;
            }
        }

        return null;
    }


    /**
     * Checks to see whether we are logged in.
     *
     * @param request  the Hst request
     * @param response the Hst response
     * @return Returns the CustomerId if logged in. Otherwise a negative number.
     * @throws HstComponentException .
     */
    protected int validKKSession(HstRequest request, HstResponse response) throws HstComponentException {

        try {
            // If the session is null, set the forward and return a negative number
            if (kkAppEng.getSessionId() == null) {
                return -1;
            }

            // If the user can't be logged-in, an exception if thrown
            try {
                // At this point we return a valid customer id
                return kkAppEng.getEng().checkSession(kkAppEng.getSessionId());
            } catch (KKException e) {

                //Get recently viewed products before logging out
                CustomerTagIf prodsViewedTagCust = kkAppEng.getCustomerTagMgr().getCustomerTag(TAG_PRODUCTS_VIEWED);

                kkAppEng.getCustomerMgr().logout();

                // Ensure that the guest customer is the one in the cookie
                kkCookieMgr.manageCookieLogout(request, response, kkAppEng);

                // Set recently viewed products for the guest customer if changed while logged in
                CustomerTagIf prodsViewedTagGuest = kkAppEng.getCustomerTagMgr().getCustomerTag(TAG_PRODUCTS_VIEWED);

                updateRecentlyViewedProducts(prodsViewedTagCust, prodsViewedTagGuest);
            }
        } catch (Exception e) {
            log.warn("Unable to check the Konakart session - {} ", e.toString());
        }

        return -1;
    }


    /**
     * Sets the variable KKEngine to the KKEngine instance saved in the session. If cannot be found,
     * then it is instantiated and attached.
     *
     * @param request  http request
     * @param response http response
     * @return Returns a KonaKart client engine instance
     * @throws HstComponentException .
     */
    private KKAppEng getKKEngine(HstRequest request, HstResponse response) throws HstComponentException {

        // Check if the Konakart engine has been created
        HttpSession session = request.getSession();
        KKAppEng kkAppEng = (KKAppEng) session.getAttribute(KKAppEng.KONAKART_KEY);

        if (kkAppEng == null) {

            if (log.isInfoEnabled()) {
                log.info("KKEngine not found on the session");
            }

            try {
                String storeId = getStoreIdFromChannel(request);

                // Create the Konakart engine
                kkAppEng = KKEngine.get(storeId);

                // initialize the Fetch production options
                FetchProductOptionsIf productOptions = new FetchProductOptions();
                productOptions.setCatalogId(getCatalogIdFromChannel(request));
                productOptions.setUseExternalPrice(isUseExternalPrice());
                productOptions.setUseExternalQuantity(isUseExternalQuantity());

                kkAppEng.setFetchProdOptions(productOptions);

                if (log.isInfoEnabled()) {
                    log.info("Set KKAppEng on the session for storeId " + storeId);
                }

                // Store the engine under the session
                session.setAttribute(KKAppEng.KONAKART_KEY, kkAppEng);

                // Create or retrieve the customer's cookie
                kkCookieMgr.manageCookies(request, response, kkAppEng);

                // Retrieve the config.
                kkAppEng.refreshAllClientConfigs();
            } catch (Exception e) {
                log.error("Failed to create Konakart engine ", e);
                throw new HstComponentException("Failed to create Konakart engine", e);
            }
        }

        return kkAppEng;
    }

    /**
     * Method called when a customer logs in or logs out. When logging in we need to decide whether
     * to update the customer's PRODUCTS_VIEWED tag value from the value of the guest customer's
     * tag. When logging out we need to make the same decision in the opposite direction. We only do
     * the updates if the tag value of the "oldTag" is more recent than the tag value of the
     * "newTag".
     *
     * @param oldTag When logging in, it is the tag of the guest customer. When logging out, it is the
     *               tag of the logged in customer.
     * @param newTag When logging in, it is the tag of the logged in customer. When logging out, it is
     *               the tag of the guest customer.
     * @throws com.konakart.al.KKAppException .
     * @throws com.konakart.app.KKException   .
     */
    private void updateRecentlyViewedProducts(CustomerTagIf oldTag, CustomerTagIf newTag) throws KKException, KKAppException {
        if (oldTag != null && oldTag.getDateAdded() != null && oldTag.getValue() != null
                && oldTag.getValue().length() > 0) {
            if (newTag == null || newTag.getDateAdded() == null
                    || newTag.getDateAdded().before(oldTag.getDateAdded())) {
                /*
                 * If new tag doesn't exist or old tag is newer than new tag, then give newTag the
                 * value of old tag
                 */
                kkAppEng.getCustomerTagMgr().insertCustomerTag(TAG_PRODUCTS_VIEWED, oldTag.getValue());
            }
        }
    }

    /**
     * @param request the hst request
     *
     * @return the Channel Info
     */
    private KonakartSiteInfo getKonakartSiteInfo(HstRequest request) {
        Mount mount = request.getRequestContext().getResolvedMount().getMount();

        Object o = mount.getChannelInfo();
        
        if (!(o instanceof KonakartSiteInfo)) {
            log.error("//////////////////////////////////////");
            log.error("The ChannelInfo must extends org.onehippo.forge.konakart.hst.channel.KonakartSiteInfo");
            log.error("//////////////////////////////////////");
            
            return null;
        }
        
        return (KonakartSiteInfo) o;
    }


}
