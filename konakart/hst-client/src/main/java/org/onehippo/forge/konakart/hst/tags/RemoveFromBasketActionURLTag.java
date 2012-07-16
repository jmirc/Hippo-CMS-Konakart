package org.onehippo.forge.konakart.hst.tags;

import org.hippoecm.hst.core.component.HstURL;
import org.hippoecm.hst.tag.HstActionURLTag;
import org.onehippo.forge.konakart.hst.utils.KKActionsConstants;
import org.onehippo.forge.konakart.hst.vo.CartItem;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

public class RemoveFromBasketActionURLTag extends HstActionURLTag {


    private CartItem cartItem;

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    @Override
    protected void setUrlParameters(HstURL url) {
        super.setUrlParameters(url);

        url.setParameter("action", KKActionsConstants.ACTIONS.REMOVE_FROM_BASKET.name());
        url.setParameter("basketId", String.valueOf(cartItem.getBasketItemId()));
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
