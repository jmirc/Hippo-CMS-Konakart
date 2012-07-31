package org.onehippo.forge.konakart.site.service.impl;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.al.WishListMgr;
import com.konakart.app.DataDescriptor;
import com.konakart.app.FetchProductOptions;
import com.konakart.app.KKException;
import com.konakart.app.WishList;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.ProductIf;
import com.konakart.appif.WishListIf;
import com.konakart.bl.ConfigConstants;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.site.service.KKCustomerService;
import org.onehippo.forge.konakart.site.service.KKTagsService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KKCustomerServiceImpl extends KKBaseServiceImpl implements KKCustomerService {

    @Override
    public CustomerIf getCurrentCustomer(@Nonnull HstRequest request) {
        return getKKAppEng(request).getCustomerMgr().getCurrentCustomer();
    }

    @Override
    public boolean isGuestCustomer(@Nonnull HstRequest request) {
        return getKKAppEng(request).getCustomerMgr().getCurrentCustomer().getId() < 0;
    }


    @Override
    public boolean wishListEnabled(@Nonnull HstRequest request) {

        KKAppEng kkAppEng = getKKAppEng(request);

        boolean wishListEnabled = kkAppEng.getConfigAsBoolean(ConfigConstants.ENABLE_WISHLIST, false);
        boolean allowWishListWhenNotLoggedIn = kkAppEng.getConfigAsBoolean(ConfigConstants.ALLOW_WISHLIST_WHEN_NOT_LOGGED_IN, false);

        boolean guestCustomer = isGuestCustomer(request);

        return wishListEnabled && (!guestCustomer || allowWishListWhenNotLoggedIn);
    }

    @Override
    @Nullable
    public WishListIf getDefaultWishList(@Nonnull HstRequest request) {

        KKAppEng kkAppEng = getKKAppEng(request);

        if (!wishListEnabled(request)) {
            return null;
        }

        WishListIf[] wishLists = kkAppEng.getCustomerMgr().getCurrentCustomer().getWishLists();

        // Try to load default wishlist
        if (wishLists == null) {
            try {
                kkAppEng.getWishListMgr().fetchCustomersWishLists();

                wishLists = kkAppEng.getCustomerMgr().getCurrentCustomer().getWishLists();

            } catch (Exception e) {
                log.warn("Failed to retrieve the wish lists", e);
            }
        }

        if (wishLists == null) {
            return null;
        }

        for (WishListIf wishList : wishLists) {
            if (wishList.getListType() == WishListMgr.WISH_LIST_TYPE) {
                return wishList;
            }
        }

        return null;
    }

    @Override
    public int createDefaultWishList(@Nonnull HstRequest request, String wishListName, boolean isPublic) {

        if (!wishListEnabled(request)) {
            log.warn("The wishlist feature has not been enabled.");
            return -1;
        }

        KKAppEng kkAppEng = getKKAppEng(request);

        WishListIf defaultWishList = new WishList();
        defaultWishList.setListType(WishListMgr.WISH_LIST_TYPE);
        defaultWishList.setName(wishListName);
        defaultWishList.setPublicWishList(isPublic);

        try {
            return kkAppEng.getWishListMgr().createWishList(defaultWishList);
        } catch (KKException e) {
            log.error("Failed to create the favorites wishlist", e);
        } catch (KKAppException e) {
            log.error("Failed to create the favorites wishlist", e);
        }


        return -1;
    }

    @Override
    public ProductIf[] getCustomerProductsViewed(@Nonnull HstRequest request) {

        KKAppEng kkAppEng = getKKAppEng(request);

        try {
            String productViewedTagValue = kkAppEng.getCustomerTagMgr().getCustomerTagValue(KKTagsService.TAG_PRODUCTS_VIEWED);

            if (StringUtils.isNotEmpty(productViewedTagValue)) {
                String[] ids = StringUtils.split(productViewedTagValue, ":");

                if (ids != null && ids.length > 0) {
                    int[] prodIdArray = new int[ids.length];

                    for (int i = 0; i < ids.length; i++) {
                          prodIdArray[i] = Integer.parseInt(ids[i]);
                    }

                    return kkAppEng.getEng().getProductsFromIdsWithOptions(kkAppEng.getSessionId(), new DataDescriptor(),
                            prodIdArray, kkAppEng.getLangId(), new FetchProductOptions());
                }
            }
        } catch (Exception e) {
            log.warn("Failed to use the Customer tags feature. Please check if this feature has been enabled.", e);
        }


        return new ProductIf[0];
    }
}
