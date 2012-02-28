package org.onehippo.forge.konakart.common.al;

import com.konakart.app.DataDescriptor;
import com.konakart.app.KKException;
import com.konakart.app.Review;
import com.konakart.appif.DataDescriptorIf;
import com.konakart.appif.ProductIf;
import com.konakart.appif.ReviewIf;
import com.konakart.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.common.engine.KKEngine;

/**
 * Contains methods to read and write product reviews.
 */
public class ReviewMgr extends BaseMgr {

    /**
     * Default constuctor
     *
     * @param kkEngine the Konakart Engine
     */
    public ReviewMgr(KKEngine kkEngine) {
        super(kkEngine);
    }

    /**
     * The reviews are fetched from the engine and put in the currentReviews array.
     *
     *
     * @param prodId The id of the product for which we are fetching reviews
     * @param langId language's id
     * @return The reviews
     * @throws com.konakart.app.KKException .
     */
    public ReviewIf[] fetchReviewsPerProduct(int prodId, int langId) throws KKException {

        ProductIf selectedProduct = kkEngine.getProductMgr().getProductById(prodId, langId);

        DataDescriptorIf dataDescriptorIf = new DataDescriptor();

        return kkEng.getReviewsPerProduct(dataDescriptorIf, prodId).getReviewArray();
    }

    /**
     * Save a review in the database
     *
     * @param reviewText - the text to put into the review
     * @param rating - the rating for the review
     * @param customerId - id of the customer writing the review
     * @param productId the product id
     * @return the id of the review that was created
     * @throws com.konakart.app.KKException .
     */
    public int writeReview(String reviewText, int rating, int customerId, int productId) throws KKException {
        Review review = new Review();
        review.setRating(rating);
        
        if (!StringUtils.isEmpty(reviewText)) {
            reviewText = Utils.escapeHtml(reviewText);
        }

        review.setReviewText(reviewText);
        review.setCustomerId(customerId);
        review.setProductId(productId);

        return kkEng.writeReview(kkEngine.getSessionId(), review);
    }
}
