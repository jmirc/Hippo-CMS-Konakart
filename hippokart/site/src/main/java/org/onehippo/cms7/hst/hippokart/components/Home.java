package org.onehippo.cms7.hst.hippokart.components;

import com.konakart.al.KKAppException;
import com.konakart.app.DataDescConstants;
import com.konakart.app.KKException;
import com.konakart.appif.ProductIf;
import com.konakart.al.ProductMgr;
import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.hst.components.KKProductsOverview;

import javax.annotation.Nonnull;

public class Home extends KKProductsOverview {



    @Override
    protected ProductIf[] searchProducts(@Nonnull HstRequest hstRequest) {
        try {
            ProductMgr productMgr = getKKAppEng(hstRequest).getProductMgr();
            productMgr.fetchNewProductsArray(NO_CATEGORY);
            productMgr.orderCurrentProds(DataDescConstants.ORDER_BY_NAME_ASCENDING);
            return productMgr.getCurrentProducts();
        } catch (KKException e) {
            return new ProductIf[0];
        } catch (KKAppException e) {
            return new ProductIf[0];
        }
    }
}
