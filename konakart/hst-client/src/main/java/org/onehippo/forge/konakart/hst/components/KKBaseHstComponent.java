package org.onehippo.forge.konakart.hst.components;

import com.konakart.al.KKAppEng;
import com.konakart.app.DataDescriptor;
import com.konakart.appif.DataDescriptorIf;
import com.konakart.appif.ProductIf;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.content.beans.query.HstQuery;
import org.hippoecm.hst.content.beans.query.HstQueryResult;
import org.hippoecm.hst.content.beans.query.filter.Filter;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.util.HstResponseUtils;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.utils.KKComponentUtils;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.Node;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This class is the based class to interact with Konakart
 */
public class KKBaseHstComponent extends BaseHstComponent {

  public static final String CART_DETAIL_ID = "cartDetailId";
  public static final String MY_ACCOUNT_ID = "myAccountId";
  /**
   * The <code>Log</code> instance for this application.
   */
  protected Logger log = LoggerFactory.getLogger(KKBaseHstComponent.class);

  @Override
  public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
    super.doBeforeRender(request, response);

    KKComponentUtils.setGlobalKonakartAttributes(request);
  }

  /**
   * Retrieve the Konakart client from the HstRequest.
   * The client has been set by the Konakart Valve.
   *
   * @param request the hst request
   * @return the Konakart client.
   */
  @Nonnull
  public KKAppEng getKKAppEng(@Nonnull HstRequest request) {
    KKAppEng kkAppEng = KKComponentUtils.getKKAppEng(request);

    return checkNotNull(kkAppEng);
  }

  /**
   * Check if the current customer is a guest or a registered customer
   *
   * @param request the hst request
   * @return true if the customer is a guest, false otherwise.
   */
  public boolean isGuestCustomer(@Nonnull HstRequest request) {
    return KKServiceHelper.getKKCustomerService().isGuestCustomer(request);
  }

  /**
   * @return the siteMapItemRefId associated with the detail cart.
   */
  public String getCartDetailRefId() {
    return CART_DETAIL_ID;
  }

  /**
   * @return the siteMapItemRefId associated with the my account.
   */
  public String getMyAccountRefId() {
    return MY_ACCOUNT_ID;
  }

  /**
   * Redirect the user to the 404 page
   *
   * @param response the Hst response
   */
  public void redirectToNotFoundPage(HstResponse response) {
    try {
      response.forward("/404");
    } catch (IOException e) {
      throw new HstComponentException(e);
    }
  }

  /**
   * This is an helper class to redirect the customer to another page
   *
   * @param request  the HstRequest
   * @param response the HstResponse
   * @param refId    the refId
   */
  public void redirectByRefId(HstRequest request, HstResponse response, String refId) {

    HstLinkCreator linkCreator = request.getRequestContext().getHstLinkCreator();

    HstLink link = linkCreator.createByRefId(refId, request.getRequestContext().getResolvedMount().getMount());

    HstResponseUtils.sendRedirectOrForward(request, response, link.getPath());
  }

  /**
   * Get the current Konakart Product Document
   *
   * @param request the HST request
   * @return the product document
   * @throws org.hippoecm.hst.core.component.HstComponentException
   *          thrown if the document is not a type of KKProductDocument
   */
  public KKProductDocument getKKProductDocument(@Nonnull HstRequest request) throws HstComponentException {
    HippoBean currentBean = getContentBean(request);

    if (currentBean == null) {
      throw new HstComponentException("No document has been found");
    }

    // Not an instance of KKProductdocuemnt
    if (!(currentBean instanceof KKProductDocument)) {
      log.error(currentBean.getClass().getName() + " must extend " + KKProductDocument.class.getName());
      throw new HstComponentException(currentBean.getClass().getName() + " must extend " +
          KKProductDocument.class.getName());
    }

    return (KKProductDocument) currentBean;
  }

  /**
   * Convert a konakart products to a KKProductDocument
   *
   * @param hstRequest the hst request
   * @param product    a konakart product
   */
  @Nullable
  public KKProductDocument convertProduct(HstRequest hstRequest, ProductIf product) {
    if (product == null) {
      return null;
    }

    return convertProduct(hstRequest, product.getId());
  }

  /**
   * Convert a konakart products to a KKProductDocument
   *
   * @param hstRequest the hst request
   * @param productId  id of the product to search
   */
  @Nullable
  public KKProductDocument convertProduct(HstRequest hstRequest, int productId) {

    try {

      KKStoreConfig kkStoreConfig = KKComponentUtils.getKKStoreConfig(hstRequest);

      Node scope = hstRequest.getRequestContext().getSession().getNode(kkStoreConfig.getContentRoot());

      // the third argument, 'true', indicates whether to include subtypes
      HstQuery hstQuery = getQueryManager(hstRequest).createQuery(scope, KKProductDocument.class, true);

      Filter filter = hstQuery.createFilter();
      filter.addEqualTo(KKCndConstants.PRODUCT_ID, (long) productId);

      hstQuery.setFilter(filter);

      // execute the query
      HstQueryResult result = hstQuery.execute();

      if (result.getSize() == 0) {
        log.error("Failed to retrieve the KKPRoductDocument with the konakart id = " + productId);
        return null;
      }

      // return the first element
      return (KKProductDocument) result.getHippoBeans().next();
    } catch (Exception e) {
      log.error("Failed to find the Hippo Document for the product id - " + productId, e);
    }

    return null;
  }

  /**
   * Convert a konakart products to a KKProductDocument
   *
   * @param hstRequest the hst request
   * @param product    a konakart product
   */
  @Nullable
  public ProductIf convertProduct(HstRequest hstRequest, KKProductDocument product) {
    if (product == null) {
      return null;
    }

    ProductIf productIf = null;

    try {
      KKAppEng kkAppEng = getKKAppEng(hstRequest);
      productIf = kkAppEng.getEng().getProduct(kkAppEng.getSessionId(), product.getProductId(), kkAppEng.getLangId());

      DataDescriptorIf dataDescriptorIf = new DataDescriptor();
      dataDescriptorIf.setShowInvisible(false);

    } catch (Exception e) {
      log.error("Failed to find the Konakart product with the id - " + product.getProductId());
    }

    return productIf;
  }


  /**
   * Convert a list of konakart products to a list of KKProductDocument
   *
   * @param productIfs list of konakart products
   */
  public List<KKProductDocument> convertProducts(HstRequest hstRequest, ProductIf[] productIfs) {

    if (productIfs == null || productIfs.length == 0) {
      return Collections.emptyList();
    }

    LinkedList<KKProductDocument> documents = new LinkedList<KKProductDocument>();

    for (ProductIf productIf : productIfs) {

      KKProductDocument document = convertProduct(hstRequest, productIf.getId());

      if (document != null) {
        documents.addLast(document);
      }
    }

    return documents;
  }
}
