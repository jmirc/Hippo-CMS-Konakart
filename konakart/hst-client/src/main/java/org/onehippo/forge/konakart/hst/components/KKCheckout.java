package org.onehippo.forge.konakart.hst.components;

import com.konakart.al.KKAppException;
import com.konakart.app.Address;
import com.konakart.app.CustomerRegistration;
import com.konakart.app.KKException;
import com.konakart.appif.*;
import com.konakart.bl.ConfigConstants;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormField;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.component.support.forms.FormUtils;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.joda.time.DateTime;
import org.onehippo.forge.konakart.hst.utils.KKCustomerEventMgr;
import org.onehippo.forge.konakart.hst.utils.KKUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.onehippo.forge.konakart.hst.utils.KKUtil.checkMandatoryField;

/**
 * This component is used to manage the checkout process.
 */
public abstract class KKCheckout extends KKHstActionComponent {

    private static final String ONE_PAGE_CHECKOUT = "onePageCheckout";
    private static final String CHECKOUT_ORDER = "checkoutOrder";

    private static final String COUNTRIES = "countries";
    private static final String PROVINCES = "provinces";

    private static final String ADDRESSES = "addresses";
    protected static final String STATE = "state";

    protected static enum STATES {
        INITIAL, CHECKOUT_METHOD_REGISTER, BILLING_ADDRESS, BILLING_ADDRESS_REGISTER,
        SHIPPING_ADDRESS, SHIPPING_ADDRESS_REGISTER, SHIPPING_METHOD, PAYMENT_METHOD, ORDER_REVIEW
    }

    protected static enum ACTIONS {
        EDIT, LOGIN, REGISTER, SELECT_ADDRESS
    }

    public static final String SELECT_SAME_SHIPPING_ADDRESS = "same";


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
            String nextState = initializeNextState(request);

            doBeforeRender(nextState, formMap, request, response);

            request.setAttribute(CHECKOUT_ORDER, kkAppEng.getOrderMgr().getCheckoutOrder());

