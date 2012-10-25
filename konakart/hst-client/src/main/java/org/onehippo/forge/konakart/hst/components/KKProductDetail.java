package org.onehippo.forge.konakart.hst.components;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.al.ProdOptionContainer;
import com.konakart.al.ReviewMgr;
import com.konakart.app.KKException;
import com.konakart.appif.CustomerIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.utils.KKActionsConstants;
import org.onehippo.forge.konakart.hst.utils.KKComponentUtils;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.onehippo.forge.konakart.site.service.KKTagsService;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This detail product component should be used to retrieve a product
 */
public class KKProductDetail extends KKHstActionComponent {

    public static final String  PRODUCT = "product";
    private static final String NAME = "name";
    private static final String COMMENT = "comment";
    private static final String EMAIL = "email";
    private static final String RATING = "rating";
    private static final String SUCCESS = "success";

    private static final String ERRORS = "errors";
    public static final String REVIEWS = "reviews";
    public static final String ALLOW_COMMENTS = "allowComments";


    @Override
    public final void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        KKProductDocument document = getKKProductDocument(request);
        document.setProductIf(convertProduct(request, document));
        request.setAttribute(PRODUCT, document);

        doBeforeRender(request, response, document);
    }

    public void doBeforeRender(HstRequest request, HstResponse response, KKProductDocument document) throws HstComponentException {

        KKAppEng kkAppEng = getKKAppEng(request);

        try {

            // Fetch the product related data from the database
            kkAppEng.getProductMgr().fetchSelectedProduct(document.getProductId());

            //We fetch the data for the selected product
            kkAppEng.getProductMgr().updateProductViewedCount(document.getProductId());
            kkAppEng.getProductMgr().fetchAlsoPurchasedArray();
            kkAppEng.getProductMgr().fetchRelatedProducts();

            // Set the PRODUCTS_VIEWED customer tag for this customer
            if (isTagProduct()) {
                kkAppEng.getCustomerTagMgr().addToCustomerTag(KKTagsService.TAG_PRODUCTS_VIEWED, document.getProductId());
            }
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


        KKComponentUtils.setCurrentCategories(request);

        request.setAttribute(ALLOW_COMMENTS, !isGuestCustomer(request));
        request.setAttribute(REVIEWS, KKServiceHelper.getKKReviewService().getReviewsForProductId(request, document.getProductId()));

        request.setAttribute(ERRORS, request.getParameterValues(ERRORS));
        request.setAttribute(COMMENT, request.getParameter(COMMENT));
        request.setAttribute(NAME, request.getParameter(NAME));
        request.setAttribute(EMAIL, request.getParameter(EMAIL));
        request.setAttribute(SUCCESS, request.getParameter(SUCCESS));
    }

    /**
     * @return true if the product should be tagged or not
     */
    protected boolean isTagProduct() {
        return true;
    }

    @Override
    public void doAction(String action, HstRequest request, HstResponse response) {

        super.doAction(action, request, response);

        KKProductDocument product = getKKProductDocument(request);

        if (StringUtils.equals(action, KKActionsConstants.ACTIONS.REVIEW.name())) {
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
    protected void processReview(@Nonnull KKProductDocument product, @Nonnull HstRequest request,
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
