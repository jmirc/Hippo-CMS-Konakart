package org.onehippo.cms7.hst.hippokart.components;

import com.konakart.appif.ProductIf;
import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.hst.components.KKProductsOverview;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import javax.annotation.Nonnull;

public class Home extends KKProductsOverview {

    @Override
    protected ProductIf[] searchProducts(@Nonnull HstRequest hstRequest) {
        return KKServiceHelper.getKKProductService().
                fetchNewProducts(hstRequest, NO_CATEGORY, true, 6);
    }
}
