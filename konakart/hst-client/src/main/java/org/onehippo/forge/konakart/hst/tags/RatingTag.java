package org.onehippo.forge.konakart.hst.tags;

import com.konakart.app.DataDescriptor;
import com.konakart.app.KKException;
import com.konakart.appif.DataDescriptorIf;
import com.konakart.appif.ReviewIf;
import com.konakart.appif.ReviewsIf;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class RatingTag extends KKTagSupport {

  protected Integer productId;
  protected String ratingVar;
  protected String nbReviewsVar;
  protected Boolean showVisible = false;

  public void setProductId(Integer productId) {
    this.productId = productId;
  }

  public void setRatingVar(String ratingVar) {
    this.ratingVar = ratingVar;
  }

  public void setNbReviewsVar(String nbReviewsVar) {
    this.nbReviewsVar = nbReviewsVar;
  }

  public void setShowVisible(Boolean showVisible) {
    this.showVisible = showVisible;
  }

  /* (non-Javadoc)
  * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
  */
  @Override
  public int doStartTag() throws JspException {
    if (ratingVar != null) {
      pageContext.removeAttribute(ratingVar, PageContext.PAGE_SCOPE);
    }

    if (nbReviewsVar != null) {
      pageContext.removeAttribute(nbReviewsVar, PageContext.PAGE_SCOPE);
    }

    return EVAL_BODY_INCLUDE;
  }

  @Override
  public int doEndTag() throws JspException {

    Double ratingValue = 0D;

    DataDescriptorIf dataDescriptorIf = new DataDescriptor();
    dataDescriptorIf.setShowInvisible(showVisible);

    double numberOfReview = 0;

    try {
      ReviewsIf reviewsIf = getKkAppEng().getEng().getReviewsPerProduct(dataDescriptorIf, productId);

      if (reviewsIf.getTotalNumReviews() == 0) {
        return 0;
      }

      // Retreive the reviews.
      ReviewIf[] reviews = reviewsIf.getReviewArray();

      // Double check...
      if (reviews != null && reviews.length > 0) {

        numberOfReview = reviews.length;

        double rating = 0;

        for (ReviewIf reviewIf : reviews) {
          rating += reviewIf.getRating();
        }

        ratingValue = rating / reviews.length;
      }
    } catch (KKException e) {
      ratingValue = 0D;
    }

    writeOrSetVar(ratingVar, ratingValue);
    writeOrSetVar(nbReviewsVar, numberOfReview);

    return EVAL_PAGE;
  }

  private void writeOrSetVar(String var, Double rating) throws JspException {
    if (var == null) {
      JspWriter writer = pageContext.getOut();
      try {
        writer.write(rating.toString());
      } catch (IOException e) {
        throw new JspException("IOException while trying to write script tag", e);
      }
    } else {
      int varScope = PageContext.PAGE_SCOPE;
      pageContext.setAttribute(var, rating, varScope);
    }
  }
}
