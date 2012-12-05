package org.onehippo.forge.konakart.hst.tags;

import org.hippoecm.hst.core.component.HstURL;
import org.hippoecm.hst.tag.HstActionURLTag;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.utils.KKActionsConstants;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class RemoveProductFromWishListActionURLTag extends HstActionURLTag {


  private KKProductDocument kkProductDocument;
  private Integer wishListId;

  public void setProduct(KKProductDocument kkProductDocument) {
    this.kkProductDocument = kkProductDocument;
  }

  public void setWishListId(Integer wishListId) {
    this.wishListId = wishListId;
  }

  @Override
  protected void setUrlParameters(HstURL url) {
    super.setUrlParameters(url);

    url.setParameter("action", KKActionsConstants.ACTIONS.REMOVE_FROM_WISHLIST.name());
    url.setParameter("prodId", String.valueOf(kkProductDocument.getProductId()));
    url.setParameter("wishListId", String.valueOf(wishListId));
  }

  /**
   * TagExtraInfo class for HstURLTag.
   */
  public static class TEI extends TagExtraInfo {

    public VariableInfo[] getVariableInfo(TagData tagData) {
      VariableInfo vi[] = null;
      String var = tagData.getAttributeString("var");
      if (var != null) {
        vi = new VariableInfo[1];
        vi[0] =
            new VariableInfo(var, "java.lang.String", true,
                VariableInfo.AT_BEGIN);
      }
      return vi;
    }

  }
}
