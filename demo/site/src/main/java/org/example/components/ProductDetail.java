package org.example.components;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.components.KKProductDetail;

import javax.annotation.Nonnull;

public class ProductDetail extends KKProductDetail {

    @Override
    protected void redirectAfterProductAddedToBasket(boolean added, @Nonnull HstRequest request, @Nonnull HstResponse response) {
        if(added) {
            redirectByRefId(request, response, getDetailCartRefId());
        }
    }
}