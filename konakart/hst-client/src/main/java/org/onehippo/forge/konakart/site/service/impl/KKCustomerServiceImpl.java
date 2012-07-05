package org.onehippo.forge.konakart.site.service.impl;

import com.konakart.al.KKAppEng;
import com.konakart.al.WishListMgr;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.WishListIf;
import com.konakart.bl.ConfigConstants;
import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.site.service.KKCustomerService;

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


}
