package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import com.konakart.al.KKAppException;
import com.konakart.app.KKException;
import com.konakart.appif.BasketIf;
import com.konakart.appif.CustomerIf;
import com.konakart.bl.ConfigConstants;
import org.onehippo.forge.konakart.hst.wizard.BaseActivity;

public abstract class BaseCheckoutActivity extends BaseActivity {

    /**
     * This method is used to validate the cart before going foward with the checkout process
     */
    protected boolean validateCurrentCart() {

        try {
            // Update the basket data from the database
            kkAppEng.getBasketMgr().getBasketItemsPerCustomer();

            // Check to see whether there is something in the cart
            CustomerIf cust = kkAppEng.getCustomerMgr().getCurrentCustomer();
            if (cust.getBasketItems() == null || cust.getBasketItems().length == 0) {
                redirectToCartDetail();
                return false;
            }

            // Check that all cart items have a quantity of at least one
            boolean removed = false;
            for (int i = 0; i < cust.getBasketItems().length; i++) {
                BasketIf b = cust.getBasketItems()[i];
                if (b.getQuantity() == 0) {
                    kkAppEng.getBasketMgr().removeFromBasket(b, /* refresh */false);
                    removed = true;
                }
            }

            if (removed) {
                // Update the basket data from the database
                kkAppEng.getBasketMgr().getBasketItemsPerCustomer();

                // Check to see whether there is something in the cart
                if (cust.getBasketItems() == null || cust.getBasketItems().length == 0) {
                    redirectToCartDetail();
                    return false;
                }
            }

            // Check to see whether we are trying to checkout an item that isn't in stock
            String stockAllowCheckout = kkAppEng.getConfig(ConfigConstants.STOCK_ALLOW_CHECKOUT);
            if (stockAllowCheckout != null && stockAllowCheckout.equalsIgnoreCase("false")) {
                // If required, we check to see whether the products are in stock
                BasketIf[] items = kkAppEng.getEng().updateBasketWithStockInfoWithOptions(
                        cust.getBasketItems(), kkAppEng.getBasketMgr().getAddToBasketOptions());

                for (BasketIf basket : items) {
                    if (basket.getQuantity() > basket.getQuantityInStock()) {
                        redirectToCartDetail();
                        return false;
                    }
                }
            }

            return true;
        } catch (KKException e) {
            log.warn("Cart has been invalidated", e);
        } catch (KKAppException e) {
            log.warn("Cart has been invalidated", e);
        }

        return false;
    }

    protected void redirectToCartDetail() {
        processorContext.getSeedData().getKkBaseHstComponent().redirectByRefId(hstRequest, hstResponse,
                processorContext.getSeedData().getKkBaseHstComponent().getCartDetailRefId());
    }

}
