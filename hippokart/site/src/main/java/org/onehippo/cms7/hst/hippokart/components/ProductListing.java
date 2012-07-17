package org.onehippo.cms7.hst.hippokart.components;

import com.konakart.al.ProductMgr;
import com.konakart.appif.CategoryIf;
import com.konakart.appif.ProductIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.components.KKProductsOverview;
import org.onehippo.forge.konakart.hst.utils.KKComponentUtils;

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

        try {
            if (currentCategory.getId() > 0) {
                ProductMgr productMgr = getKKAppEng(request).getProductMgr();
                productMgr.fetchProductsPerCategory(currentCategory);
                return productMgr.getCurrentProducts();
            }
        } catch (Exception e) {
            return new ProductIf[0];
        }

        return super.searchProducts(request);
    }

    @Override
    public void resetKonkartStates(HstRequest request) {
        // do nothing
    }
}
