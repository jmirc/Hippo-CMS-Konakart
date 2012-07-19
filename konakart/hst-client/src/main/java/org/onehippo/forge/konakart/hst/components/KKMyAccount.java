package org.onehippo.forge.konakart.hst.components;

import com.konakart.al.KKAppEng;
import com.konakart.app.KKException;
import com.konakart.appif.CustomerRegistrationIf;
import com.konakart.appif.ZoneIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormField;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.component.support.forms.FormUtils;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.utils.KKActionsConstants;
import org.onehippo.forge.konakart.hst.utils.KKComponentUtils;
import org.onehippo.forge.konakart.hst.utils.KKRegisterFormUtils;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static org.onehippo.forge.konakart.hst.utils.KKRegisterFormUtils.*;
import static org.onehippo.forge.konakart.hst.utils.KKUtil.checkMandatoryField;

public abstract class KKMyAccount extends KKBaseHstComponent {

    public static final String ALREADYEXITS = "alreadyExists";
    public static final String REGISTER_FAILED = "registerFailed";


    public static final String FORM = "form";
    public static final String REGISTER_MANDATORY_FIELD = "register.mandatory.field";
    public static final String NON_VALID_FORM = "NON_VALID_FORM";

    @Override
    public void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        KKComponentUtils.setLoginAttributes(request);

        String action = request.getParameter(KKActionsConstants.ACTION);

