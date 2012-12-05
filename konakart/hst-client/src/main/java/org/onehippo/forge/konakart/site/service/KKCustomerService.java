package org.onehippo.forge.konakart.site.service;

import com.konakart.appif.CustomerIf;
import com.konakart.appif.ProductIf;
import com.konakart.appif.WishListIf;
import org.hippoecm.hst.core.component.HstRequest;

import javax.annotation.Nonnull;

public interface KKCustomerService {


  /**
   * Get the current customer (guest or registered)
   *
   * @param request the hst request
   * @return the current customer
   */
  CustomerIf getCurrentCustomer(@Nonnull HstRequest request);


  /**
   * Check if the current customer is a guest or a registered customer
   *
   * @param request the hst request
   * @return true if the customer is a guest, false otherwise.
   */
  boolean isGuestCustomer(HstRequest request);


  /**
   * Check if the wish list is enabled.
   *
   * @param request the hst request
   * @return true if the wish list is enabled, false otherwise
   */
  boolean wishListEnabled(HstRequest request);

  /**
   * @param request the hst request
   * @return the wish list that has the type equals to 0
   */
  WishListIf getDefaultWishList(HstRequest request);

  /**
   * @param request      the hst request
   * @param wishListName the name of the wish list
   * @param isPublic     true if the wish list is public, false otherwise
   * @return the wish list id, -1 if the wish list has not been created
   */
  int createDefaultWishList(@Nonnull HstRequest request, String wishListName, boolean isPublic);

  /**
   * @param request the hst request
   * @return the list of products that have been reviewed
   */
  ProductIf[] getCustomerProductsViewed(@Nonnull HstRequest request);
}
