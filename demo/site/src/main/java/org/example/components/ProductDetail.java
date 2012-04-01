package org.example.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.util.HstResponseUtils;
import org.onehippo.forge.konakart.hst.components.KKProductDetail;

public class ProductDetail extends KKProductDetail {

    @Override
    protected void redirectAfterProductAddedToBasket(HstRequest request, HstResponse response) {

        redirectByRefId(request, response, getDetailCartRefId());
    }
}