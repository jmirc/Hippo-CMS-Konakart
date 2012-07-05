package org.onehippo.forge.konakart.hst.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;

import javax.annotation.Nonnull;

public class DefaultKKProductsOverview extends AbstractKKProductsOverview<KKProductDocument> {

    @Override
    protected void doBeforeRender(HstRequest request, HstResponse response, HippoBean currentBean) {
        // do nothing
    }

    @Override
    protected void redirectAfterProductAddedToBasket(boolean added, @Nonnull HstRequest request, @Nonnull HstResponse response) {

    }

    @Override
    protected void redirectAfterProductAddedToWishList(boolean added, @Nonnull HstRequest request, @Nonnull HstResponse response) {

    }
}