            request.setAttribute(ONE_PAGE_CHECKOUT, isOnePageCheckout());

        } catch (Exception e) {
            log.warn("Failed to initialize the checkout page - {}", e.toString());
        }
    }

    @Override
    public final void doAction(String action, HstRequest request, HstResponse response) {


        String state = KKUtil.getEscapedParameter(request, STATE);

        ResourceBundle bundle = ResourceBundle.getBundle("messages", request.getLocale());

        // should logged-in
        FormMap formMap = new FormMap(request, getCheckoutFormMapFields());

        // check required fields
        if (doValidForm(action, state, formMap, bundle)) {
            doAction(action, state, formMap, request, response);
        }

        FormUtils.persistFormMap(request, response, formMap, null);
    }

    /**
     * This method could be overrides to apply business rules for a specific state
     *
     * @param nextState the next state
     * @param formMap   the formMap
     * @param request   the Hst Request
     * @param response  the Hst Response
     */
    protected void doBeforeRender(String nextState, FormMap formMap,
                                  HstRequest request, HstResponse response) {

        if (nextState.equals(STATES.BILLING_ADDRESS.name()) || nextState.equals(STATES.SHIPPING_ADDRESS.name())) {
            try {
                // fill the form.
                initializeFormMap(nextState, formMap, request, response);


                // Set the province if has been selected
                FormField countryField = formMap.getField("country");

                if (countryField != null) {
                    request.setAttribute(COUNTRIES, kkAppEng.getAllCountries());

                    int country = StringUtils.isNotBlank(countryField.getValue()) ? Integer.valueOf(countryField.getValue()) : -1;
                    ZoneIf[] zones = kkAppEng.getEng().getZonesPerCountry(country);

                    List<String> stateProvinces = new ArrayList<String>();

                    for (ZoneIf zone : zones) {
                        stateProvinces.add(zone.getZoneName());
                    }

                    request.setAttribute(PROVINCES, stateProvinces);
                }
            } catch (KKException e) {
                log.warn("Failed to initialize the checkout page - {}", e.toString());
            }
        }

        if (nextState.equals(STATES.BILLING_ADDRESS.name()) || nextState.equals(STATES.SHIPPING_ADDRESS.name())) {

            if (!isGuestCustomer()) {
                try {
                    kkAppEng.getCustomerMgr().populateCurrentCustomerAddresses(/* force */ false);

                    AddressIf[] addresses = kkAppEng.getCustomerMgr().getCurrentCustomer().getAddresses();

                    request.setAttribute(ADDRESSES, addresses);

                } catch (Exception e) {
                    log.warn("Failed to load customer addresses - {}", e.toString());
                }
            }

        }

    }

    /**
     * Initialize the formap with existing information.
     *
     * @param nextState the next state
     * @param formMap   the formMap
     * @param request   the Hst Request
     * @param response  the Hst Response
     */
    protected void initializeFormMap(String nextState, FormMap formMap, HstRequest request, HstResponse response) {


    }

    /**
     * This method could be overrides to apply business rules for a specific state
     *
     * @param action   the action
     * @param state    the state
     * @param formMap  the fromMap
     * @param request  the Hst Request
     * @param response the Hst Response
     */
    protected void doAction(String action, String state, FormMap formMap,
                            HstRequest request, HstResponse response) {

        ResourceBundle bundle = ResourceBundle.getBundle("messages", request.getLocale());

        if (action.equals(ACTIONS.LOGIN.name())) {

            String username = KKUtil.getEscapedParameter(request, "email");
            String password = KKUtil.getEscapedParameter(request, "password");

            if (!super.loggedIn(request, response, username, password)) {
                formMap.addMessage("email", bundle.getString("checkout.invalid.password"));
            } else {
                try {
                    // Create an order object that we will use for the checkout process
                    kkAppEng.getOrderMgr().createCheckoutOrder();

                    // Get shipping quotes from the engine
                    kkAppEng.getOrderMgr().createShippingQuotes();
                } catch (Exception e) {
                    log.error("A new Order could not be created", e);
                }
            }


        }

        if (action.equals(ACTIONS.SELECT_ADDRESS.name()) && state.equals(STATES.BILLING_ADDRESS.name())) {
            Integer addressId = Integer.valueOf(KKUtil.getEscapedParameter(request, "address"));
            String shippingAddress = KKUtil.getEscapedParameter(request, "shippingAddress");

            // Create a new address
            if (addressId == -1) {
                try {
                    addressId = kkAppEng.getCustomerMgr().addAddressToCustomer(createAddressForCustomer(formMap));
                } catch (Exception e) {
                    response.setRenderParameter(STATE, STATES.INITIAL.name());
                    formMap.addMessage("globalmessage", bundle.getString("checkout.failed.create.address"));
                    log.error("Failed to add the address", e);
                    return;
                }
            }

            kkAppEng.getOrderMgr().setCheckoutOrderBillingAddress(addressId);

            if (shippingAddress.equals(SELECT_SAME_SHIPPING_ADDRESS)) {
                try {
                    kkAppEng.getOrderMgr().setCheckoutOrderShippingAddress(addressId);

                    // Skip the SHIPPING ADDRESS step because the customer has decided to use the
                    // same billing address
                    state = STATES.SHIPPING_ADDRESS.name();
                } catch (KKException e) {
                    log.error("Failed to set the shipping address", e);
                }
            }
        } else if (action.equals(ACTIONS.SELECT_ADDRESS.name()) && state.equals(STATES.SHIPPING_ADDRESS.name())) {
            Integer addressId = Integer.valueOf(KKUtil.getEscapedParameter(request, "address"));

            // Ask for a new address
            if (addressId == -1) {
                try {
                    addressId = kkAppEng.getCustomerMgr().addAddressToCustomer(createAddressForCustomer(formMap));
                } catch (Exception e) {
                    log.error("Failed to add the address", e);
                    formMap.addMessage("globalmessage", bundle.getString("checkout.failed.create.address"));
                    return;
                }
            }

            try {
                kkAppEng.getOrderMgr().setCheckoutOrderShippingAddress(addressId);

                // Skip the SHIPPING ADDRESS step because the customer has decided to use the
                // same billing address
                state = STATES.SHIPPING_ADDRESS.name();
            } catch (KKException e) {
                log.error("Failed to set the shipping address", e);
            }
        }

        response.setRenderParameter(STATE, state);
    }


    /**
     * This method is used to set the next state based on the current state
     *
     * @param request the Hst Request
     * @return the next state
     */
    protected String initializeNextState(HstRequest request) {

        STATES nextState = STATES.INITIAL;

        String currentState = request.getParameter(STATE);


        if (StringUtils.isEmpty(currentState)) {

            // Insert event
            eventMgr.insertCustomerEvent(kkAppEng, KKCustomerEventMgr.ACTION_ENTER_CHECKOUT);

            if (!isGuestCustomer()) {
                nextState = STATES.BILLING_ADDRESS;
            } else {
                nextState = STATES.INITIAL;
            }
        } else {
            if (currentState.equals(STATES.INITIAL.name()) && !isGuestCustomer()) {
                nextState = STATES.BILLING_ADDRESS;
            }

            if (currentState.equals(STATES.CHECKOUT_METHOD_REGISTER.name())) {
                nextState = STATES.BILLING_ADDRESS;
            }

            if (currentState.equals(STATES.BILLING_ADDRESS.name())) {
                request.setAttribute(STATES.BILLING_ADDRESS.name().concat("_EDIT"), true);
                nextState = STATES.SHIPPING_ADDRESS;
            }

            if (currentState.equals(STATES.SHIPPING_ADDRESS.name())) {
                request.setAttribute(STATES.BILLING_ADDRESS.name().concat("_EDIT"), true);
                request.setAttribute(STATES.SHIPPING_ADDRESS.name().concat("_EDIT"), true);
                nextState = STATES.SHIPPING_METHOD;
            }

            if (currentState.equals(STATES.SHIPPING_METHOD.name())) {
                request.setAttribute(STATES.BILLING_ADDRESS.name().concat("_EDIT"), true);
                request.setAttribute(STATES.SHIPPING_ADDRESS.name().concat("_EDIT"), true);
                request.setAttribute(STATES.SHIPPING_METHOD.name().concat("_EDIT"), true);
                nextState = STATES.PAYMENT_METHOD;
            }

            if (currentState.equals(STATES.PAYMENT_METHOD.name())) {
                request.setAttribute(STATES.BILLING_ADDRESS.name().concat("_EDIT"), true);
                request.setAttribute(STATES.SHIPPING_ADDRESS.name().concat("_EDIT"), true);
                request.setAttribute(STATES.SHIPPING_METHOD.name().concat("_EDIT"), true);
                request.setAttribute(STATES.PAYMENT_METHOD.name().concat("_EDIT"), true);
                nextState = STATES.ORDER_REVIEW;
            }
        }

        request.setAttribute(STATE, nextState.name());

        return nextState.name();

    }

    /**
     * This method must be overiddes to validate the one checkout form
     *
     * @param action  the current action
     * @param formMap the form map
     * @param bundle  the resource bundle
     * @return true if the form is valid, false otherwise
     */
    /**
     * This method must be overiddes to validate the one checkout form
     *
     * @param action current action
     * @param currentState current state
     * @param formMap the form map
     * @param bundle  the resource bundle
     * @return true if the form is valid, false otherwise
     */
    protected boolean doValidForm(String action, String currentState, FormMap formMap, ResourceBundle bundle) {
        boolean result = true;

        String errorMessage = bundle.getString("checkout.mandatory.field");

        if (action.equals(ACTIONS.SELECT_ADDRESS.name()) && (currentState.equals(STATES.BILLING_ADDRESS.name()) || currentState.equals(STATES.SHIPPING_ADDRESS.name()))) {

            String addressId = formMap.getField("address").getValue();

            if (!"-1".equals(addressId)) {
                return result;
            }

            result = checkMandatoryField(formMap, "gender", errorMessage);
            result = result & checkMandatoryField(formMap, "firstname", errorMessage);
            result = result & checkMandatoryField(formMap, "lastname", errorMessage);
            result = result & checkMandatoryField(formMap, "email", errorMessage);
            result = result & checkMandatoryField(formMap, "streetaddress", errorMessage);
            result = result & checkMandatoryField(formMap, "postalcode", errorMessage);
            result = result & checkMandatoryField(formMap, "city", errorMessage);
            result = result & checkMandatoryField(formMap, "stateprovince", errorMessage);
            result = result & checkMandatoryField(formMap, "country", errorMessage);
        }

        return result;
    }


    /**
     * Create a customer registration from the onecheckout form
     *
     * @param formMap the form
     * @return a created customer registration
     */
    protected AddressIf createAddressForCustomer(FormMap formMap) {

        AddressIf address = new Address();


        FormField formField = formMap.getField("gender");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setGender(formField.getValue());
        }

        formField = formMap.getField("firstname");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setFirstName(formField.getValue());
        }

        formField = formMap.getField("lastname");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setLastName(formField.getValue());
        }

        formField = formMap.getField("email");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setEmailAddr(formField.getValue());
        }

        formField = formMap.getField("companyname");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setCompany(formField.getValue());
        }

        formField = formMap.getField("streetaddress");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setStreetAddress(formField.getValue());
        }

        formField = formMap.getField("suburb");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setSuburb(formField.getValue());
        }

        formField = formMap.getField("postalcode");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setPostcode(formField.getValue());
        }

        formField = formMap.getField("city");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setCity(formField.getValue());
        }

        formField = formMap.getField("stateprovince");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setState(formField.getValue());
        }

        formField = formMap.getField("country");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setCountryId(Integer.parseInt(formField.getValue()));
        }

        formField = formMap.getField("primarytelephone");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setTelephoneNumber(formField.getValue());
        }

        formField = formMap.getField("othertelephone");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            address.setTelephoneNumber1(formField.getValue());
        }

        return address;
    }


    /**
     * This method must be overrided if you implement a different onepagecheckout
     *
     * @return the onePageCheckout formMapFields
     */
    protected String[] getCheckoutFormMapFields() {
        return new String[]{"gender", "firstname", "lastname", "email", "day", "month", "year", "companyname"
                , "streetaddress", "suburb", "postalcode", "city", "stateprovince", "country", "primarytelephone"
                , "othertelephone", "faxnumber", "saveinaddressbook", "address"};
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
}