        if (isGuestCustomer(request)) {
            FormMap formMap = new FormMap();
            FormUtils.populate(request, formMap);
            request.setAttribute(FORM, formMap);

            if (StringUtils.equals(action, KKActionsConstants.ACTIONS.REGISTER.name())) {
                try {
                    request.setAttribute(COUNTRIES, KKServiceHelper.getKKEngineService().getKKAppEng(request).getAllCountries());

                    // Set the province if has been selected
                    FormField countryField = formMap.getField(COUNTRY);

                    if (countryField != null) {

                        int country = StringUtils.isNotBlank(countryField.getValue()) ? Integer.valueOf(countryField.getValue()) : -1;
                        ZoneIf[] zones = KKServiceHelper.getKKEngineService().getKKAppEng(request).getEng().getZonesPerCountry(country);

                        List<String> stateProvinces = new ArrayList<String>();

                        for (ZoneIf zone : zones) {
                            stateProvinces.add(zone.getZoneName());
                        }

                        request.setAttribute(PROVINCES, stateProvinces);
                    }
                } catch (KKException e) {
                    log.error("Failed to retrieve the list of provinces - {} ", e.toString());
                }

                deBeforeRenderRegisterAction(request, response);
            } else if (StringUtils.equals(action, KKActionsConstants.ACTIONS.CREATE_ACCOUNT.name())) {

                doBeforeRenderCreateAction(request, response);
            } else {
                doBeforeRenderNotLoginAction(request, response);
            }
        }
    }

    protected abstract List<String> getCreateFormMapFields();


    @Override
    public void doAction(HstRequest request, HstResponse response) throws HstComponentException {
        super.doAction(request, response);

        String action = KKUtil.getEscapedParameter(request, KKActionsConstants.ACTION);

        response.setRenderParameter(KKActionsConstants.ACTION, action);

        if (StringUtils.equals(action, KKActionsConstants.ACTIONS.CREATE_ACCOUNT.name())) {

            FormMap formMap = new FormMap(request, getCreateFormMapFields());

            if (!doValidForm(formMap, request, response)) {
                FormUtils.persistFormMap(request, response, formMap, null);
                response.setRenderParameter(NON_VALID_FORM, "true");
            } else { // the form is valid, we will create the customer
                KKRegisterFormUtils registerFormUtils = new KKRegisterFormUtils();
                KKAppEng kkAppEng = getKKAppEng(request);

                CustomerRegistrationIf customerRegistration = registerFormUtils.createCustomerRegistration(formMap);

                String username = formMap.getField(KKRegisterFormUtils.EMAIL).getValue();
                String password = formMap.getField(KKRegisterFormUtils.PASSWORD).getValue();

                // Set the password
                customerRegistration.setPassword(password);

                // Set the locale
                customerRegistration.setLocale(request.getLocale().toString());

                // Register the customer
                try {
                    // Set additional informations
                    doCallBeforeRegisterCustomer(customerRegistration, formMap);

                    // Register the customer
                    int customerId = kkAppEng.getEng().registerCustomer(customerRegistration);

                    // call after the customer is registered
                    doCallAfterRegisterCustomer(request, response, customerId);
                } catch (KKException e) {
                    log.error("Failed to register the customer with the email " + username, e);
                    FormUtils.persistFormMap(request, response, formMap, null);
                    response.setRenderParameter(REGISTER_FAILED, "true");
                }
            }
        }
    }




    /**
     * This method is used to valid if the register form is valid.
     * @param formMap the form map
     * @param request the hst request
     * @param response the hst response
     *
     * @return true if the form is valid, false otherwise
     */
    protected boolean doValidForm(FormMap formMap, HstRequest request, HstResponse response) {

        String errorMessage = null;

        try {
            errorMessage = ResourceBundle.getBundle("messages", request.getLocale()).getString(REGISTER_MANDATORY_FIELD);
        } catch (Exception e) {
            log.warn("Failed to retrieve the message with the key " + REGISTER_MANDATORY_FIELD + " within any ressources bundles.");
        }

        boolean result = checkMandatoryField(formMap, GENDER, errorMessage);
        result = result & checkMandatoryField(formMap, FIRSTNAME, errorMessage);
        result = result & checkMandatoryField(formMap, LASTNAME, errorMessage);
        result = result & checkMandatoryField(formMap, STREETADDRESS, errorMessage);
        result = result & checkMandatoryField(formMap, POSTALCODE, errorMessage);
        result = result & checkMandatoryField(formMap, CITY, errorMessage);
        result = result & checkMandatoryField(formMap, STATEPROVINCE, errorMessage);
        result = result & checkMandatoryField(formMap, COUNTRY, errorMessage);

        result = result & checkMandatoryField(formMap, EMAIL, errorMessage);
        result = result & checkMandatoryField(formMap, DATEOFBIRTH, errorMessage);

        // Valid if the a customer has already this email
        String emailAddress = formMap.getField(EMAIL).getValue();

        try {
            KKAppEng kkAppEng = getKKAppEng(request);

            // Check if the email already exists
            if (kkAppEng.getEng().doesCustomerExistForEmail(emailAddress)) {

                String password = formMap.getField(PASSWORD).getValue();

                // Try to logged-in
                boolean isLoggedIn = KKServiceHelper.getKKEngineService().logIn(request, response,
                        emailAddress, password);

                if (!isLoggedIn) {
                    // login has failed
                    result = false;
                    response.setRenderParameter(ALREADYEXITS, "true");
                }
            }
        } catch (KKException e) {
            result = false;
            log.error("Failed to validate if the email set during the checkout process already exists.", e);
            response.setRenderParameter(REGISTER_FAILED, "true");
        }

        result = result & checkMandatoryField(formMap, PASSWORD, errorMessage);
        result = result & checkMandatoryField(formMap, PASSWORD_CONFIRMATION, errorMessage);

        return result;
    }


    /**
     * This method is called to set any parameters or to do actions before rendering the JSP
     *
     * @param request the hst request
     * @param response the hst response
     */
    protected abstract void doBeforeRenderNotLoginAction(HstRequest request, HstResponse response);

    /**
     * This method is called to set any parameters or to do actions before rendering the JSP
     *
     * @param request the hst request
     * @param response the hst response
     */
    protected abstract void deBeforeRenderRegisterAction(HstRequest request, HstResponse response);

    /**
     * This method is called to set any parameters or to do actions before rendering the JSP
     *
     * @param request the hst request
     * @param response the hst response
     */
    protected abstract void doBeforeRenderCreateAction(HstRequest request, HstResponse response);

    /**
     * This method could be overrides to add additionnal information to the user
     *
     * @param customerRegistration the customer registration
     * @param formMap the form map
     */
    protected abstract void doCallBeforeRegisterCustomer(CustomerRegistrationIf customerRegistration,
                                                         FormMap formMap);

    /**
     * This method is called after the customer is registered
     * @param request the hst request
     * @param response the hst response
     * @param customerId id of the created customer
     */
    protected abstract void doCallAfterRegisterCustomer(HstRequest request, HstResponse response, int customerId);

}
