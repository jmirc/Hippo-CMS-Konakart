package org.onehippo.forge.konakart.hst.components;

import com.konakart.al.KKAppEng;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
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
import org.hippoecm.hst.util.HstResponseUtils;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.utils.KKComponentUtils;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class is the based class to interact with Konakart
 */
public class KKBaseHstComponent extends BaseHstComponent {

    /**
     * The <code>Log</code> instance for this application.
     */
    protected Logger log = LoggerFactory.getLogger(KKBaseHstComponent.class);

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        KKComponentUtils.setGlobalKonakartAttributes(request);
    }

    /**
     * Retrieve the Konakart client from the HstRequest.
     * The client has been set by the Konakart Valve.
     *
     * @param request the hst request
     * @return the Konakart client.
     */
    @Nonnull
    public KKAppEng getKKAppEng(@Nonnull HstRequest request) {
        KKAppEng kkAppEng = (KKAppEng) request.getAttribute(KKAppEng.KONAKART_KEY);

        return checkNotNull(kkAppEng);
    }


    /**
     * Retrieve the current StoreConfig from the HstRequest
     * The config has been set by the Konakart Valve
     *
     * @param request the hst request
     * @return the Konakart store config.
     */
    @Nonnull
    public KKStoreConfig getKKStoreConfig(@Nonnull HstRequest request) {
        KKStoreConfig kkStoreConfig = (KKStoreConfig) request.getAttribute(KKStoreConfig.KK_STORE_CONFIG);

        return checkNotNull(kkStoreConfig);
    }

    /**
     * Check if the current customer is a guest or a registered customer
     *
     * @param request the hst request
     * @return true if the customer is a guest, false otherwise.
     */
    public boolean isGuestCustomer(@Nonnull HstRequest request) {
        return KKServiceHelper.getKKCustomerService().isGuestCustomer(request);
    }

    /**
     * @return the siteMapItemRefId associated with the detail cart.
     */
    public String getDetailCartRefId() {
        return "detailCartId";
    }

    /**
     * Redirect the user to the 404 page
     *
     * @param response the Hst response
     */
    public void redirectToNotFoundPage(HstResponse response) {
        try {
            response.forward("/404");
        } catch (IOException e) {
            throw new HstComponentException(e);
        }
    }

    /**
     * This is an helper class to redirect the customer to another page
     *
     * @param request  the HstRequest
     * @param response the HstResponse
     * @param refId    the refId
     */
    public void redirectByRefId(HstRequest request, HstResponse response, String refId) {

        HstLinkCreator linkCreator = request.getRequestContext().getHstLinkCreator();

        HstLink link = linkCreator.createByRefId(refId, request.getRequestContext().getResolvedMount().getMount());

        HstResponseUtils.sendRedirectOrForward(request, response, link.getPath());
    }

    /**
     * Find and retrieve the associated KKProductDoucment from a product id.
     *
     * @param request   the Hst Request
     * @param productId id of the Konakart product to find
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

}
