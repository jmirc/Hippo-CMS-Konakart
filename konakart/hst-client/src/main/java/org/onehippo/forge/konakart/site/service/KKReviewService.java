package org.onehippo.forge.konakart.site.service;

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
     * @param reviewText the text to put into the review
     * @param rating     the rating for the review
     * @param customerId id of the customer writing the review
     */
    void writeReview(String reviewText, int rating, int customerId);

}
