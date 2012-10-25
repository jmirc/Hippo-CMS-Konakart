package org.onehippo.forge.konakart.site.service;

import com.konakart.app.KKException;
import com.konakart.appif.ReviewIf;
import org.hippoecm.hst.core.component.HstRequest;

import javax.annotation.Nonnull;

public interface KKReviewService {

    /**
     * Get a list of reviews written for a product
     *
     * Invisible review will not be displayed.
     *
     *
     * @param request the hst request
     * @param productId the product id
     * @return the list of reviews or empty if no review has been written.
     */
    ReviewIf[] getReviewsForProductId(@Nonnull HstRequest request, int productId);

    /**
     * Get a list of reviews written for a product
     *
     *
     * @param request the hst request
     * @param productId the product id
     * @param showInvisible true invisible reviews will be displayed, false otherwise
     * @return the list of reviews or empty if no review has been written.
     */
    ReviewIf[] getReviewsForProductId(@Nonnull HstRequest request, int productId, boolean showInvisible);

    /**
     * Write a review for a product
     *
     * @param request the hst request
     * @param productId id of the product
     * @param reviewText the text to put into the review
     * @param rating     the rating for the review
     * @param customerId id of the customer writing the review
     * @param isVisible true if the review is visible, false otherwise
     * @return the review id
     */
    int writeReview(HstRequest request, int productId, String reviewText, int rating, int customerId, boolean isVisible) throws Exception;

    /**
     * Write a review for a product
     *
     * @param request the hst request
     * @param productId id of the product
     * @param reviewText the text to put into the review
     * @param rating     the rating for the review
     * @param customerId id of the customer writing the review
     * @return the review id
     */
    int writeReview(HstRequest request, int productId, String reviewText, int rating, int customerId) throws Exception;

    /**
     * Write a review for a product
     *
     * @param request the hst request
     * @param reviewText the text to put into the review
     * @param rating     the rating for the review
     * @param customerId id of the customer writing the review
     */
    void writeReview(HstRequest request, String reviewText, int rating, int customerId) throws KKException, Exception;

}
