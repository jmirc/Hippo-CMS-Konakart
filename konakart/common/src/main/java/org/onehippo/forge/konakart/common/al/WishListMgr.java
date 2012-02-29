package org.onehippo.forge.konakart.common.al;

import com.konakart.app.AddToWishListOptions;
import com.konakart.app.CustomerSearch;
import com.konakart.app.KKException;
import com.konakart.app.WishList;
import com.konakart.appif.AddToWishListOptionsIf;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.WishListIf;
import com.konakart.appif.WishListsIf;
import com.konakart.bl.ConfigConstants;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.common.engine.KKEngine;

/**
 * Contains methods to manage Wish Lists
 */
public class WishListMgr extends BaseMgr {

    /**
     * Default constuctor
     *
     * @param kkEngine the Konakart Engine
     */
    public WishListMgr(KKEngine kkEngine) {
        super(kkEngine);
    }

    /**
     * @return Contains methods to manage Wish Lists
     */
    public boolean allowWishListWhenNotLoggedIn() {
        String enableWishlist = kkEngine.getConfig(ConfigConstants.ALLOW_WISHLIST_WHEN_NOT_LOGGED_IN);

        return StringUtils.isNotEmpty(enableWishlist) && (enableWishlist.equalsIgnoreCase("true"));
    }

    /**
     * Get the wish list for a customer and language
     * and set them on the customer object of the customerMgr.
     *
     * @throws com.konakart.app.KKException thrown if the wish list is not enabled.
     */
    public void fetchCustomersWishLists() throws KKException {
        checkEnabled();

        CustomerIf currentCustomer = kkEngine.getCustomerMgr().getCurrentCustomer();


        CustomerSearch localCustomerSearch = null;

        if (currentCustomer != null) {

            boolean isGuestCustomer = currentCustomer.getId() < 0;

            // If the customer is not null, and it is not logged-in (Guest customer)
            if (isGuestCustomer && allowWishListWhenNotLoggedIn()) {
                localCustomerSearch = new CustomerSearch();
                localCustomerSearch.setTmpId(kkEngine.getCustomerMgr().getCurrentCustomer().getId());
            }

            WishListsIf localWishListsIf = kkEng.searchForWishLists(kkEngine.getSessionId(), null, localCustomerSearch);

            if ((localWishListsIf != null) && (localWishListsIf.getWishListArray() != null)) {

                WishListIf[] arrayOfWishList = new WishList[localWishListsIf.getWishListArray().length];

                for (int i = 0; i < localWishListsIf.getWishListArray().length; i++) {
                    WishListIf localWishListIf = localWishListsIf.getWishListArray()[i];
                    localWishListIf = kkEng.getWishListWithItemsWithOptions(kkEngine.getSessionId(),
                            localWishListIf.getId(), kkEngine.getLanguage().getId(),
                            getAddToWishListOptions(isGuestCustomer));

                    arrayOfWishList[i] = localWishListIf;
                }

                kkEngine.getCustomerMgr().getCurrentCustomer().setWishLists(arrayOfWishList);
            }
        }
    }

    /**
     * @param isGuestCustomer true if the customer is a guest, false otherwise
     * @return the wish list options
     */
    private AddToWishListOptionsIf getAddToWishListOptions(boolean isGuestCustomer) {

        AddToWishListOptions wishListOptions = new AddToWishListOptions();

        if (!StringUtils.isEmpty(kkEngine.getCatalogId())) {
            wishListOptions.setCatalogId(kkEngine.getCatalogId());
            wishListOptions.setUseExternalPrice(kkEngine.getCatalogId() != null);
        }

        if (isGuestCustomer) {
            wishListOptions.setCustomerId(kkEngine.getCustomerMgr().getCurrentCustomer().getId());
        }

        return wishListOptions;
    }


    /**
     * Check if the wish list is enabled
     *
     * @throws com.konakart.app.KKException thrown if the wish list is not enabled.
     */
    private void checkEnabled() throws KKException {
        if (isEnabled()) {
            return;
        }

        throw new KKException("The whish list is not enabled. Within the Store Configuration of the administration, " +
                " please check if Enable Wish List functionality is set to true or Enable Gift Registry functionality to true");

    }


    /**
     * @return true if the wish list is enabled, false otherwise
     */
    private boolean isEnabled() {

        String enableWishlist = kkEngine.getConfig(ConfigConstants.ENABLE_WISHLIST);

        if (StringUtils.isNotEmpty(enableWishlist) && (enableWishlist.equalsIgnoreCase("true"))) {
            return true;
        }

        enableWishlist = kkEngine.getConfig(ConfigConstants.ENABLE_WISHLIST);

        return StringUtils.isNotEmpty(enableWishlist) && (enableWishlist.equalsIgnoreCase("true"));
    }
}
