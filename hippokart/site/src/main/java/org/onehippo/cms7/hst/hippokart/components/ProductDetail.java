package org.onehippo.cms7.hst.hippokart.components;

import com.konakart.al.KKAppEng;
import com.konakart.appif.ProductIf;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.components.KKProductDetail;

public class ProductDetail extends KKProductDetail {

    public static final String CROSS_SELL_PRODUCTS = "crossCellProducts";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response, KKProductDocument document) throws HstComponentException {
        super.doBeforeRender(request, response, document);

        KKAppEng kkAppEng = getKKAppEng(request);

        ProductIf[] crossSellProducts = kkAppEng.getProductMgr().getCrossSellProducts();

        if (crossSellProducts != null && crossSellProducts.length > 0) {
            request.setAttribute(CROSS_SELL_PRODUCTS, convertProducts(request, crossSellProducts));
        }
    }
}
