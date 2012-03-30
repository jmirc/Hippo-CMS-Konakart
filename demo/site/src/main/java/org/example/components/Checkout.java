package org.example.components;

import com.konakart.app.CustomerRegistration;
import com.konakart.app.KKException;
import com.konakart.appif.CustomerRegistrationIf;
import com.konakart.appif.ZoneIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormField;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.util.HstResponseUtils;
import org.joda.time.DateTime;
import org.onehippo.forge.konakart.hst.components.KKCheckout;
import org.onehippo.forge.konakart.hst.utils.KKCustomerEventMgr;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Checkout extends KKCheckout {

    private static final String COUNTRIES = "countries";
    private static final String PROVINCES = "provinces";

    private static final String NO_STATE = "NONE";
    private static final String ASK_PASSWORD_STATE = "ASK_PASSWORD";
    private static final String LOGGED_STATE = "LOGGED";

    @Override
    protected String[] getCheckoutFormMapFields() {
        return new String[] {"gender", "firstname", "lastname", "email", "day", "month", "year", "companyname"
                , "streetaddress", "suburb", "postalcode", "city", "stateprovince", "country", "primarytelephone"
                , "othertelephone", "faxnumber", "password"};
    }

    @Override
    protected void doBeforeRender(String nextState, FormMap formMap, HstRequest request, HstResponse response) {
        try {

            if (nextState.equals(NO_STATE)) {

                // Insert event
                eventMgr.insertCustomerEvent(kkAppEng, KKCustomerEventMgr.ACTION_ENTER_CHECKOUT);


                request.setAttribute(COUNTRIES, kkAppEng.getAllCountries());

                // Set the province if has been selected
                FormField countryField = formMap.getField("country");

                if (countryField != null) {
                    int country = StringUtils.isNotBlank(countryField.getValue()) ? Integer.valueOf(countryField.getValue()) : -1;
                    ZoneIf[] zones = kkAppEng.getEng().getZonesPerCountry(country);

                    List<String> stateProvinces = new ArrayList<String>();

                    for (ZoneIf zone : zones) {
                        stateProvinces.add(zone.getZoneName());
                    }

                    request.setAttribute(PROVINCES, stateProvinces);
                }
            } else if (nextState.equals(LOGGED_STATE)) {
                HstResponseUtils.sendRedirect(request, response, "/orderConfirmation");
            }
        } catch (KKException e) {
            log.warn("Failed to initialize the checkout page - {}", e.toString());
        }
    }

    @Override
    protected void doAction(String action, FormMap formMap, HstRequest request, HstResponse response) {

        // The form is validated. Now we need to validate if the email already exists or not.
        // If the email exists we will force the customer to logged-in
        // If the email does not exist, we will force the customer to register
        if (action.equals(NO_STATE)) {
            String email = formMap.getField("email").getValue();

            try {
                if (kkAppEng.getEng().doesCustomerExistForEmail(email)) {
                    // ask the password
                    response.setRenderParameter(STATE, ASK_PASSWORD_STATE);
                } else {
                    kkAppEng.getEng().forceRegisterCustomer(createCustomerRegistration(formMap));
                    String username = formMap.getField("email").getValue();
                    String password = String.valueOf(System.currentTimeMillis());

                    super.loggedIn(request, response, username, password);

                    response.setRenderParameter(STATE, LOGGED_STATE);
                }
            } catch (KKException e) {
                log.error("Failed to validate if the customer exists.", e);
            }
        } else if (action.equals(ASK_PASSWORD_STATE)) {
            String username = formMap.getField("email").getValue();
            String password = formMap.getField("password").getValue();

            if (!super.loggedIn(request, response, username, password)) {
                ResourceBundle bundle = ResourceBundle.getBundle("messages", request.getLocale());

                formMap.addMessage("password", bundle.getString("onepagecheckout.invalid.password"));
                response.setRenderParameter(STATE, ASK_PASSWORD_STATE);
            } else {
                response.setRenderParameter(STATE, LOGGED_STATE);
            }

        }
    }

    @Override
    protected String getNextState(String currentState) {

        if (!isGuestCustomer()) {
            return LOGGED_STATE;
        }


        if (StringUtils.isEmpty(currentState)) {
            return NO_STATE;
        }

        if (currentState.equals(NO_STATE)) {
            return ASK_PASSWORD_STATE;
        }

        if (currentState.equals(ASK_PASSWORD_STATE)) {
            return ASK_PASSWORD_STATE;
        }

        return LOGGED_STATE;

    }

    /**
     * This method must be overiddes to validate the one checkout form
     * @param formMap the form map
     * @param bundle the resource bundle
     * @return true if the form is valid, false otherwise
     */
    protected boolean doValidForm(String currentState, FormMap formMap, ResourceBundle bundle) {
        boolean result = true;

        String errorMessage = bundle.getString("onepagecheckout.mandatory.field");

        if (currentState.equals(NO_STATE)) {
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

        if (currentState.equals(ASK_PASSWORD_STATE)) {
            result = checkMandatoryField(formMap, "password", errorMessage);
        }

        return result;
    }


    /**
     * Create a customer registration from the onecheckout form
     * @param formMap the form
     * @return a created customer registration
     */
    protected CustomerRegistrationIf createCustomerRegistration(FormMap formMap) {

        CustomerRegistrationIf customerRegistration = new CustomerRegistration();


        FormField formField = formMap.getField("gender");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setGender(formField.getValue());
        }

        formField = formMap.getField("firstname");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setFirstName(formField.getValue());
        }

        formField = formMap.getField("lastname");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setLastName(formField.getValue());
        }

        formField = formMap.getField("email");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setEmailAddr(formField.getValue());
        }

        try {
            int day = Integer.parseInt(formMap.getField("day").getValue());
            int month = Integer.parseInt(formMap.getField("month").getValue()) - 1;
            int year = Integer.parseInt(formMap.getField("year").getValue()) - 1900;
            DateTime dateTime = new DateTime(year, month, day, 0, 0);
            customerRegistration.setBirthDate(dateTime.toGregorianCalendar());
        } catch (NumberFormatException e) {
            // No birth date
        }

        formField = formMap.getField("companyname");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setCompany(formField.getValue());
        }

        formField = formMap.getField("streetaddress");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setStreetAddress(formField.getValue());
        }

        formField = formMap.getField("suburb");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setSuburb(formField.getValue());
        }

        formField = formMap.getField("postalcode");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setPostcode(formField.getValue());
        }

        formField = formMap.getField("city");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setCity(formField.getValue());
        }

        formField = formMap.getField("stateprovince");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setState(formField.getValue());
        }

        formField = formMap.getField("country");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setCountryId(Integer.parseInt(formField.getValue()));
        }

        formField = formMap.getField("primarytelephone");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setTelephoneNumber(formField.getValue());
        }

        formField = formMap.getField("othertelephone");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setTelephoneNumber1(formField.getValue());
        }

        formField = formMap.getField("faxnumber");
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setFaxNumber(formField.getValue());
        }

        String randomPassword = String.valueOf(System.currentTimeMillis());
        customerRegistration.setPassword(randomPassword);

        customerRegistration.setLocale(kkAppEng.getLocale());

        return customerRegistration;


    }

}
