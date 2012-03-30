package org.onehippo.forge.konakart.hst.components;

import com.konakart.appif.BasketIf;
import com.konakart.appif.CustomerIf;
import com.konakart.appif.CustomerRegistrationIf;
import com.konakart.bl.ConfigConstants;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormField;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.component.support.forms.FormUtils;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.linking.HstLink;
import org.hippoecm.hst.core.linking.HstLinkCreator;
import org.hippoecm.hst.util.HstResponseUtils;
import org.onehippo.forge.konakart.hst.utils.KKCustomerEventMgr;

import java.util.ResourceBundle;

/**
 * This component is used to manage the checkout process.
 */
public abstract class KKCheckout extends KKHstActionComponent {

    private static final String ONE_PAGE_CHECKOUT = "onePageCheckout";

    protected static final String STATE = "STATE";

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

            // retrieve the state
            String currentState = request.getParameter(STATE);

            String nextState = getNextState(currentState);

            doBeforeRender(nextState, formMap, request, response);

            // Set the current state
            request.setAttribute(STATE, nextState);

            request.setAttribute(ONE_PAGE_CHECKOUT, isOnePageCheckout());

        } catch (Exception e) {
            log.warn("Failed to initialize the checkout page - {}", e.toString());
        }
    }

    @Override
    public final void doAction(String action, HstRequest request, HstResponse response) {

        ResourceBundle bundle = ResourceBundle.getBundle("messages", request.getLocale());

        // should logged-in
        FormMap formMap = new FormMap(request, getCheckoutFormMapFields());

        // check required fields
        if (doValidForm(action, formMap, bundle)) {
            doAction(action, formMap, request, response);
        }

        FormUtils.persistFormMap(request, response, formMap, null);
    }

    /**
     * This method could be overrides to apply business rules for a specific state
     * @param nextState the next state
     * @param formMap the formMap
     * @param request the Hst Request
     * @param response the Hst Response
     */
    protected abstract void doBeforeRender(String nextState, FormMap formMap,
                                           HstRequest request, HstResponse response);

    /**
     * This method could be overrides to apply business rules for a specific state
     * @param action the action
     * @param formMap the fromMap
     * @param request the Hst Request
     * @param response the Hst Response
     */
    protected abstract void doAction(String action, FormMap formMap, HstRequest request, HstResponse response);

    /**
     * This method is used to set the next state based on the current state
     * @param currentState the current state
     *
     * @return the next state
     */
    protected abstract String getNextState(String currentState);

    /**
     * This method must be overiddes to validate the one checkout form
     *
     * @param currentState the current state
     * @param formMap the form map
     * @param bundle the resource bundle
     * @return true if the form is valid, false otherwise
     */
    protected abstract boolean doValidForm(String currentState, FormMap formMap, ResourceBundle bundle);

    /**
     * Create a customer registration from the onecheckout form
     * @param formMap the form
     * @return a created customer registration
     */
    protected abstract CustomerRegistrationIf createCustomerRegistration(FormMap formMap);

    /**
     * This method must be overrided if you implement a different onepagecheckout
     * @return the onePageCheckout formMapFields
     */
    protected abstract String[] getCheckoutFormMapFields();

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
     * This is an helper class to redirect the customer to another page
     *
     * @param request the HstRequest
     * @param response the HstResponse
     * @param refId the refId
     */
    protected void redirectByRefId(HstRequest request, HstResponse response, String refId) {

        HstLinkCreator linkCreator = request.getRequestContext().getHstLinkCreator();

        HstLink link = linkCreator.createByRefId(refId, request.getRequestContext().getResolvedMount().getMount());

        HstResponseUtils.sendRedirectOrForward(request, response, link.getPath());
    }

    /**
     * Check if a mandatory field is empty or not.
     *
     * @param formMap the formMap
     * @param fieldName the field name to check
     * @param errorMessage the error message
     * @return true if a mandatory is empty, false otherwise.
     */
    protected boolean checkMandatoryField(FormMap formMap, String fieldName, String errorMessage) {
        FormField formField = formMap.getField(fieldName);
        if ((formField == null) || StringUtils.isBlank(formField.getValue())) {
            formMap.addMessage(fieldName, errorMessage);
            return false;
        }

        return true;
    }


}
