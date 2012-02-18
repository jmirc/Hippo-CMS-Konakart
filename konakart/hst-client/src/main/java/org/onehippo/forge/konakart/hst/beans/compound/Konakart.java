package org.onehippo.forge.konakart.hst.beans.compound;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoHtml;
import org.hippoecm.hst.content.beans.standard.HippoItem;
import org.onehippo.forge.konakart.common.KKCndConstants;

@Node(jcrType = KKCndConstants.DOCUMENT_TYPE)
public class Konakart extends HippoItem {

    public Long getProductId() {
        return getProperty(KKCndConstants.PRODUCT_ID);
    }

    public Long getLanguageId() {
        return getProperty(KKCndConstants.PRODUCT_LANGUAGE_ID);
    }

    public String getProductSku() {
        return getProperty(KKCndConstants.PRODUCT_SKU);
    }

    public String getProductName() {
        return getProperty(KKCndConstants.PRODUCT_NAME);
    }

    public HippoHtml getProductDescription() {
        return getProperty(KKCndConstants.PRODUCT_DESCRIPTION);
    }




}
