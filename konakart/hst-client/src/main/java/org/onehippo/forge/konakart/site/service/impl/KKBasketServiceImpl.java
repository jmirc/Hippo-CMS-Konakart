package org.onehippo.forge.konakart.site.service.impl;

import com.konakart.al.BasketMgr;
import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.app.Basket;
import com.konakart.app.KKException;
import com.konakart.app.WishListItem;
import com.konakart.appif.*;
import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.site.service.KKBasketService;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KKBasketServiceImpl extends KKBaseServiceImpl implements KKBasketService {


    @Override
    public boolean displayPriceWithTax(@Nonnull HstRequest request) {
        return getKKAppEng(request).displayPriceWithTax();
    }

    @Override
    public String getBasketTotal(@Nonnull HstRequest request) {

        try {
            return getKKAppEng(request).getBasketMgr().getBasketTotal();
        } catch (KKException e) {
            log.warn("Failed to retrieve the total price", e);
        } catch (KKAppException e) {
            log.warn("Failed to retrieve the total price", e);
        }

        return "";
    }

    @Override
    public BasketMgr getBasketMgr(@Nonnull HstRequest request) {
        return getKKAppEng(request).getBasketMgr();
    }

    @Override
    public int getNumberOfITems(@Nonnull HstRequest request) {

        CustomerIf customerIf = KKServiceHelper.getKKCustomerService().getCurrentCustomer(request);

        BasketIf[] baskets = customerIf.getBasketItems();

        if (baskets != null) {
            return baskets.length;
        }

        return 0;
    }

    @Override
    public boolean addProductToBasket(@Nonnull KKAppEng kkAppEng, @Nonnull HstRequest request,
                                      int productId, @Nonnull OptionIf[] optionIfs, int quantity) {

        // Get the product from its Id
        try {
            kkAppEng.getProductMgr().fetchSelectedProduct(productId);
            ProductIf selectedProd = kkAppEng.getProductMgr().getSelectedProduct();

            if (selectedProd == null) {
                return false;
            }

            /*
            * Create a basket item. Only the product id is required to save the basket item. Note
            * that the array of options may be null.
            */
            BasketIf b = new Basket();
            b.setQuantity(quantity);
            b.setOpts(optionIfs);
            b.setProductId(selectedProd.getId());

            kkAppEng.getBasketMgr().addToBasket(b, /* refresh */true);

            return true;

        } catch (KKException e) {
            log.warn("Failed to add the product with the id {} to the basket - {} ", productId, e.toString());
        } catch (KKAppException e) {
            log.warn("Failed to add the product with the id {} to the basket - {} ", productId, e.toString());
        }

        return false;
    }

    @Override
    public boolean addProductToWishList(@Nonnull KKAppEng kkAppEng, @Nonnull HstRequest request, int wishListId,
                                        int productId, @Nullable OptionIf[] optionIfs, int quantity) {
        try {
            // Add an item to the customer's wish list
            if (KKServiceHelper.getKKCustomerService().wishListEnabled(request)) {
                WishListItemIf wli = new WishListItem();

                if (optionIfs != null) {
                    wli.setOpts(optionIfs);
                }

                wli.setProductId(productId);

                // WishListId defaults to -1 to pick up default wish list or create a new one
                wli.setWishListId(wishListId);
                // Medium priority
                wli.setPriority(3);
                // Quantity = 1
                wli.setQuantityDesired(quantity);
                // Add the item
                kkAppEng.getWishListMgr().addToWishList(wli);
                // Refresh the customer's wish list
                kkAppEng.getWishListMgr().fetchCustomersWishLists();

                return true;
            }
        } catch (KKException e) {
            log.warn("Failed to add the product with the id {} to the wishlist - {} ", productId, e.toString());
        } catch (KKAppException e) {
            log.warn("Failed to add the product with the id {} to the wishlist - {} ", productId, e.toString());
        }

        return false;

    }

    @Override
    public void removeProductToWishList(@Nonnull KKAppEng kkAppEng, @Nonnull HstRequest request, int wishListId, int productId) {

        try {
            // Remove an item from the customer's wish list
            if (KKServiceHelper.getKKCustomerService().wishListEnabled(request)) {
                WishListItemIf wli = new WishListItem();
                wli.setWishListId(wishListId);
                wli.setProductId(productId);

                kkAppEng.getWishListMgr().removeFromWishList(wli);

                // Refresh the customer's wish list
                kkAppEng.getWishListMgr().fetchCustomersWishLists();
            }
        } catch (KKException e) {
            log.warn("Failed to add the product with the id {} to the wishlist - {} ", productId, e.toString());
        } catch (KKAppException e) {
            log.warn("Failed to add the product with the id {} to the wishlist - {} ", productId, e.toString());
        }
    }

    @Override
    public boolean checkProductInWishList(@Nonnull KKAppEng kkAppEng, @Nonnull HstRequest request, int wishListId, int productId) {

        try {
            // Remove an item from the customer's wish list
            if (KKServiceHelper.getKKCustomerService().wishListEnabled(request)) {
                WishListIf wli = kkAppEng.getWishListMgr().fetchWishList(wishListId);

                WishListItemIf[] items = wli.getWishListItems();

                for (WishListItemIf item : items) {
                    if (item.getProduct().getId() == productId) {
                        return true;
                    }
                }

            }
        } catch (KKException e) {
            log.warn("Failed to add the product with the id {} to the wishlist - {} ", productId, e.toString());
        }

        return false;
    }
}
