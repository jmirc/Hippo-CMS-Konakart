package org.onehippo.cms7.hst.hippokart.components;

import com.konakart.app.DataDescConstants;
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

        Integer categoryId = NO_CATEGORY;

        try {
            categoryId = Integer.parseInt(sCategoryId);
        } catch (NumberFormatException e) {
            super.resetKonkartStates(request);
        }

        if (StringUtils.isNotBlank(sCategoryId)) {
            try {
                KKComponentUtils.getKKAppEng(request).getCategoryMgr().setCurrentCat(categoryId);
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

        if (currentCategory.getId() > 0) {
            return KKServiceHelper.getKKProductService().
                    fetchNewProducts(request, currentCategory.getId(), true, false, 100, DataDescConstants.ORDER_BY_NAME_ASCENDING);
        }

        return super.searchProducts(request);
    }

    @Override
    public void resetKonkartStates(HstRequest request) {
        // do nothing
    }
}
