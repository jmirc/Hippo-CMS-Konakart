package org.onehippo.forge.konakart.hst.components;

import com.konakart.appif.BasketIf;
import com.konakart.appif.CustomerIf;
import com.konakart.bl.ConfigConstants;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.component.support.forms.FormUtils;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.container.ComponentManager;
import org.hippoecm.hst.site.HstServices;
import org.onehippo.forge.konakart.hst.utils.KKCustomerEventMgr;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.Processor;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutSeedData;
import org.onehippo.forge.konakart.hst.wizard.checkout.activity.BaseCheckoutActivity;

/**
 * This component is used to manage the checkout process.
 */
public abstract class KKCheckout extends KKHstActionComponent {

    private static final String ONE_PAGE_CHECKOUT = "onePageCheckout";
    private static final String CHECKOUT_ORDER = "checkoutOrder";

    protected static final String STATE = "state";



    @Override
    public final void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        FormMap formMap = new FormMap();
        FormUtils.populate(request, formMap);
        request.setAttribute("form", formMap);

        try {
            // Update the basket data from the database
            kkAppEng.getBasketMgr().getBasketItemsPerCustomer();

            // Check to see whether there is something in the cart
            CustomerIf cust = kkAppEng.getCustomerMgr().getCurrentCustomer();
            if (cust.getBasketItems() == null || cust.getBasketItems().length == 0) {
                redirectByRefId(request, response, getDetailCartRefId());
                return;
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
                    redirectByRefId(request, response, getDetailCartRefId());
                    return;
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
                        redirectByRefId(request, response, getDetailCartRefId());
                        return;
                    }
                }
            }

            CheckoutSeedData seedData = new CheckoutSeedData();
            seedData.setKkHstComponent(this);
            seedData.setRequest(request);
            seedData.setResponse(response);
            seedData.setState(getCurrentState(request));

            Processor processor = getProcessor();
            processor.doBeforeRender(seedData);

            request.setAttribute(STATE, processor.computeNextState(seedData));

            request.setAttribute(CHECKOUT_ORDER, kkAppEng.getOrderMgr().getCheckoutOrder());

            request.setAttribute(ONE_PAGE_CHECKOUT, isOnePageCheckout());

        } catch (Exception e) {
            log.warn("Failed to initialize the checkout page - {}", e.toString());
        }
    }

    @Override
    public final void doAction(String action, HstRequest request, HstResponse response) {

        CheckoutSeedData seedData = new CheckoutSeedData();
        seedData.setKkHstComponent(this);
        seedData.setRequest(request);
        seedData.setResponse(response);
        seedData.setAction(action);
        seedData.setState(getCurrentState(request));

        Processor processor = getProcessor();

        try {
            FormMap formMap = processor.doAction(seedData);
            FormUtils.persistFormMap(request, response, formMap, null);
        } catch (ActivityException e) {
            log.error("Unable to call doAction on the processor", e);
        }
    }




    /**
     * This method is used to set the next state based on the current state
     *
     * @param request the Hst Request
     * @return the next state
     */
    protected String initializeNextState(HstRequest request) {

        BaseCheckoutActivity.STATES nextState = BaseCheckoutActivity.STATES.INITIAL;

        String currentState = request.getParameter(STATE);


        if (StringUtils.isEmpty(currentState)) {

            // Insert event
            eventMgr.insertCustomerEvent(kkAppEng, KKCustomerEventMgr.ACTION_ENTER_CHECKOUT);

            if (!isGuestCustomer()) {
                nextState = BaseCheckoutActivity.STATES.BILLING_ADDRESS;
            } else {
                nextState = BaseCheckoutActivity.STATES.INITIAL;
            }
        } else {
            if (currentState.equals(BaseCheckoutActivity.STATES.INITIAL.name()) && !isGuestCustomer()) {
                nextState = BaseCheckoutActivity.STATES.BILLING_ADDRESS;
            }

            if (currentState.equals(BaseCheckoutActivity.STATES.CHECKOUT_METHOD_REGISTER.name())) {
                nextState = BaseCheckoutActivity.STATES.BILLING_ADDRESS;
            }

            if (currentState.equals(BaseCheckoutActivity.STATES.BILLING_ADDRESS.name())) {
                request.setAttribute(BaseCheckoutActivity.STATES.BILLING_ADDRESS.name().concat("_EDIT"), true);
                nextState = BaseCheckoutActivity.STATES.SHIPPING_ADDRESS;
            }

            if (currentState.equals(BaseCheckoutActivity.STATES.SHIPPING_ADDRESS.name())) {
                request.setAttribute(BaseCheckoutActivity.STATES.BILLING_ADDRESS.name().concat("_EDIT"), true);
                request.setAttribute(BaseCheckoutActivity.STATES.SHIPPING_ADDRESS.name().concat("_EDIT"), true);
                nextState = BaseCheckoutActivity.STATES.SHIPPING_METHOD;
            }

            if (currentState.equals(BaseCheckoutActivity.STATES.SHIPPING_METHOD.name())) {
                request.setAttribute(BaseCheckoutActivity.STATES.BILLING_ADDRESS.name().concat("_EDIT"), true);
                request.setAttribute(BaseCheckoutActivity.STATES.SHIPPING_ADDRESS.name().concat("_EDIT"), true);
                request.setAttribute(BaseCheckoutActivity.STATES.SHIPPING_METHOD.name().concat("_EDIT"), true);
                nextState = BaseCheckoutActivity.STATES.PAYMENT_METHOD;
            }

            if (currentState.equals(BaseCheckoutActivity.STATES.PAYMENT_METHOD.name())) {
                request.setAttribute(BaseCheckoutActivity.STATES.BILLING_ADDRESS.name().concat("_EDIT"), true);
                request.setAttribute(BaseCheckoutActivity.STATES.SHIPPING_ADDRESS.name().concat("_EDIT"), true);
                request.setAttribute(BaseCheckoutActivity.STATES.SHIPPING_METHOD.name().concat("_EDIT"), true);
                request.setAttribute(BaseCheckoutActivity.STATES.PAYMENT_METHOD.name().concat("_EDIT"), true);
                nextState = BaseCheckoutActivity.STATES.ORDER_REVIEW;
            }
        }

        request.setAttribute(STATE, nextState.name());

        return nextState.name();

    }

    /**
     * Returns true if configured for one page checkout
     *
     * @return Returns true if configured for one page checkout
     */
    protected boolean isOnePageCheckout() {
        // Check to see whether one page checkout is configured
        String onePageCheckout = kkAppEng.getConfig(ConfigConstants.ONE_PAGE_CHECKOUT);

        return onePageCheckout != null && onePageCheckout.equalsIgnoreCase("true");
    }

    /**
     * @param request the Hst Request
     * @return the current state
     */
    private String getCurrentState(HstRequest request) {
        return KKUtil.getEscapedParameter(request, STATE);
    }

    /**
     * @return the checkout processor name
     */
    protected abstract String getProcessorName();

    /**
     * Retrieve the processor associated with the checkout
     * @return the processor
     */
    private Processor getProcessor() {

        // Check if the user can access to this host
        ComponentManager componentManager = HstServices.getComponentManager();

        if (componentManager == null) {
            log.error("Component Manager is null!!!!!! WE HAVE A BIG ISSUE");

            return null;
        }

        return componentManager.getComponent(getProcessorName());
    }


}
