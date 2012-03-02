package org.onehippo.forge.konakart.hst.components;

import com.konakart.app.KKException;
import com.konakart.appif.BasketIf;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.CustomerTagIf;
import com.konakart.appif.ProductIf;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.ObjectBeanManagerException;
import org.hippoecm.hst.content.beans.manager.ObjectBeanManager;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.core.request.ComponentConfiguration;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.common.engine.KKEngineIf;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.utils.KKConstants;
import org.onehippo.forge.konakart.hst.utils.KKCookieMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.ServletContext;
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
     * Default StoreId as defined in the web.xml for the ContextParm *
     */
    public static String defaultStoreIdFromWebXml = null;

    /**
     * Used to manage cookie
     */
    private KKCookieMgr kkCookieMgr = new KKCookieMgr();

    /**
     * The Konakart engine
     */
    protected KKEngineIf kkEngine;


    @Override
    public void init(ServletContext servletContext, ComponentConfiguration componentConfig) throws HstComponentException {
        super.init(servletContext, componentConfig);

        defaultStoreIdFromWebXml = servletContext.getInitParameter(KKConstants.DEFAULT_STORE_ID_CONTEXT_PARAMETER);
    }

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        // Retrieve the engine
        kkEngine = getKKEngine(request, response);

        // Logged-in
        validKKSession(request, response);

        // Set the attribut isLogged if the user is a logged user
        request.setAttribute("isLogged", !isGuestCustomer());

        // Set the current customer
        if (!isGuestCustomer()) {
            request.setAttribute("currentCustomer", getCurrentCustomer());
            request.setAttribute("basketTotal", kkEngine.getBasketMgr().getBasketTotal());
        }
    }

    /**
     * Check if the current customer is a guest or a registered customer
     *
     * @return true if the customer is a guest, false otherwise.
     */
    public boolean isGuestCustomer() {
        return kkEngine.getCustomerMgr().isGuestCustomer();
    }

    /**
     * Get the current customer.
     *
     * @return the current customer
     */
    public CustomerIf getCurrentCustomer() {
        return kkEngine.getCustomerMgr().getCurrentCustomer();
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
    public KKProductDocument getProductDocument(HstRequest request, HstResponse response) throws HstComponentException {

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
        document.setKkEngine(kkEngine);

        return document;
    }

    /**
     * Find and retrieve the associated KKProductDoucment with the product id.
     *
     * @param beanManager object content manager
     * @param productId id of the Konakart product to find
     *
     * @return the Hippo Bean
     */
    public KKProductDocument getProductDocumentById(ObjectBeanManager beanManager, int productId) {

        try {
            ProductIf productIf = kkEngine.getProductMgr().getProductById(productId, kkEngine.getLanguage().getId());

            return (KKProductDocument) beanManager.getObjectByUuid(productIf.getCustom1());
        } catch (KKException e) {
            log.warn("Failed to retrieve the Konakart product with the id - " + productId);
        } catch (ObjectBeanManagerException e) {
            log.warn("Failed to retrieve the Konakart product with the id - " + productId);
        }

        return null;
    }

    /**
     * The store Id. By default this method return "store1" value or the value associated to the defaultStoreId's
     * context-param defined within the web.xml
     * <p/>
     * You can override this method to retrieve the store id from the channel info for example.
     *
     * @return the store id associated to this channel.
     * @see org.hippoecm.hst.configuration.channel.ChannelInfo
     */
    protected String getStoreIdFromChannel() {
        if (defaultStoreIdFromWebXml == null) {
            return KKConstants.DEF_STORE_ID;
        }

        return defaultStoreIdFromWebXml;
    }

    /**
     * The catalog Id (@see catalog feature. Available with the Konakart enterprise version.
     * By default this method return null.
     *
     * @return the catalog id associated with this channel.
     */
    protected String getCatalogIdFromChannel() {
        return null;
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
                kkEngine.getCustomerMgr().refreshCustomerCachedData();

                return true;
            }

            // Get recently viewed products before logging in
            CustomerTagIf prodsViewedTagGuest = kkEngine.getCustomerTagMgr().getCustomerTag(TAG_PRODUCTS_VIEWED);

            // Login
            kkEngine.getCustomerMgr().login(username, password);

            // Update the custom1 of each product under the basket to set the full path of the product's node
            updateBaskets(request);

            /*
            * Manage Cookies
            */
            kkCookieMgr.manageCookiesLogin(request, response, kkEngine);

            // Set recently viewed products for the logged in customer if changed as guest
            CustomerTagIf prodsViewedTagCust = kkEngine.getCustomerTagMgr().getCustomerTag(TAG_PRODUCTS_VIEWED);
            updateRecentlyViewedProducts(prodsViewedTagGuest, prodsViewedTagCust);


        } catch (Exception e) {
            log.warn("Unable to logged-in", e);
        }

        return false;

    }

    /**
     * Set for each products into the cart, the path of the associated Hippo Bean
     * @param request the hst request
     */
    private void updateBaskets(HstRequest request) {
        
        ObjectBeanManager beanManager = getObjectBeanManager(request);

        // getting hold of the link creator
        HstLinkCreator linkCreator = request.getRequestContext().getHstLinkCreator();

        // Retrieve the list of products into the basket
        BasketIf[] basketIfs = getCurrentCustomer().getBasketItems();

        for (BasketIf basketIf : basketIfs) {

            String hippoUUID = basketIf.getProduct().getCustom1();

            try {
                HippoBean hippoBean = (HippoBean) beanManager.getObjectByUuid(hippoUUID);

                // create HstLink
                HstLink link = linkCreator.create(hippoBean, request.getRequestContext());
                // create the url String
                String url = link.toUrlForm(request.getRequestContext(), false);

                // Set the path of a product
                basketIf.setCustom1(url);
            } catch (ObjectBeanManagerException e) {
                log.warn("Unable to retrieve the product with UUID - " + hippoUUID);
            }
        }


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
            if (kkEngine.getSessionId() == null) {
                return -1;
            }

            // If the user can't be logged-in, an exception if thrown
            try {
                // At this point we return a valid customer id
                return kkEngine.getEngine().checkSession(kkEngine.getSessionId());
            } catch (KKException e) {

                //Get recently viewed products before logging out
                CustomerTagIf prodsViewedTagCust = kkEngine.getCustomerTagMgr().getCustomerTag(TAG_PRODUCTS_VIEWED);

                kkEngine.getCustomerMgr().logout();

                // Ensure that the guest customer is the one in the cookie
                kkCookieMgr.manageCookieLogout(request, response, kkEngine);

                // Set recently viewed products for the guest customer if changed while logged in
                CustomerTagIf prodsViewedTagGuest = kkEngine.getCustomerTagMgr().getCustomerTag(TAG_PRODUCTS_VIEWED);

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
    private KKEngineIf getKKEngine(HstRequest request, HstResponse response) throws HstComponentException {

        // Check if the Konakart engine has been created
        HttpSession session = request.getSession();
        KKEngine kkEngine = (KKEngine) session.getAttribute(KKEngineIf.KONAKART_KEY);

        if (kkEngine == null) {

            if (log.isInfoEnabled()) {
                log.info("KKEngine not found on the session");
            }

            try {
                Session jcrSession = request.getRequestContext().getSession();
                kkEngine = new KKEngine(HippoModuleConfig.load(jcrSession).getEngineConfig(), request.getRequestContext().getPreferredLocale());
                kkEngine.setStoreId(getStoreIdFromChannel());
                kkEngine.setCatalogId(getCatalogIdFromChannel());


                if (log.isInfoEnabled()) {
                    log.info("Set KKAppEng on the session for storeId " + getStoreIdFromChannel());
                }

                // Store the engine under the session
                session.setAttribute(KKEngine.KONAKART_KEY, kkEngine);

                // Create or retrieve the customer's cookie
                kkCookieMgr.manageCookies(request, response, kkEngine);
            } catch (Exception e) {
                log.error("Failed to create Konakart engine {} ", e.toString());
                throw new HstComponentException("Failed to create Konakart engine", e);
            }
        }

        return kkEngine;
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
     * @throws KKException .
     */
    private void updateRecentlyViewedProducts(CustomerTagIf oldTag, CustomerTagIf newTag) throws KKException {
        if (oldTag != null && oldTag.getDateAdded() != null && oldTag.getValue() != null
                && oldTag.getValue().length() > 0) {
            if (newTag == null || newTag.getDateAdded() == null
                    || newTag.getDateAdded().before(oldTag.getDateAdded())) {
                /*
                 * If new tag doesn't exist or old tag is newer than new tag, then give newTag the
                 * value of old tag
                 */
                kkEngine.getCustomerTagMgr().insertCustomerTag(TAG_PRODUCTS_VIEWED, oldTag.getValue());
            }
        }
    }


}
