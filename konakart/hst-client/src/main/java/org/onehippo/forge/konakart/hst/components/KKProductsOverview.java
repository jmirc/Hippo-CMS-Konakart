package org.onehippo.forge.konakart.hst.components;

import com.konakart.app.KKException;
import com.konakart.appif.ProductIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.onehippo.forge.utilities.hst.paging.DefaultPagination;
import org.onehippo.forge.utilities.hst.paging.Pageable;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * This overview component offers methods used to retrieve products information
 */
public class KKProductsOverview extends KKHstActionComponent {

  public static final int NO_CATEGORY = com.konakart.bl.ProductMgr.DONT_INCLUDE;
  public static final int NO_MANUFACTURER = com.konakart.bl.ProductMgr.DONT_INCLUDE;

  private static final int DEFAULT_LIMIT = 100;
  private static final int DEFAULT_PAGE_SIZE = 6;

  private static final String PARAM_CURRENT_PAGE = "pageNumber";
  private static final int DEFAULT_CURRENT_PAGE = 1;

  @Override
  public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
    super.doBeforeRender(request, response);

    resetKonkartStates(request);

    // Search products
    ProductIf[] products = searchProducts(request);

    int resultCount = products.length;

    // Retrieve the current page
    String currentPageParam = request.getParameter(PARAM_CURRENT_PAGE);

    if (StringUtils.isEmpty(currentPageParam)) {
      currentPageParam = getPublicRequestParameter(request, PARAM_CURRENT_PAGE);
    }

    int currentPage = KKUtil.parseIntParameter(PARAM_CURRENT_PAGE, currentPageParam, DEFAULT_CURRENT_PAGE, log);

    // Create the pagination
    Pageable pagination = new DefaultPagination<ProductIf>(resultCount, Arrays.asList(products));
    pagination.setPageSize(getPageSize());
    pagination.setPageNumber(currentPage);

    request.setAttribute("products", pagination);
    request.setAttribute("count", resultCount);
  }

  /**
   * Reset the state of the objects connected to the session. i.e. Selected product etc.
   * <p/>
   * By default this method is called but you can overrides to ignore it.
   */
  public void resetKonkartStates(HstRequest request) {
    try {
      getKKAppEng(request).reset();
    } catch (KKException e) {
      log.error("Failed to reset the state of the objects", e);
    }

  }

  /**
   * Search products and returns the found list.
   *
   * @param hstRequest the Hst request
   * @return a list of products
   */
  protected ProductIf[] searchProducts(@Nonnull HstRequest hstRequest) {
    return KKServiceHelper.getKKProductService().
        fetchNewProducts(hstRequest, NO_CATEGORY, true, DEFAULT_LIMIT);
  }

  public int getPageSize() {
    return DEFAULT_PAGE_SIZE;
  }
}
