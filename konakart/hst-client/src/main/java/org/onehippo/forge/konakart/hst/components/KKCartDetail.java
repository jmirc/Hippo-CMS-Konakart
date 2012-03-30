package org.onehippo.forge.konakart.hst.components;

import com.konakart.al.KKAppException;
import com.konakart.app.CreateOrderOptions;
import com.konakart.app.KKException;
import com.konakart.app.Option;
import com.konakart.appif.*;
import com.konakart.bl.ConfigConstants;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormField;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.beans.KKProductDocument;
import org.onehippo.forge.konakart.hst.utils.KKCustomerEventMgr;
import org.onehippo.forge.konakart.hst.vo.CartItem;
import org.onehippo.forge.konakart.hst.vo.OrderItem;

import java.util.LinkedList;
import java.util.List;

/**
 * This component is used to manage the detail cart page. You will be able to update quantity, to remove product, etc..
 * <p/>
 * You need to define a refid="cartDetailId" on the sitemap associated to the mycart.
 */
public class KKCartDetail extends KKHstActionComponent {

    /**
     * This action is used to add a update cart
     */
    private static final String UPDATE_ACTION = "update";


    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        /*
             * If the current customer has items in his basket, then we have to create a list of
             * CartItem objects and populate them since these are the objects that we will use to
             * display the basket items on the screen.
             */
        CustomerIf currentCustomer = getCurrentCustomer();

        // Initialize this variable to true if one of the item is set to out of stock.
        boolean isItemOutOfStock = false;

        try {
            if (getCurrentCustomer() != null && currentCustomer.getBasketItems() != null && currentCustomer.getBasketItems().length > 0) {

                // We update the basket with the quantities in stock
                BasketIf[] items = kkAppEng.getEng().updateBasketWithStockInfoWithOptions(
                        currentCustomer.getBasketItems(),
                        kkAppEng.getBasketMgr().getAddToBasketOptions());

                /*
                * Create a temporary order to get order totals that we can display in the edit cart
                * screen. Comment this out if you don't want to show extra information such as
                * shipping and discounts before checkout.
                */
                createTempOrder(getCurrentCustomer().getId(), items);

                // Retrieve the checkout order
                OrderIf order = kkAppEng.getOrderMgr().getCheckoutOrder();

                String coupon = kkAppEng.getOrderMgr().getCouponCode();
                String giftCertCode = kkAppEng.getOrderMgr().getGiftCertCode();
                int rewardPoints = kkAppEng.getOrderMgr().getRewardPoints();

                // Set the coupon code from the one saved in the order manager
                if (coupon != null) {
                    request.setAttribute("couponCode", coupon);
                }
                // Set the GiftCert code from the one saved in the order manager
                if (giftCertCode != null) {
                    request.setAttribute("giftCertCode", giftCertCode);
                }

                // Set the reward points from the ones saved in the order manager
                if (rewardPoints != 0) {
                    request.setAttribute("rewardPoints", rewardPoints);
                }

                if (order != null) {

                    OrderTotalIf[] orderTotalIfs = order.getOrderTotals();

                    if (orderTotalIfs != null && orderTotalIfs.length > 0) {

                        OrderItem[] orderItems = new OrderItem[orderTotalIfs.length];

                        for (int i = 0; i < orderTotalIfs.length; i++) {
                            OrderTotalIf orderTotalIf = orderTotalIfs[i];

                            OrderItem orderItem = new OrderItem();
                            orderItem.setTitle(orderTotalIf.getTitle());

                            if (orderTotalIf.getClassName().equals("ot_reward_points")) {
                                orderItem.setValue(orderTotalIf.getValue().toString());
                            } else if (orderTotalIf.getClassName().equals("ot_free_product")) {
                                orderItem.setValue(orderTotalIf.getText());
                            } else {
                                try {
                                    orderItem.setValue(kkAppEng.formatPrice(orderTotalIf.getValue()));
                                } catch (KKAppException e) {
                                    log.error("Unable to convert the price for the order total - " + orderTotalIf.getId()
                                            + " - value " + orderTotalIf.getValue());
                                }
                            }

                            orderItems[i] = orderItem;
                        }

                        request.setAttribute("orderTotals", orderItems);
                    }

                    order.setCouponCode(coupon);
                    order.setGiftCertCode(giftCertCode);
                    order.setPointsRedeemed(rewardPoints);
                }


                // Fill the CartItem list
                List<CartItem> cartItems = new LinkedList<CartItem>();
                request.setAttribute("cartitems", cartItems);

                for (BasketIf b : items) {
                    if (b != null && b.getProduct() != null) {
                        CartItem item = new CartItem(b.getId(), b.getProduct().getId(), b
                                .getProduct().getName(), b.getQuantity(),
                                b.getQuantityInStock());

                        // Generate the image link
                        KKProductDocument productDocument = getProductDocumentById(request, b.getProductId());

                        item.setProductDocument(productDocument);


                        if (!item.getInStock()) {
                            isItemOutOfStock = true;
                        }

                        if (kkAppEng.displayPriceWithTax()) {
                            try {
                                item.setTotalPrice(kkAppEng.formatPrice(b.getFinalPriceIncTax()));
                            } catch (KKAppException e) {
                                // to nothing
                            }
                        } else {
                            try {
                                item.setTotalPrice(kkAppEng.formatPrice(b.getFinalPriceExTax()));
                            } catch (KKAppException e) {
                                // do nothing
                            }
                        }

                        // Set the options of the new CartItem
                        if (b.getOpts() != null && b.getOpts().length > 0) {
                            String[] optNameArray = new String[b.getOpts().length];
                            for (int j = 0; j < b.getOpts().length; j++) {
                                if (b.getOpts()[j] != null) {
                                    if (b.getOpts()[j].getType() == Option.TYPE_VARIABLE_QUANTITY) {
                                        optNameArray[j] = b.getOpts()[j].getName() + " "
                                                + b.getOpts()[j].getQuantity() + " "
                                                + b.getOpts()[j].getValue();
                                    } else {
                                        optNameArray[j] = b.getOpts()[j].getName() + " "
                                                + b.getOpts()[j].getValue();
                                    }
                                } else {
                                    optNameArray[j] = "";
                                }
                            }
                            item.setOptNameArray(optNameArray);
                        }


                        cartItems.add(item);
                    }
                }
            }
        } catch (KKException e) {
            log.warn("Unable to display the cart", e);
        }

