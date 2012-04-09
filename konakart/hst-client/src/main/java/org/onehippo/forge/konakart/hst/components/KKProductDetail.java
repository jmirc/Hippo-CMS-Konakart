package org.onehippo.forge.konakart.hst.components;

import com.konakart.al.KKAppException;
import com.konakart.al.ProdOptionContainer;
import com.konakart.al.ReviewMgr;
import com.konakart.app.KKException;
import com.konakart.appif.CustomerIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.content.beans.manager.workflow.WorkflowPersistenceManager;
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
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.beans.KKReviewDocument;
import org.onehippo.forge.konakart.hst.utils.KKConstants;
import org.onehippo.forge.konakart.hst.utils.KKUtil;

import javax.jcr.Session;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This detail product component should be used to retrieve a product
 */
public abstract class KKProductDetail extends KKHstActionComponent {

    private static final String DATE_PATTERN = "yyyy-MM-dd HH.mm.ss.SSS";

    private static final String NAME = "name";
    private static final String COMMENT = "comment";
    private static final String EMAIL = "email";
    private static final String RATING = "rating";
    private static final String SUCCESS = "success";
    private static final String ERRORS = "errors";


    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);


        KKProductDocument document = getProductDocument(request, response);

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


        // Set the folder where the reviews will be saved
        String reviewsFolderName = KKCndConstants.DEFAULT_REVIEWS_FOLDER;

        if (!StringUtils.isEmpty(document.getReviewsFolder())) {
            reviewsFolderName = document.getReviewsFolder();
        }

        HippoBean siteContentBase = getSiteContentBaseBean(request);
        HippoFolder reviewsFolder = siteContentBase.getBean(reviewsFolderName);
        if (reviewsFolder == null) {
            log.warn("Product reviews folder not found: '{}/{}'. No product reviews will be shown.", siteContentBase.getPath(), reviewsFolderName);
        } else {
            try {
                List<KKReviewDocument> reviews = new ArrayList<KKReviewDocument>();

                final HstQuery incomingBeansQuery = ContentBeanUtils.createIncomingBeansQuery(document, reviewsFolder,
                        4, getObjectConverter(), KKReviewDocument.class, false);
                final HstQueryResult result = incomingBeansQuery.execute();
                final HippoBeanIterator beanIterator = result.getHippoBeans();
                int count = 0;
                while (beanIterator.hasNext()) {
                    KKReviewDocument review = (KKReviewDocument) beanIterator.nextHippoBean();
                    reviews.add(review);
                    count++;
                }
                request.setAttribute("reviews", reviews);
                request.setAttribute("votes", count);
            } catch (QueryException e) {
                log.error("Unable to execute query to get the reviews :" + e.getMessage(), e);
            }
        }

        request.setAttribute("allowComments", !isGuestCustomer());

    }

    @Override
    public void doAction(String action, HstRequest request, HstResponse response) {

        super.doAction(action, request, response);

        KKProductDocument product = getProductDocument(request, response);

        if (StringUtils.equals(action, KKConstants.ACTIONS.REVIEW.name())) {
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
    private void processReview(KKProductDocument product, HstRequest request, HstResponse response) {

        CustomerIf currentCustomer = getCurrentCustomer();

        String name = KKUtil.getEscapedParameter(request, NAME);
        String email = KKUtil.getEscapedParameter(request, EMAIL);
        String comment = KKUtil.getEscapedParameter(request, COMMENT);

        // If the customer is not a guest override the name and the email
        if (!isGuestCustomer()) {
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

        String productUuid = product.getCanonicalHandleUUID();
        String reviewName = product.getReviewsFolder();

        Session persistableSession = null;
        WorkflowPersistenceManager wpm;

        try {
            persistableSession = getPersistableSession(request);
            wpm = getWorkflowPersistenceManager(persistableSession);

            final String reviewFolderPath = createReviewFolderPath(request, product, reviewName);
            final String reviewNodeName = createReviewNodeName(product);

            final String reviewPath = wpm.createAndReturn(reviewFolderPath, KKCndConstants.REVIEW_DOC_TYPE, reviewNodeName, true);

            KKReviewDocument review = (KKReviewDocument) wpm.getObject(reviewPath);

            // update content properties
            if (review != null) {
                review.setName(name);
                review.setComment(comment);
                review.setRating(rating);
                review.setEmail(email);
                review.setProductUuid(productUuid);
                review.setCustomerId((long) currentCustomer.getId());

                // update now           `
                wpm.update(review);

                // Add the review into konakart
                ReviewMgr reviewMgr = kkAppEng.getReviewMgr();
                reviewMgr.writeReview(comment, rating.intValue(), currentCustomer.getId());

                response.setRenderParameter(SUCCESS, SUCCESS);
            } else {
                log.warn("Failed to add review for product '{}': could not retrieve Review bean for node '{}'.",
                        product.getName(), reviewPath);

                KKUtil.refreshWorkflowManager(wpm);
            }

        } catch (Exception e) {
            log.warn("Failed to create a review for product '" + product.getName() + "'", e);
        } finally {
            if (persistableSession != null) {
                persistableSession.logout();
            }
        }
    }

    private String createReviewFolderPath(HstRequest request, KKProductDocument product, String reviewsFolderName) {
        StringBuilder builder = new StringBuilder();

        builder.append(request.getRequestContext().getResolvedMount().getMount().getCanonicalContentPath());

        builder.append('/');
        builder.append(reviewsFolderName);
        builder.append('/');
        builder.append(product.getName());

        return builder.toString();
    }

    private String createReviewNodeName(KKProductDocument product) {
        StringBuilder builder = new StringBuilder();

        builder.append("Review for ");
        builder.append(product.getName());
        builder.append(' ');

        final Date now = new Date();
        final String timestamp = new SimpleDateFormat(DATE_PATTERN).format(now);
        builder.append(timestamp);

        return builder.toString();
    }

}
