package org.onehippo.forge.konakart.site.service.impl;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.al.ReviewMgr;
import com.konakart.app.KKException;
import com.konakart.app.Review;
import com.konakart.appif.ReviewIf;
import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.site.service.KKReviewService;

import javax.annotation.Nonnull;

public class KKReviewServiceImpl extends KKBaseServiceImpl implements KKReviewService {

    @Override
    public ReviewIf[] getReviewsForProductId(@Nonnull HstRequest request, int productId) {
        return getReviewsForProductId(request, productId, false);
    }

    @Override
    @Nonnull
    public ReviewIf[] getReviewsForProductId(@Nonnull HstRequest request, int productId, boolean showInvisible) {
        KKAppEng kkAppEng = getKKAppEng(request);

        ReviewMgr reviewMgr = kkAppEng.getReviewMgr();

        try {
            reviewMgr.getDataDesc().setShowInvisible(showInvisible);
            int nbReviews = reviewMgr.fetchAllReviews();

            if (nbReviews > 0) {
                return reviewMgr.getCurrentReviews();
            }
        } catch (KKException e) {
            log.warn("Failed to fetch the list of reviews");
        } catch (KKAppException e) {
            log.warn("Failed to fetch the list of reviews");
        }


        return new Review[0];
    }

    @Override
    public void writeReview(String reviewText, int rating, int customerId) {

    }


}
