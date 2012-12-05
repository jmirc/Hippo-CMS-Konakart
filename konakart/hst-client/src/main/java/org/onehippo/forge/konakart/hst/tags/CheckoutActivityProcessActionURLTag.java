package org.onehippo.forge.konakart.hst.tags;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstURL;
import org.hippoecm.hst.tag.HstActionURLTag;
import org.onehippo.forge.konakart.hst.utils.KKActionsConstants;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class CheckoutActivityProcessActionURLTag extends HstActionURLTag {


  private String action = KKActionsConstants.ACTIONS.SELECT.name();
  private String state = null;

  public void setAction(String action) {
    this.action = action;
  }

  public void setState(String state) {
    this.state = state;
  }

  @Override
  protected void setUrlParameters(HstURL url) {
    super.setUrlParameters(url);

    url.setParameter(KKActionsConstants.ACTION, action);

    String currentState = (String) pageContext.getRequest().getAttribute(KKActionsConstants.STATE);

    if (StringUtils.isEmpty(state)) {
      state = currentState;
    }

    url.setParameter(KKActionsConstants.STATE, state);

    String dontHaveAccount = (String) pageContext.getRequest().getAttribute(KKActionsConstants.DONT_HAVE_ACCOUNT);

    if (StringUtils.isEmpty(dontHaveAccount)) {
      dontHaveAccount = pageContext.getRequest().getParameter(KKActionsConstants.DONT_HAVE_ACCOUNT);
    }

    url.setParameter(KKActionsConstants.DONT_HAVE_ACCOUNT, dontHaveAccount);
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
