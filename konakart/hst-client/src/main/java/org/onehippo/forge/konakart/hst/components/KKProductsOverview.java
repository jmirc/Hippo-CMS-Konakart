package org.onehippo.forge.konakart.hst.components;

import com.konakart.appif.ProductIf;
import com.konakart.bl.ProductMgr;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.parameters.ParametersInfo;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.componentsinfo.KKProductsOverviewInfo;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.onehippo.forge.utilities.hst.paging.IterablePagination;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * This overview component offers methods used to retrieve products information
 */
@ParametersInfo(type = KKProductsOverviewInfo.class)
public class KKProductsOverview extends KKHstActionComponent {

    private static final String PARAM_CURRENT_PAGE = "pageNumber";
    private static final int DEFAULT_CURRENT_PAGE = 1;

    @Override
    final public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        // Retrieve the info used to customize the search
        KKProductsOverviewInfo overviewInfo = getParametersInfo(request);

        // Search products
        List<KKProductDocument> products = searchProducts(request, overviewInfo);

        int resultCount = products.size();

        // Retrieve the current page
        String currentPageParam = getPublicRequestParameter(request, PARAM_CURRENT_PAGE);
        int currentPage = KKUtil.parseIntParameter(PARAM_CURRENT_PAGE, currentPageParam, DEFAULT_CURRENT_PAGE, log);


        // Create the pagination
        IterablePagination<KKProductDocument> pages =
                new IterablePagination<KKProductDocument>(products, overviewInfo.getPageSize(), currentPage);

        request.setAttribute("products", pages);
        request.setAttribute("count", resultCount);
    }

    /**
     * Search products and returns the found list.
     *
     *
     * @param hstRequest the Hst request
     * @param overviewInfo the Parameters info annotation
     * @return a list of products
     */
    protected List<KKProductDocument> searchProducts(@Nonnull HstRequest hstRequest, KKProductsOverviewInfo overviewInfo) {
        ProductIf[] productIfs = KKServiceHelper.getKKProductService().
                fetchNewProducts(hstRequest, ProductMgr.DONT_INCLUDE, true, overviewInfo.getLimit());

        return convertProducts(hstRequest, productIfs);
    }
}
