package org.onehippo.forge.konakart.hst.components;

import com.konakart.app.KKException;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.ComponentConfiguration;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.common.engine.KKEngineIf;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.konakart.hst.utils.KKConstants;
import org.onehippo.forge.konakart.hst.utils.KKCookieMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

/**
 * This class is the based class to interact with Konakart
 */
public abstract class KKHstComponent extends BaseHstComponent {

   /**
     * Default StoreId as defined in the web.xml for the ContextParm *
     */
    public static String defaultStoreIdFromWebXml = null;

    private KKCookieMgr kkCookieMgr = new KKCookieMgr();

    /**
     * The <code>Log</code> instance for this application.
     */
    protected Logger log = LoggerFactory.getLogger(KKHstComponent.class);

    @Override
    public void init(ServletContext servletContext, ComponentConfiguration componentConfig) throws HstComponentException {
        super.init(servletContext, componentConfig);

        defaultStoreIdFromWebXml = servletContext.getInitParameter(KKConstants.DEFAULT_STORE_ID_CONTEXT_PARAMETER);
    }

    /**
     * Sets the variable KKEngine to the KKEngine instance saved in the session. If cannot be found,
     * then it is instantiated and attached.
     *
     * @param request  http request
     * @param response http response
     * @return Returns a KonaKart client engine instance
     * @throws KKException .
     */
    protected KKEngineIf getKKEngine(HstRequest request, HstResponse response)
            throws KKException {

        // Check if the Konakart engine has been created
        HttpSession session = request.getSession();
        KKEngine kkEngine = (KKEngine) session.getAttribute(KKEngineIf.KONAKART_KEY);

        if (kkEngine == null) {

            if (log.isInfoEnabled()) {
                log.info("KKEngine not found on the session");
            }

            try {
                Session jcrSession = request.getRequestContext().getSession();
                kkEngine = new KKEngine(HippoModuleConfig.load(jcrSession).getEngineConfig(), request.getLocale());
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
            }
        }

        return kkEngine;
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
     * @return the product namespace. Most of the time, this value is defined when the project is created
     */
    protected abstract String getProductNamespace();

}
