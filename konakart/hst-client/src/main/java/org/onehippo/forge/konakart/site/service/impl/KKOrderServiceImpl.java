package org.onehippo.forge.konakart.site.service.impl;

import com.konakart.al.KKAppEng;
import com.konakart.app.CreateOrderOptions;
import com.konakart.appif.BasketIf;
import com.konakart.appif.CreateOrderOptionsIf;
import com.konakart.appif.OrderIf;
import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.site.service.KKOrderService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KKOrderServiceImpl extends KKBaseServiceImpl implements KKOrderService {


  @Override
  @Nullable
  public OrderIf createTempOrder(@Nonnull HstRequest request, int custId, BasketIf[] items) {

    KKAppEng kkAppEng = getKKAppEng(request);
    try {
      String sessionId = null;

      // Reset the checkout order
      kkAppEng.getOrderMgr().setCheckoutOrder(null);

      CreateOrderOptionsIf options = new CreateOrderOptions();
      if (custId < 0) {
        options.setUseDefaultCustomer(true);
      } else {
        sessionId = kkAppEng.getSessionId();
        options.setUseDefaultCustomer(false);
      }

      // Add extra info to the options
      if (kkAppEng.getFetchProdOptions() != null) {
        options.setPriceDate(kkAppEng.getFetchProdOptions().getPriceDate());
        options.setCatalogId(kkAppEng.getFetchProdOptions().getCatalogId());
        options.setUseExternalPrice(kkAppEng.getFetchProdOptions().isUseExternalPrice());
        options.setUseExternalQuantity(kkAppEng.getFetchProdOptions().isUseExternalQuantity());
      }

      // Create the order
      OrderIf order = kkAppEng.getEng().createOrderWithOptions(sessionId, items, options,
          kkAppEng.getLangId());

      if (order == null) {
        return null;
      }

            /*
            * We set the customer id to that of the guest customer so that promotions with
            * expressions are calculated correctly
            */
      if (custId < 0) {
        order.setCustomerId(kkAppEng.getCustomerMgr().getCurrentCustomer().getId());
      }

      // Populate the order with the coupon code if it exists
      order.setCouponCode(kkAppEng.getOrderMgr().getCouponCode());

      // Set the checkout order to be the new order
      kkAppEng.getOrderMgr().setCheckoutOrder(order);

      // Get shipping quotes and select the first one
      kkAppEng.getOrderMgr().createShippingQuotes();
      if (kkAppEng.getOrderMgr().getShippingQuotes() != null
          && kkAppEng.getOrderMgr().getShippingQuotes().length > 0) {

        kkAppEng.getOrderMgr().getCheckoutOrder().setShippingQuote(kkAppEng.getOrderMgr().getShippingQuotes()[0]);
      }

      // Populate the checkout order with order totals
      kkAppEng.getOrderMgr().populateCheckoutOrderWithOrderTotals();

      return order;

    } catch (Exception e) {
      // If the order can't be created we don't report back an exception
      if (log.isWarnEnabled()) {
        log.warn("A temporary order could not be created", e);
      }
    }

    return null;
  }
}

