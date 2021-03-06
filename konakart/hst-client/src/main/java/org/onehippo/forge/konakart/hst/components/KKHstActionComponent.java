package org.onehippo.forge.konakart.hst.components;

import com.konakart.al.KKAppEng;
import com.konakart.al.ProdOption;
import com.konakart.al.ProdOptionContainer;
import com.konakart.app.KKException;
import com.konakart.app.Option;
import com.konakart.appif.BasketIf;
import com.konakart.appif.OptionIf;
import com.konakart.appif.ProductIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormField;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.component.support.forms.FormUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.util.HstResponseUtils;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.utils.KKActionsConstants;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.onehippo.forge.konakart.site.service.KKTagsService;
import org.onehippo.forge.konakart.site.service.impl.KKEventServiceImpl;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public abstract class KKHstActionComponent extends KKBaseHstComponent {


  @Override
  final public void doAction(HstRequest request, HstResponse response) {
    String action = KKUtil.getEscapedParameter(request, KKActionsConstants.ACTION);

    response.setRenderParameter(KKActionsConstants.ACTION, action);
    doAction(action, request, response);
  }

  /**
   * Call with the prepopulated action. The value of the action is defined when the hst:url is created.
   * <p/>
   * i.e.
   * <hst:actionURL var="addToBasket">
   * <hst:param name="action" value="addToBasket"/>
   * <hst:param name="prodId" value="${document.productId}"/>
   * </hst:actionURL>
   *
   * @param action   the action value
   * @param request  the Hst Request
   * @param response the Hst Response
   */
  public void doAction(String action, HstRequest request, HstResponse response) {

    KKAppEng kkAppEng = getKKAppEng(request);


    if (StringUtils.equals(action, KKActionsConstants.ACTIONS.ADD_TO_BASKET.name())) {
      String sProductId = KKUtil.getActionRequestParameter(request, KKActionsConstants.PRODUCT_ID);
      String addToWishList = KKUtil.getActionRequestParameter(request, KKActionsConstants.ADD_TO_WISH_LIST);
      String sQuantity = KKUtil.getActionRequestParameter(request, KKActionsConstants.QUANTITY);

      int quantity = 1;

      if (StringUtils.isNotBlank(sQuantity)) {
        quantity = Integer.parseInt(sQuantity);

        if (quantity < 1) {
          quantity = 1;
        }
      }

      // Add this product to the basket
      if (StringUtils.isNotEmpty(sProductId)) {

        int productId = Integer.parseInt(sProductId);

        // Get the selected options if exists
        OptionIf[] optionIfs = retrieveSelectedProductOptions(kkAppEng, request);


        // Check if a product has been added but no options have been selected
        if (optionIfs.length == 0) {

          try {
            ProductIf productIf = kkAppEng.getEng().getProduct(kkAppEng.getSessionId(), productId,
                kkAppEng.getLangId());

            OptionIf[] currentOptionIfs = productIf.getOpts();

            if (currentOptionIfs != null && currentOptionIfs.length > 0) {
              KKProductDocument productDocument = convertProduct(request, productIf.getId());

              HstLinkCreator linkCreator = request.getRequestContext().getHstLinkCreator();
              HstLink link = linkCreator.create(productDocument, request.getRequestContext());

              HstResponseUtils.sendRedirect(request, response, link.getPath());

              return;
            }
          } catch (KKException e) {
            log.warn("Failed to retrieve the Konakart product with his id - " + productId);
            return;
          }
        }


        // Add this product to the wish list
        if (StringUtils.isNotEmpty(addToWishList) && Boolean.valueOf(addToWishList)) {
          String wishListId = KKUtil.getActionRequestParameter(request, KKActionsConstants.WISH_LIST_ID);

          if (StringUtils.isNotEmpty(wishListId)) {
            try {
              kkAppEng.getCustomerTagMgr().addToCustomerTag(KKTagsService.TAG_PRODUCTS_IN_WISHLIST, productId);
            } catch (Exception e) {
              log.warn("Failed to use the Customer tags feature. Please check if this feature has been enabled.", e);
            }

            boolean added = KKServiceHelper.getKKBasketService().addProductToWishList(kkAppEng, request,
                Integer.valueOf(wishListId), productId, optionIfs, quantity);

            redirectAfterProductAddedToWishList(added, request, response);
          }
        } else {
          boolean added = KKServiceHelper.getKKBasketService().addProductToBasket(kkAppEng, request,
              productId, optionIfs, quantity);

          redirectAfterProductAddedToBasket(added, request, response);
        }
      }

    }

    if (StringUtils.equals(action, KKActionsConstants.ACTIONS.REMOVE_FROM_BASKET.name())) {
      String basketId = KKUtil.getEscapedParameter(request, KKActionsConstants.BASKET_ID);

      // Remove this product fromthe basket
      if (StringUtils.isNotEmpty(basketId)) {

        int basketIdToRemove = Integer.valueOf(basketId);

        // remove the basket item
        try {
          // basket items
          BasketIf[] basketItems = kkAppEng.getCustomerMgr().getCurrentCustomer().getBasketItems();


          for (BasketIf basketItem : basketItems) {
            if (basketItem.getId() == basketIdToRemove) {
              kkAppEng.getBasketMgr().removeFromBasket(basketItem, /** refresh **/false);

              // insert an event
              KKServiceHelper.getKKEventService().insertCustomerEvent(request, KKEventServiceImpl.ACTION_REMOVE_FROM_CART,
                  basketItem.getProductId());
            }
          }
        } catch (Exception e) {
          log.error("Unable to remove the basket with the id - " + basketIdToRemove);
        }
      }
    }

    if (StringUtils.equals(action, KKActionsConstants.ACTIONS.ADD_TO_WISHLIST.name())) {
      String sProductId = KKUtil.getActionRequestParameter(request, KKActionsConstants.PRODUCT_ID);
      String wishListId = KKUtil.getActionRequestParameter(request, KKActionsConstants.WISH_LIST_ID);

      if (StringUtils.isNotEmpty(wishListId) && StringUtils.isNotEmpty(sProductId)) {
        KKServiceHelper.getKKBasketService().addProductToWishList(kkAppEng, request,
            Integer.valueOf(wishListId), Integer.valueOf(sProductId), null, 1);
      }
    }

    if (StringUtils.equals(action, KKActionsConstants.ACTIONS.REMOVE_FROM_WISHLIST.name())) {
      String sProductId = KKUtil.getActionRequestParameter(request, KKActionsConstants.PRODUCT_ID);
      String wishListId = KKUtil.getActionRequestParameter(request, KKActionsConstants.WISH_LIST_ID);

      if (StringUtils.isNotEmpty(wishListId) && StringUtils.isNotEmpty(sProductId)) {
        KKServiceHelper.getKKBasketService().removeProductToWishList(kkAppEng, request,
            Integer.valueOf(wishListId), Integer.valueOf(sProductId));
      }
    }
  }


  /**
   * Called when the product is added to the cart.
   * <p/>
   * By default no redirection is done
   *
   * @param added    true if the product has been added, false otherwise
   * @param request  the Hst Request
   * @param response the Hst Response
   */
  protected void redirectAfterProductAddedToBasket(boolean added, @Nonnull HstRequest request,
                                                   @Nonnull HstResponse response) {
    redirectByRefId(request, response, getCartDetailRefId());
  }


  /**
   * Called when the product is added to the wish list.
   * <p/>
   * By default no redirection is done
   *
   * @param added    true if the product has been added, false otherwise
   * @param request  the Hst Request
   * @param response the Hst Response
   */
  protected void redirectAfterProductAddedToWishList(boolean added, @Nonnull HstRequest request,
                                                     @Nonnull HstResponse response) {

  }


  /**
   * Used to retrieve for a product the option that has been selected by the customer.
   *
   * @param kkAppEng the Konakart client
   * @param request  the Hst Request
   * @return a list of options.
   */
  protected OptionIf[] retrieveSelectedProductOptions(@Nonnull KKAppEng kkAppEng, @Nonnull HstRequest request) {

    List<String> fieldsName = new ArrayList<String>();

    // Retrieve selected options
    List<ProdOptionContainer> opts = kkAppEng.getProductMgr().getSelectedProductOptions();

    for (ProdOptionContainer opt : opts) {
      fieldsName.add(opt.getId());
    }


    FormMap formMap = new FormMap(request, fieldsName);
    FormUtils.populate(request, formMap);

    OptionIf[] results = new OptionIf[opts.size()];

    int i = 0;

    // Retrieve selected options
    for (ProdOptionContainer opt : opts) {
      FormField field = formMap.getField(opt.getId());

      if (field != null) {
        String value = field.getValue();

        if (StringUtils.isNotEmpty(value)) {
          int selectedValue = Integer.parseInt(value);

          List<ProdOption> optValues = opt.getOptValues();

          for (ProdOption optValue : optValues) {
            if (optValue.getId() == selectedValue) {
              OptionIf optionIf = new Option();
              optionIf.setId(Integer.parseInt(opt.getId()));
              optionIf.setValueId(optValue.getId());
              optionIf.setType(Integer.parseInt(opt.getType()));

              results[i++] = optionIf;
            }
          }

        }
      }

    }

    return results;
  }

}
