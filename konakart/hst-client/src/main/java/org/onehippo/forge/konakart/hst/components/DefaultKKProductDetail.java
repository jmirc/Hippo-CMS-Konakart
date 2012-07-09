package org.onehippo.forge.konakart.hst.components;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.al.ProdOptionContainer;
import com.konakart.al.ReviewMgr;
import com.konakart.app.KKException;
import com.konakart.appif.CustomerIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.content.beans.manager.workflow.WorkflowCallbackHandler;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.exceptions.QueryException;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.content.beans.standard.HippoBeanIterator;
import org.hippoecm.hst.content.beans.standard.HippoFolder;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.util.ContentBeanUtils;
import org.hippoecm.repository.reviewedactions.FullReviewedActionsWorkflow;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.beans.KKReviewDocument;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.onehippo.forge.konakart.hst.utils.KKComponentUtils;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This detail product component should be used to retrieve a product
 */
public class DefaultKKProductDetail extends KKHstActionComponent {

    private static final String DATE_PATTERN = "yyyy-MM-dd HH.mm.ss.SSS";

    private static final String NAME = "name";
    private static final String COMMENT = "comment";
    private static final String EMAIL = "email";
    private static final String RATING = "rating";
    private static final String SUCCESS = "success";
    private static final String ERRORS = "errors";

    public static final String REVIEWS = "reviews";
    public static final String ALLOW_COMMENTS = "allowComments";


    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        KKAppEng kkAppEng = getKKAppEng(request);

        KKProductDocument document = KKComponentUtils.getKKProductDocument(this, request);

        request.setAttribute("document", document);

        try {

            // Fetch the product related data from the database
            kkAppEng.getProductMgr().fetchSelectedProduct(document.getProductId());

            //We fetch the data for the selected product
            kkAppEng.getProductMgr().updateProductViewedCount(document.getProductId());
            kkAppEng.getProductMgr().fetchAlsoPurchasedArray();
            kkAppEng.getProductMgr().fetchRelatedProducts();
        } catch (KKException e) {
            log.info("Unable to fetch the data for the selected product {}", e.toString());
        } catch (KKAppException e) {
            log.info("Unable to fetch the selected product {}", e.toString());
        }

        // Retrieve options
        List<ProdOptionContainer> opts = kkAppEng.getProductMgr().getSelectedProductOptions();

        if (opts != null) {
            request.setAttribute("prodOptContainer", opts);
        }

        request.setAttribute(ALLOW_COMMENTS, !isGuestCustomer(request));
        request.setAttribute(REVIEWS, KKServiceHelper.getKKReviewService().getReviewsForProductId(request, document.getProductId()));

        request.setAttribute(ERRORS, request.getParameterValues(ERRORS));
        request.setAttribute(COMMENT, request.getParameter(COMMENT));
        request.setAttribute(NAME, request.getParameter(NAME));
        request.setAttribute(EMAIL, request.getParameter(EMAIL));
        request.setAttribute(SUCCESS, request.getParameter(SUCCESS));
    }

    @Override
    public void doAction(String action, HstRequest request, HstResponse response) {

        super.doAction(action, request, response);

        KKProductDocument product = KKComponentUtils.getKKProductDocument(this, request);

        if (StringUtils.equals(action, KKCheckoutConstants.ACTIONS.REVIEW.name())) {
            processReview(product, request, response);
        }
    }


    /**
     * Process a review
     *
     * @param product  the product to review
     * @param request  the HST request
     * @param response the HST response
     */
    private void processReview(@Nonnull KKProductDocument product, @Nonnull HstRequest request,
                               @Nonnull HstResponse response) {

        CustomerIf currentCustomer = KKServiceHelper.getKKCustomerService().getCurrentCustomer(request);

        String name = KKUtil.getEscapedParameter(request, NAME);
        String email = KKUtil.getEscapedParameter(request, EMAIL);
        String comment = KKUtil.getEscapedParameter(request, COMMENT);

        // If the customer is not a guest override the name and the email
        if (!isGuestCustomer(request)) {
            name = currentCustomer.getFirstName() + " " + currentCustomer.getLastName();
            email = currentCustomer.getEmailAddr();
        }


        Long rating = Long.valueOf(request.getParameter(RATING));

        List<String> errors = new ArrayList<String>();

        if (StringUtils.isEmpty(name)) {
            errors.add("invalid.name-label");
        }
        if (StringUtils.isEmpty(comment)) {
            errors.add("invalid.comment-label");
        }
        if (StringUtils.isEmpty(email) || email.indexOf('@') == -1) {
            errors.add("invalid.email-label");
        }
        if (errors.size() > 0) {
            response.setRenderParameter(ERRORS, errors.toArray(new String[errors.size()]));
            response.setRenderParameter(NAME, name);
            response.setRenderParameter(COMMENT, comment);
            response.setRenderParameter(EMAIL, email);
            return;
        }

        try {
            // Add the review into konakart
            ReviewMgr reviewMgr = KKServiceHelper.getKKEngineService().getKKAppEng(request).getReviewMgr();
            reviewMgr.writeReview(comment, rating.intValue(), currentCustomer.getId());

        } catch (Exception e) {
            log.warn("Failed to create a review for product '" + product.getName() + "'", e);
        }
    }

}
