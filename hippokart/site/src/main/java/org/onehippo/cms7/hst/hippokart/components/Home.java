package org.onehippo.cms7.hst.hippokart.components;

import com.konakart.al.ProductMgr;
import com.konakart.appif.ProductIf;
import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.hst.components.KKProductsOverview;

import javax.annotation.Nonnull;

public class Home extends KKProductsOverview {



    @Override
    protected ProductIf[] searchProducts(@Nonnull HstRequest hstRequest) {
        try {
            ProductMgr productMgr = getKKAppEng(hstRequest).getProductMgr();

            productMgr.fetchNewProductsArray(NO_CATEGORY);
            return productMgr.getNewProducts();

        } catch (Exception e) {
            return new ProductIf[0];
        }
    }
}
