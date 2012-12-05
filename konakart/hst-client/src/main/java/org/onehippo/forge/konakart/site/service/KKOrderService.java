package org.onehippo.forge.konakart.site.service;

import com.konakart.appif.BasketIf;
import com.konakart.appif.OrderIf;
import org.hippoecm.hst.core.component.HstRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface KKOrderService {

  /**
   * Populate checkout order with a temporary order created before the checkout process really
   * begins. If the customer hasn't registered or logged in yet, we use the default customer to
   * create the order.
   * <p/>
   * With this temporary order we can give the customer useful information on shipping costs and
   * discounts without him having to login.
   *
   * @param request the hst request
   * @param custId  the customer Id
   * @param items   the basket's items
   */
  @Nullable
  OrderIf createTempOrder(@Nonnull HstRequest request, int custId, final BasketIf[] items);

}
