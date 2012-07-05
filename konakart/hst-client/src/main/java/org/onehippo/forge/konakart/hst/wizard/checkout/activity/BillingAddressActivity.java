/*
 * =========================================================
 * Hippo CMS - Konakart
 * https://bitbucket.org/jmirc/hippo-cms-konakart
 * =========================================================
 * Copyright 2012
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================================================
 */

package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import com.konakart.app.KKException;
import com.konakart.app.OrderStatusHistory;
import com.konakart.appif.CustomerRegistrationIf;
import com.konakart.appif.OrderIf;
import com.konakart.appif.OrderStatusHistoryIf;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutProcessContext;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutSeedData;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class BillingAddressActivity extends BaseAddressActivity {

    public static final String SHIPPING_ADDRESS = "shippingAddress";
    public static final String SELECT_SAME_SHIPPING_ADDRESS = "same";

    @Override
    public void doAction() throws ActivityException {
        super.doAction();

        CheckoutProcessContext checkoutProcessContext = (CheckoutProcessContext) processorContext;
        CheckoutSeedData seedData = checkoutProcessContext.getSeedData();

        String action = seedData.getAction();

        if (action.equals(KKCheckoutConstants.ACTIONS.SELECT.name())) {


            String sAddressId = KKUtil.getEscapedParameter(seedData.getRequest(), ADDRESS);
            String shippingAddress = KKUtil.getEscapedParameter(seedData.getRequest(), SHIPPING_ADDRESS);

            Integer addressId = -1;

            if (StringUtils.isNotEmpty(sAddressId)) {
                addressId = Integer.parseInt(sAddressId);
            }

            // User is already logged in - Create a new address
            if (StringUtils.isNotEmpty(sAddressId) && (StringUtils.equals(sAddressId, "-1"))) {
                try {
                    addressId = KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getCustomerMgr().
                            addAddressToCustomer(createAddressForCustomer());
                } catch (Exception e) {
                    setNextLoggedState(KKCheckoutConstants.STATES.INITIAL.name());
                    addMessage(GLOBALMESSAGE, seedData.getBundleAsString("checkout.failed.create.address"));
                    return;
                }
            } else if (StringUtils.isEmpty(sAddressId) && (isCheckoutAsGuest() || isCheckoutAsRegister())) { // User is not logged-in. User wants to checkout as guest.

                CustomerRegistrationIf customerRegistration = createCustomerRegistration();

                String username = formMap.getField(EMAIL).getValue();

                // Generate a random password.
                String password = String.valueOf(System.currentTimeMillis());

                if (isCheckoutAsRegister()) {
                    password = formMap.getField(PASSWORD).getValue();
                }

                // Set the password
                customerRegistration.setPassword(password);

                // Set the locale
                customerRegistration.setLocale(hstRequest.getLocale().toString());

                // Set additional informations
                addAdditionalInformationToCustomerRegistration(customerRegistration);

                try {
                    // Register the customer as guest
                    if (isCheckoutAsGuest()) {
                        kkAppEng.getEng().forceRegisterCustomer(customerRegistration);
                    } else { // Register the customer a real customer
                        kkAppEng.getEng().registerCustomer(customerRegistration);
                    }

                    // Logged-in
                    KKServiceHelper.getKKEngineService().loggedIn(hstRequest, hstResponse, username, password);
                } catch (KKException e) {
                    log.error("Failed to register a customer", e);
                    addMessage(GLOBALMESSAGE, seedData.getBundleAsString("checkout.failed.register.customer"));
                    return;
                }
            }

            // At this stage, the customer has been logged-in.
            // The checkout order can be created.
            createCheckoutOrder();

            // Set the billing address
            KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().setCheckoutOrderBillingAddress(addressId);

            if (shippingAddress.equals(SELECT_SAME_SHIPPING_ADDRESS)) {
                try {
                    KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().setCheckoutOrderShippingAddress(addressId);

                    // Skip the SHIPPING ADDRESS step because the customer has decided to use the
                    // same billing address
                    setNextLoggedState(KKCheckoutConstants.STATES.SHIPPING_METHOD.name());
                } catch (KKException e) {
                    log.error("Failed to set the shipping address", e);
                }
            }

            OrderIf checkoutOrder = KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().getCheckoutOrder();

            // Set the comment
            OrderStatusHistoryIf osh = new OrderStatusHistory();
            // TODO CAN SET COMMENTS
            osh.setComments("");
            OrderStatusHistoryIf[] oshArray = new OrderStatusHistoryIf[1];
            oshArray[0] = osh;
            osh.setUpdatedById(kkAppEng.getOrderMgr().getIdForUserUpdatingOrder(checkoutOrder));
            checkoutOrder.setStatusTrail(oshArray);
        }

        hstResponse.setRenderParameter(KKCheckoutConstants.ACTION, action);
    }

    @Override
    public void doAdditionalData() {
        super.doAdditionalData();

        CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

        List<String> acceptedStates = Arrays.asList(KKCheckoutConstants.STATES.SHIPPING_ADDRESS.name(), KKCheckoutConstants.STATES.SHIPPING_METHOD.name(),
                KKCheckoutConstants.STATES.PAYMENT_METHOD.name(), KKCheckoutConstants.STATES.ORDER_REVIEW.name());

        String state = seedData.getState();

        if (StringUtils.isNotEmpty(state) && acceptedStates.contains(state)) {
            hstRequest.getRequestContext().setAttribute(getAcceptState().concat("_EDIT"), true);
            hstRequest.setAttribute(getAcceptState().concat("_EDIT"), true);
        }

    }

    /**
     * This method could be overrides to add additionnal information to the user
     * @param customerRegistration the customer registration
     */
    protected void addAdditionalInformationToCustomerRegistration(CustomerRegistrationIf customerRegistration) {
        // nothing to add by default.
    }

    /**
     * Create a checkout order
     */
    private void createCheckoutOrder() {
        try {
            // Create an order object that we will use for the checkout process
            KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().createCheckoutOrder();

            // Get shipping quotes from the engine
            KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getOrderMgr().createShippingQuotes();

        } catch (Exception e) {
            log.error("A new Order could not be created", e);
        }
    }
}
