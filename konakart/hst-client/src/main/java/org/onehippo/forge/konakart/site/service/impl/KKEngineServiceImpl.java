package org.onehippo.forge.konakart.site.service.impl;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.app.FetchProductOptions;
import com.konakart.app.KKException;
import com.konakart.appif.CustomerTagIf;
import com.konakart.appif.FetchProductOptionsIf;
import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.hst.channel.KonakartSiteInfo;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.onehippo.forge.konakart.site.service.KKEngineService;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class KKEngineServiceImpl implements KKEngineService {

    private static Logger log = LoggerFactory.getLogger(KKEngineServiceImpl.class);


    @Override
    public KKAppEng getKKAppEng(@Nonnull HttpServletRequest servletRequest) {
        // Check if the Konakart engine has been created
        HttpSession session = servletRequest.getSession();

        return (KKAppEng) session.getAttribute(KKAppEng.KONAKART_KEY);
    }

    /**
     * Retrieve the Konakart client or null if it is not found into the session
     *
     * @param request http request
     * @return the KonaKart client engine instance
     */
    @Nullable
    public KKAppEng getKKAppEng(@Nonnull HstRequest request) {
        return (KKAppEng) request.getAttribute(KKAppEng.KONAKART_KEY);
    }

    /**
     * Sets the variable KKEngine to the KKEngine instance saved in the session. If cannot be found,
     * then it is instantiated and attached.
     *
     * @param servletRequest  http servlet request
     * @param servletResponse http servlet response
     * @return Returns a KonaKart client engine instance
     * @throws HstComponentException thrown if the konakart client could not be created.
     */
    public KKAppEng initKKEngine(@Nonnull HttpServletRequest servletRequest, @Nonnull HttpServletResponse servletResponse) throws HstComponentException {

        HstRequest request = (HstRequest) servletRequest;
        HstResponse response = (HstResponse) servletResponse;


        KKCookieServiceImpl kkCookieServiceImpl = new KKCookieServiceImpl();

        KKAppEng kkAppEng = getKKAppEng(request);

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
                productOptions.setUseExternalPrice(KKServiceHelper.getUseExternalPrice());
                productOptions.setUseExternalQuantity(KKServiceHelper.getUseExternalQuantity());

                kkAppEng.setFetchProdOptions(productOptions);

                if (log.isInfoEnabled()) {
                    log.info("Set KKAppEng on the session for storeId " + storeId);
                }

                // Store the engine under the session
                request.getSession().setAttribute(KKAppEng.KONAKART_KEY, kkAppEng);

                // Create or retrieve the customer's cookie
                kkCookieServiceImpl.manageCookies(request, response, kkAppEng);

                // Retrieve the config.
                kkAppEng.refreshAllClientConfigs();
            } catch (Exception e) {
                log.error("Failed to create Konakart engine ", e);
                throw new HstComponentException("Failed to create Konakart engine", e);
            }
        }

        return kkAppEng;
    }


    @Override
    public int validKKSession(@Nonnull HttpServletRequest servletRequest, @Nonnull HttpServletResponse servletResponse) throws HstComponentException {
        HstRequest request = (HstRequest) servletRequest;
        HstResponse response = (HstResponse) servletResponse;

        HttpSession session = servletRequest.getSession();
        KKAppEng kkAppEng = (KKAppEng) session.getAttribute(KKAppEng.KONAKART_KEY);

        if (kkAppEng == null) {
            return -1;
        }

        KKCookieServiceImpl kkCookieServiceImpl = new KKCookieServiceImpl();

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
                CustomerTagIf prodsViewedTagCust = kkAppEng.getCustomerTagMgr().getCustomerTag(KKCheckoutConstants.TAG_PRODUCTS_VIEWED);

                kkAppEng.getCustomerMgr().logout();

                // Ensure that the guest customer is the one in the cookie
                kkCookieServiceImpl.manageCookieLogout(request, response, kkAppEng);

                // Set recently viewed products for the guest customer if changed while logged in
                CustomerTagIf prodsViewedTagGuest = kkAppEng.getCustomerTagMgr().getCustomerTag(KKCheckoutConstants.TAG_PRODUCTS_VIEWED);

                updateRecentlyViewedProducts(kkAppEng, prodsViewedTagCust, prodsViewedTagGuest);
            }
        } catch (Exception e) {
            log.warn("Unable to check the Konakart session - {} ", e.toString());
        }

        return -1;
    }

    @Override
    public boolean loggedIn(HstRequest request, HstResponse response, String username, String password) {

        KKAppEng kkAppEng = getKKAppEng(request);

        if (kkAppEng == null) {
            return false;
        }

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
            CustomerTagIf prodsViewedTagGuest = kkAppEng.getCustomerTagMgr().getCustomerTag(KKCheckoutConstants.TAG_PRODUCTS_VIEWED);

            // Login
            String result = kkAppEng.getCustomerMgr().login(username, password);

            if (result == null) {
                return false;
            }

            // Update the custom1 of each product under the basket to set the full path of the product's node
            // TODO check how to handle that - perhaps we need to create a custom taglin
            //updateBaskets(request);

            /*
            * Manage Cookies
            */
            KKServiceHelper.getKKCookieService().manageCookiesLogin(request, response, kkAppEng);

            // Insert event
            KKServiceHelper.getKKEventService().insertCustomerEvent(request, KKEventServiceImpl.ACTION_CUSTOMER_LOGIN);

            // Set recently viewed products for the logged in customer if changed as guest
            CustomerTagIf prodsViewedTagCust = kkAppEng.getCustomerTagMgr().getCustomerTag(KKCheckoutConstants.TAG_PRODUCTS_VIEWED);
            updateRecentlyViewedProducts(kkAppEng, prodsViewedTagGuest, prodsViewedTagCust);

            return true;

        } catch (Exception e) {
            log.warn("Unable to logged-in", e);
        }

        return false;

    }


    /**
     * The store Id. By default this method return "store1" value or the value associated to the defaultStoreId's
     * context-param defined within the web.xml
     * <p/>
     * You can override this method to retrieve the store id from the channel info for example.
     *
     * @param request the hst request
     * @return the store id associated to this channel.
     * @see org.hippoecm.hst.configuration.channel.ChannelInfo
     */
    @Nonnull
    private String getStoreIdFromChannel(@Nonnull HstRequest request) {
        KonakartSiteInfo siteInfo = getKonakartSiteInfo(request);

        if (siteInfo == null) {
            return KKCheckoutConstants.DEF_STORE_ID;

        }

        return siteInfo.getStoreId();
    }

    /**
     * The catalog Id (@see catalog feature. Available with the Konakart enterprise version.
     * By default this method return null.
     *
     * @param request the hst request
     * @return the catalog id associated with this channel.
     */
    @Nullable
    private String getCatalogIdFromChannel(@Nonnull HstRequest request) {
        KonakartSiteInfo siteInfo = getKonakartSiteInfo(request);

        if (siteInfo == null) {
            return null;
        }

        return siteInfo.getCatalogId();
    }

    /**
     * @param request the hst request
     * @return the Channel Info
     */
    @Nullable
    private KonakartSiteInfo getKonakartSiteInfo(@Nonnull HstRequest request) {
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
    private void updateRecentlyViewedProducts(@Nonnull KKAppEng kkAppEng, @Nullable CustomerTagIf oldTag,
                                              @Nullable CustomerTagIf newTag) throws KKException, KKAppException {
        if (oldTag != null && oldTag.getDateAdded() != null && oldTag.getValue() != null
                && oldTag.getValue().length() > 0) {
            if (newTag == null || newTag.getDateAdded() == null
                    || newTag.getDateAdded().before(oldTag.getDateAdded())) {
                /*
                 * If new tag doesn't exist or old tag is newer than new tag, then give newTag the
                 * value of old tag
                 */
                kkAppEng.getCustomerTagMgr().insertCustomerTag(KKCheckoutConstants.TAG_PRODUCTS_VIEWED, oldTag.getValue());
            }
        }
    }

}