        // Display or not the coupon entry
        request.setAttribute("displayCouponEntry", kkAppEng.getConfigAsBoolean(ConfigConstants.DISPLAY_COUPON_ENTRY, false));

        // Display or not the gift certificate entry
        request.setAttribute("displayGiftCertEntry", kkAppEng.getConfigAsBoolean(ConfigConstants.DISPLAY_GIFT_CERT_ENTRY, false));

        // Set the stock information
        request.setAttribute("stockCheck", kkAppEng.getConfigAsBoolean(ConfigConstants.STOCK_CHECK, true));
        request.setAttribute("itemOutOfStock", isItemOutOfStock);
        request.setAttribute("stockAllowCheckout", kkAppEng.getConfigAsBoolean(ConfigConstants.STOCK_ALLOW_CHECKOUT, true));
    }

    @Override
    public void doAction(String action, HstRequest request, HstResponse response) {

        // We need to find the Basket object corresponding to the cartItem object and we remove it or
        // update it if required.

        if (StringUtils.equals(action, UPDATE_ACTION)) {
            // basket items
            BasketIf[] basketItems = kkAppEng.getCustomerMgr().getCurrentCustomer().getBasketItems();

            String[] definedFormFields = new String[2 + basketItems.length * 2];

            int i = 0;

            definedFormFields[i++] = "couponCode";
            definedFormFields[i++] = "giftCertCode";

            for (BasketIf basketItem : basketItems) {
                definedFormFields[i] = "quantity_" + basketItem.getId();
                i++;
                definedFormFields[i] = "remove_" + basketItem.getId();
                i++;
            }

            FormMap formMap = new FormMap(request, definedFormFields);

            for (BasketIf basketItem : basketItems) {
                FormField removeFormField = formMap.getField("remove_" + basketItem.getId());
                FormField quantityFormField = formMap.getField("quantity_" + basketItem.getId());

                // Remove the basket item
                if (removeFormField != null && removeFormField.getValues() != null
                        && removeFormField.getValues().size() > 0) {
                    // remove the basket item
                    try {
                        kkAppEng.getBasketMgr().removeFromBasket(basketItem, /** refresh **/false);

                        // insert an event
                        eventMgr.insertCustomerEvent(kkAppEng, KKCustomerEventMgr.ACTION_REMOVE_FROM_CART,
                                basketItem.getProductId());
                    } catch (Exception e) {
                        log.error("Unable to remove the basket with the id - " + basketItem.getId());
                    }
                }

                // Update the quantity
                if (quantityFormField != null) {
                    int quantity = Integer.parseInt(quantityFormField.getValue());

                    // Remove from the basket if quantity is set to 0
                    if (quantity == 0) {
                        // remove the basket item
                        try {
                            kkAppEng.getBasketMgr().removeFromBasket(basketItem, /** refresh **/false);

                            // insert an event
                            eventMgr.insertCustomerEvent(kkAppEng, KKCustomerEventMgr.ACTION_REMOVE_FROM_CART,
                                    basketItem.getProductId());
                        } catch (Exception e) {
                            log.error("Unable to remove the basket with the id - " + basketItem.getId());
                        }
                    }


                    if (quantity != basketItem.getQuantity()) {
                        basketItem.setQuantity(quantity);
                        try {
                            kkAppEng.getBasketMgr().updateBasket(basketItem, /* refresh */false);
                        } catch (Exception e) {
                            log.error("Unable to update quantity for the basket with the id - " + basketItem.getId());
                        }
                    }
                }
            }

            // Retrieve the coupon information
            FormField couponField = formMap.getField("couponCode");

            if (couponField != null) {
                kkAppEng.getOrderMgr().setCouponCode(couponField.getValue());
            } else {
                kkAppEng.getOrderMgr().setCouponCode(null);
            }


            // Update the basket data
            try {
                kkAppEng.getBasketMgr().getBasketItemsPerCustomer();
            } catch (Exception e) {
                log.error("Unable to update the basket - {}", e.toString());
            }
        }


    }

    /*
    * Populate checkout order with a temporary order created before the checkout process really
    * begins. If the customer hasn't registered or logged in yet, we use the default customer to
    * create the order.
    *
    * With this temporary order we can give the customer useful information on shipping costs and
    * discounts without him having to login.
    */
    private OrderIf createTempOrder(int custId, BasketIf[] items) {
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
