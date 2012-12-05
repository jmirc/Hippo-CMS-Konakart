package org.onehippo.forge.konakart.site.service.impl;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.al.ReviewMgr;
import com.konakart.app.KKException;
import com.konakart.app.Review;
import com.konakart.appif.ReviewIf;
import com.konakart.util.Utils;
import com.konakartadmin.app.AdminReview;
import com.konakartadmin.blif.AdminReviewMgrIf;
import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.onehippo.forge.konakart.site.service.KKReviewService;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

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
      int nbReviews = reviewMgr.fetchReviewsPerProduct(productId);

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
  public void writeReview(HstRequest request, String reviewText, int rating, int customerId) throws Exception {
    ReviewMgr reviewMgr = KKServiceHelper.getKKEngineService().getKKAppEng(request).getReviewMgr();
    reviewMgr.writeReview(reviewText, rating, customerId);
  }

  @Override
  public int writeReview(HstRequest request, int productId, String reviewText, int rating, int customerId) throws Exception {

    KKAppEng kkAppEng = getKKAppEng(request);

    Review review = new Review();

    review.setRating(rating);
    review.setReviewText(Utils.escapeHtml(reviewText));
    review.setProductId(productId);
    review.setLanguageId(kkAppEng.getLangId());
    review.setCustomerId(customerId);

    return kkAppEng.getEng().writeReview(kkAppEng.getSessionId(), review);
  }

  @Override
  public int writeReview(HstRequest request, int productId, String reviewText, int rating, int customerId, boolean isVisible) throws Exception {

    KKAppEng kkAppEng = getKKAppEng(request);

    AdminReviewMgrIf reviewMgr = KKAdminEngine.getInstance().getFactory().getAdminReviewMgr(true);

    AdminReview review = new AdminReview();
    review.setRating(rating);
    review.setReviewText(Utils.escapeHtml(reviewText));
    review.setProductId(productId);
    review.setLanguageId(kkAppEng.getLangId());
    review.setCustomerId(customerId);
    review.setStatus(isVisible ? 1 : 0);

    return reviewMgr.insertReview(review);
  }
}
