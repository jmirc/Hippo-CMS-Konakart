package org.onehippo.forge.konakart.site.service;

import com.konakart.al.BasketMgr;
import com.konakart.al.KKAppEng;
import com.konakart.appif.OptionIf;
import org.hippoecm.hst.core.component.HstRequest;

import javax.annotation.Nonnull;

public interface KKBasketService {

    /**
     * Called to determine whether to display prices with tax.
     *
     * @return Return true if we should display prices with tax
     */
    boolean displayPriceWithTax(@Nonnull HstRequest request);

    /**
     * Returns the total price of the basket as a formatted string
     *
     * @param request the Hst request
     * @return Return total value of basket already formatted
     */
    String getBasketTotal(@Nonnull HstRequest request);

    /**
     * Returns the number of items into the basket
     *
     * @param request the Hst request
     * @return Returns the number of items into the basket
     */
    int getNumberOfITems(@Nonnull HstRequest request);

    /**
     * Used to get an instance of the BasketMgr.
     *
     * @param request the hst request
     * @return Return the basketMgr.
     */
    BasketMgr getBasketMgr(@Nonnull HstRequest request);

    /**
     * Add the product to the basket
     *
     * @param kkAppEng  the konakart engine
     * @param request   the hst request
     * @param prodId    id of the product to add
     * @param optionIfs list of selected options associated with the product
     * @return true if the product has been added, false otherwise
     */
    boolean addProductToBasket(@Nonnull KKAppEng kkAppEng, @Nonnull HstRequest request, int prodId,
                               @Nonnull OptionIf[] optionIfs, int quantity);


    /**
     * Add a product to a wish list
     *
     * @param kkAppEng   the konakart engine
     * @param request    the hst request
     * @param wishListId id of the wishList on which the product will be added
     * @param productId  id of the product to add
     * @param optionIfs  list of selected options associated with the product
     * @return true if the product has been added, false otherwise
     */
    public boolean addProductToWishList(@Nonnull KKAppEng kkAppEng, @Nonnull HstRequest request, int wishListId,
                                        int productId, @Nonnull OptionIf[] optionIfs, int quantity);

    /**
     * remove a product from a wish list
     *
     * @param kkAppEng   the konakart engine
     * @param request    the hst request
     * @param wishListId id of the wishList on which the product will be removed
     * @param productId  id of the product to remove
     */
    public void removeProductToWishList(@Nonnull KKAppEng kkAppEng, @Nonnull HstRequest request, int wishListId,
                                        int productId);

    /**
     * check if a wish list contains a specific product id
     *
     * @param kkAppEng   the konakart engine
     * @param request    the hst request
     * @param wishListId id of the wishList
     * @param productId  id of the product to check
     * @return true if the wish list contains the product id, false otherwise
     */
    public boolean checkProductInWishList(@Nonnull KKAppEng kkAppEng, @Nonnull HstRequest request, int wishListId,
                                        int productId);

}
