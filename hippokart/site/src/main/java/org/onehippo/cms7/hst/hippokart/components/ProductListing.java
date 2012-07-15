package org.onehippo.cms7.hst.hippokart.components;

import com.konakart.appif.CategoryIf;
import com.konakart.appif.ProductIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.components.KKProductsOverview;
import org.onehippo.forge.konakart.hst.utils.KKComponentUtils;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import javax.annotation.Nonnull;

public class ProductListing extends KKProductsOverview {

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {

        String pathInfo = request.getRequestContext().getBaseURL().getPathInfo();
        String sCategoryId = StringUtils.substringAfterLast(pathInfo, "/");

        if (StringUtils.isNotBlank(sCategoryId)) {
            try {
                KKComponentUtils.getKKAppEng(request).getCategoryMgr().setCurrentCat(Integer.parseInt(sCategoryId));
            } catch (Exception e) {
                // no dothing
            }
        }

        KKComponentUtils.setCurrentCategories(request);

        super.doBeforeRender(request, response);
    }

    @Override
    protected ProductIf[] searchProducts(@Nonnull HstRequest request) {
        CategoryIf currentCategory = KKComponentUtils.getKKAppEng(request).getCategoryMgr().getCurrentCat();

        return KKServiceHelper.getKKProductService().
                fetchNewProducts(request, currentCategory.getId(), true, 100);
    }

    @Override
    public void resetKonkartStates(HstRequest request) {
        // do nothing
    }
}
