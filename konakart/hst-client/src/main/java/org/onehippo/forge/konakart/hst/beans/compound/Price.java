package org.onehippo.forge.konakart.hst.beans.compound;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoItem;
import org.onehippo.forge.konakart.common.KKCndConstants;

@Node(jcrType = KKCndConstants.CP_PRICE_TYPE)
public class Price extends HippoItem {

    public Double gePrice0ExTax() {
        return getProperty(KKCndConstants.CP_PRICE_0);
    }

    public Double gePrice1ExTax() {
        return getProperty(KKCndConstants.CP_PRICE_1);
    }

    public Double gePrice2ExTax() {
        return getProperty(KKCndConstants.CP_PRICE_2);
    }

    public Double gePrice3ExTax() {
        return getProperty(KKCndConstants.CP_PRICE_3);
    }

}
